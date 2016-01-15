package com.techgeekme.sis;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/*
* TODO Find out the best way to handle orientation change during async tasks
* TODO Dismiss login even after orientation change
*/
public class Login extends AppCompatActivity {
    private EditText mDobEditText;
    private EditText mUsnEditText;
    private String usn;
    private String dob;
    private LoadingDialogFragment loadingDialogFragment;
    private FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        mDobEditText = (EditText) findViewById(R.id.dob_edit_text);
        mUsnEditText = (EditText) findViewById(R.id.usn_edit_text);
        mDobEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mDobEditText.performClick();
                }
            }
        });
        mDobEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DialogFragment datePickerFragment = new DatePickerDialogFragment();
                datePickerFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
        fm = getSupportFragmentManager();
        loadingDialogFragment = new LoadingDialogFragment();
        SisApplication.getInstance().currentActivityWeakReference = new WeakReference<Activity>(this);
    }

    private void displaySis() {
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
        String usnPatternString = "^1[Mm][Ss]\\d\\d[A-Za-z][A-Za-z]\\d\\d\\d$";
        if (!mUsnEditText.getText().toString().matches(usnPatternString)) {
            mUsnEditText.requestFocus();
            mUsnEditText.setError("Invalid USN");
            return;
        }

        if (TextUtils.isEmpty(mDobEditText.getText())) {
            mDobEditText.setError("Enter DOB");
            return;
        }

        loadingDialogFragment.show(fm, "Hello");
        usn = mUsnEditText.getText() + "";
        dob = mDobEditText.getText() + "";
        String url = getString(R.string.server_url) + "?usn=" + usn + "&dob=" + dob;

        StudentFetcher studentFetcher = new StudentFetcher(url, new LoginStudentFetcherErrorListener()) {
            @Override
            public void onStudentResponse(Student s) {
                storeLoginDetails(usn, dob, s.studentName);
                storeCourses(s.courses);
                loadingDialogFragment.dismiss();
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
            ++month;
            String paddedMonth = String.format("%02d", month);
            String paddedDay = String.format("%02d", day);
            StringBuilder dobString = new StringBuilder().append(year).append("-").append(paddedMonth).append("-").append(paddedDay);
            Login l = (Login) getActivity();
            l.setmDobEditText(dobString.toString());
        }

    }

    private class LoginStudentFetcherErrorListener extends StudentFetcherErrorListener {
        @Override
        public void onStudentFetcherError() {
            loadingDialogFragment.dismiss();
        }
    }

}
