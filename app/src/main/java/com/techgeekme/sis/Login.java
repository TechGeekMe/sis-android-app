package com.techgeekme.sis;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.ArrayList;

public class Login extends AppCompatActivity {
    private Button mLoginButton;
    private EditText mDobEditText;
    private EditText mUsnEditText;
    private View mCoordinatorLayout;
    private ProgressDialog mProgressDialog;
    private String usn;
    private String dob;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
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

    private void displaySis() {
        mProgressDialog.dismiss();
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
        finish();
    }

    private void storeLoginDetails(String usn, String dob, String name) {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("usn", usn);
        editor.putString("dob", dob);
        editor.putString("name", name);
        editor.apply();

    }

    private void storeCourses(ArrayList<Course> courses) {
        DatabaseManager dm = new DatabaseManager(this);
        dm.putCourses(courses);
    }

    public void login(View v) {
        mProgressDialog.show();
        mLoginButton.setEnabled(false);
        StudentFetcherErrorListener el = new StudentFetcherErrorListener(mCoordinatorLayout) {
            @Override
            public void onStudentFetcherError() {
                mProgressDialog.dismiss();
                mLoginButton.setEnabled(true);
            }
        };

        usn = mUsnEditText.getText() + "";
        dob = mDobEditText.getText() + "";
        String url = getString(R.string.server_url) + "?usn=" + usn + "&dob=" + dob;

        StudentFetcher studentFetcher = new StudentFetcher(url, el) {
            @Override
            public void onStudentResponse(Student s) {
                storeLoginDetails(usn, dob, s.studentName);
                storeCourses(s.courses);
                displaySis();
            }
        };

        studentFetcher.fetchStudent();
    }


    private void setmDobEditText(String dob) {
        mDobEditText.setText(dob);
    }


    public static class DatePickerDialogFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, 1995, 0, 1);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            month += 1;
            String paddedMonth = String.format("%02d", month);
            String paddedDay = String.format("%02d", day);
            StringBuilder dobString = new StringBuilder().append(year).append("-").append(paddedMonth).append("-").append(paddedDay);
            Login l = (Login) getActivity();
            l.setmDobEditText(dobString.toString());
        }

    }


}
