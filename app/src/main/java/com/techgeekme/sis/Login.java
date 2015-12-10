package com.techgeekme.sis;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class Login extends AppCompatActivity {
    private Button mLoginButton;
    private EditText mDobEditText;
    private EditText mUsnEditText;
    private View mCoordinatorLayout;
    private ProgressDialog mProgressDialog;
    private DatabaseManager DBobj;
    public static final String PREFS_NAME = "Credentials";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        DBobj = new DatabaseManager(this);
        mCoordinatorLayout = findViewById(R.id.coordinator_layout);
        mLoginButton = (Button) findViewById(R.id.login_button);
        mDobEditText = (EditText) findViewById(R.id.dob_edit_text);
        mUsnEditText = (EditText) findViewById(R.id.usn_edit_text);
        mDobEditText.setFocusable(false);
        mDobEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DialogFragment datePickerFragment = new DatePickerDialogFragment();
                datePickerFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Logging in");
        mProgressDialog.setCancelable(false);
    }

    public void displaySis(Student student) {
        mProgressDialog.dismiss();
        Intent mIntent = new Intent(this, Home.class);
        mIntent.putExtra("student_object", student);
        startActivity(mIntent);
        finish();
    }

    public void login(View v) {
        SharedPreferences credentialsSharedPref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor credentialsEditor = credentialsSharedPref.edit();
        credentialsEditor.putString("usn", mUsnEditText.getText() + "");
        credentialsEditor.putString("dob", mDobEditText.getText() + "");
        credentialsEditor.commit();
        mProgressDialog.show();
        mLoginButton.setEnabled(false);
        ErrorListener el = new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                Log.e("Login", error.toString());
                if (error.getClass() == NoConnectionError.class) {
                    Snackbar.make(mCoordinatorLayout, "Check your internet connection and try again", Snackbar.LENGTH_INDEFINITE).show();
                } else if (error.networkResponse.statusCode == 504) {
                    Snackbar.make(mCoordinatorLayout, "MSRIT SIS Server is down, try again later", Snackbar.LENGTH_INDEFINITE).show();
                } else if (error.networkResponse.statusCode == 500) {
                    Snackbar.make(mCoordinatorLayout, "Oops! Something went wrong, try again later", Snackbar.LENGTH_INDEFINITE).show();
                } else if (error.networkResponse.statusCode == 401) {
                    Snackbar.make(mCoordinatorLayout, "Incorrect USN or DOB", Snackbar.LENGTH_INDEFINITE).show();
                }
                mLoginButton.setEnabled(true);
            }
        };
        String url = getString(R.string.server_url) + "?usn=" + mUsnEditText.getText() + "&dob=" + mDobEditText.getText();
        Request<byte[]> req = new Request<byte[]>(Method.GET, url, el) {

            @Override
            protected Response<byte[]> parseNetworkResponse(NetworkResponse networkResponse) {
                return Response.success(networkResponse.data, HttpHeaderParser.parseCacheHeaders(networkResponse));
            }

            @Override
            protected void deliverResponse(byte[] responseBytes) {
                ByteArrayInputStream bais = new ByteArrayInputStream(responseBytes);
                ObjectInputStream ois;
                Student s;
                try {
                    ois = new ObjectInputStream(bais);
                    s = (Student) ois.readObject();
                    Log.i("login", "getting student object");
                    DBobj.deleteAll();
                    DBobj.addStudent(s.courses);
                    displaySis(s);
                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                }
            }
        };
        req.setRetryPolicy(new DefaultRetryPolicy(100000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = VolleyApplication.getInstance().getRequestQueue();
        requestQueue.add(req);
    }


    public void setmDobEditText(String dob) {
        mDobEditText.setText(dob);
    }


    public static class DatePickerDialogFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, 1995, 0, 1);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            Login l = (Login) getActivity();
            month += 1;
            String paddedMonth = String.format("%02d", month);
            String paddedDay = String.format("%02d", day);
            StringBuilder dobString = new StringBuilder().append(year).append("-").append(paddedMonth).append("-").append(paddedDay);
            l.setmDobEditText(dobString.toString());
        }

    }


}
