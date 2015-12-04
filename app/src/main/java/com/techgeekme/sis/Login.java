package com.techgeekme.sis;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        mLoginButton = (Button) findViewById(R.id.login_button);
        mDobEditText = (EditText) findViewById(R.id.dob_edit_text);
        mUsnEditText = (EditText) findViewById(R.id.usn_edit_text);
        // Prevents keyboard from popping up momentarily
        mDobEditText.setInputType(EditorInfo.TYPE_NULL);
        mDobEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    DialogFragment datePickerFragment = new DatePickerDialogFragment();
                    datePickerFragment.show(getSupportFragmentManager(), "datePicker");
                }
                v.clearFocus();
            }
        });
        mDobEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DialogFragment datePickerFragment = new DatePickerDialogFragment();
                datePickerFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
    }
    public void displaySis(Student student) {
        Intent mIntent = new Intent(this, Home.class);
        mIntent.putExtra("student_object", student);
        startActivity(mIntent);
    }
    public void login(View v) {
        mLoginButton.setEnabled(false);
        ErrorListener el = new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Login", error.toString());
                if (error.networkResponse.statusCode == 504) {
                    Toast.makeText(Login.this, "MSRIT SIS Server is down, try again later", Toast.LENGTH_SHORT).show();
                } else if (error.networkResponse.statusCode == 500) {
                    Toast.makeText(Login.this, "Oops! Something went wrong, try again later", Toast.LENGTH_SHORT).show();
                } else if (error.networkResponse.statusCode == 401) {
                    Toast.makeText(Login.this, "Wrong USN or DOB", Toast.LENGTH_SHORT).show();
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
