package com.techgeekme.sis;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.techgeekme.sis.sync.SisSyncAdapter;

public class Login extends AppCompatActivity {
    private static final String LOG_TAG = Login.class.getSimpleName();
    private static final String TAG_DIALOG = "dialog_loading";
    private EditText mDobEditText;
    private EditText mUsnEditText;
    private LoadingDialogFragment mLoadingDialogFragment;
    private FragmentManager mFragmentManager;
    private SyncStatusReceiver mSyncStatusReceiver;
    private CoordinatorLayout mCoordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_login);
        setSupportActionBar(toolbar);

        mSyncStatusReceiver = new SyncStatusReceiver();

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        mFragmentManager = getFragmentManager();

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
                v.clearFocus();
                DialogFragment datePickerFragment = new DatePickerDialogFragment();
                datePickerFragment.show(mFragmentManager, "date_picker");
            }
        });

        mLoadingDialogFragment = (LoadingDialogFragment) mFragmentManager.findFragmentByTag(TAG_DIALOG);
        if (mLoadingDialogFragment == null) {
            mLoadingDialogFragment = new LoadingDialogFragment();
        }
    }

    private void displaySis() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(SisSyncAdapter.ACTION_SYNC_FINSISHED);
        LocalBroadcastManager.getInstance(this).registerReceiver(mSyncStatusReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mSyncStatusReceiver);
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
        mLoadingDialogFragment = new LoadingDialogFragment();
        mLoadingDialogFragment.show(mFragmentManager, TAG_DIALOG);
        String usn = mUsnEditText.getText() + "";
        String dob = mDobEditText.getText() + "";
        Utility.storeLoginDetails(this, usn, dob);
        SisSyncAdapter.initializeSyncAdapter(this);
    }

    private void setmDobEditText(String dob) {
        mDobEditText.setText(dob);
    }

    private void showOrHideDialog(boolean loggingIn) {
        if (loggingIn && mLoadingDialogFragment.getDialog() == null) {
            mLoadingDialogFragment.show(mFragmentManager, TAG_DIALOG);
        }
//        else {
//            if (mLoadingDialogFragment != null) {
//                mLoadingDialogFragment.dismiss();
//            }
//        }
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

    public class SyncStatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mLoadingDialogFragment.getDialog() != null) {
                mLoadingDialogFragment.dismissAllowingStateLoss();
            }
            switch (intent.getStringExtra(SisSyncAdapter.EXTRA_SYNC_STATUS)) {
                case SisSyncAdapter.SYNC_FAIL:
                    int statusCode = intent.getIntExtra(SisSyncAdapter.EXTRA_ERROR_CODE, -1);
                    Utility.showErrorSnackBar(mCoordinatorLayout, statusCode);
                    return;
                case SisSyncAdapter.SYNC_SUCCESS:
                    Intent homeActivity = new Intent(Login.this, HomeActivity.class);
                    startActivity(homeActivity);
                    finish();
                    return;
                default:
                    Log.e(LOG_TAG, "Invalid intent extra for SyncStatusReceiver");
            }

        }
    }
}
