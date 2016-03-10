package com.techgeekme.sis.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.techgeekme.sis.Course;
import com.techgeekme.sis.R;
import com.techgeekme.sis.SisApplication;
import com.techgeekme.sis.Student;
import com.techgeekme.sis.StudentFetcher;
import com.techgeekme.sis.Utility;
import com.techgeekme.sis.data.SisContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.techgeekme.sis.LogUtils.LOGD;
import static com.techgeekme.sis.LogUtils.LOGE;

/**
 * Created by anirudh on 20/02/16.
 */
public class SisSyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String LOG_TAG = SisSyncAdapter.class.getSimpleName();
    public static final String ACTION_SYNC_FINSISHED = "com.techgeekme.com.action.SYNC_FINISHED";
    public static final String EXTRA_SYNC_STATUS = "sync_status";
    public static final String EXTRA_ERROR_CODE = "error_code";
    public static final String SYNC_SUCCESS = "success";
    public static final String SYNC_FAIL = "fail";
    public static final int ERROR_TIMEOUT = 1;
    public static final int ERROR_NO_CONNECTION = 2;
    public static final int ERROR_OTHER = 3;
    private static final int HOURS_TO_SECONDS = 3600;

    public SisSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();

        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);

        boolean pending = ContentResolver.isSyncPending(account,
                authority);
        if (pending) {
            LOGD(LOG_TAG, "Warning: sync is PENDING. Will cancel.");
        }
        boolean active = ContentResolver.isSyncActive(account,
                authority);
        if (active) {
            LOGD(LOG_TAG, "Warning: sync is ACTIVE. Will cancel.");
        }

        if (pending || active) {
            LOGD(LOG_TAG, "Ignoring new sync request");
            return;
        }

        LOGD(LOG_TAG, "Requesting sync now.");
        ContentResolver.requestSync(account, authority, bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * setupAccount method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
        }
        return newAccount;
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context) {
        int syncInterval = Utility.getUpdateFrequencyFromSharedPref(context) * HOURS_TO_SECONDS;
        int flexTime = syncInterval / 3;
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    private static void setupAccount(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        configurePeriodicSync(context);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void stopAutoSync(Context context) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        ContentResolver.setSyncAutomatically(account, authority, false);
    }

    public static void initializeSyncAdapter(Context context) {
        Account account = getSyncAccount(context);
        setupAccount(account, context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        LOGD(LOG_TAG, "onPerformSync Called.");

        String usn = Utility.getUsnFromSharedPref(getContext());
        String dob = Utility.getDobFromSharedPref(getContext());
        String url = getContext().getString(R.string.server_url) + "?usn=" + usn + "&dob=" + dob;
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        StudentFetcher studentFetcher = new StudentFetcher(url, future, future);
        RequestQueue requestQueue = SisApplication.getInstance().getRequestQueue();
        requestQueue.add(studentFetcher);
        JSONObject studentJson;
        Student s = new Student();
        Intent syncIntent = new Intent(ACTION_SYNC_FINSISHED);
        try {
            studentJson = future.get(70, TimeUnit.SECONDS);
            s.studentName = studentJson.getString("name");
            s.usn = studentJson.getString("usn");
            JSONArray coursesJsonArray = studentJson.getJSONArray("courses");
            for (int i = 0; i < coursesJsonArray.length(); i++) {
                JSONObject course = coursesJsonArray.getJSONObject(i);
                Course c = new Course();
                c.courseCode = course.getString("code");
                c.courseName = course.getString("name");
                JSONObject attendanceJson = course.getJSONObject("attendance");
                c.attendancePercent = attendanceJson.getString("percentage");
                c.classesAttended = attendanceJson.getString("attended");
                c.classesAbsent = attendanceJson.getString("absent");
                c.classesRemaining = attendanceJson.getString("remaining");
                c.classesHeld = String.valueOf(Integer.parseInt(c.classesAbsent) + Integer.parseInt(c.classesAttended));
                JSONArray assignmentsJsonArray = course.getJSONArray("assignments");
                for (int j = 0; j < assignmentsJsonArray.length(); j++) {
                    String assignment = assignmentsJsonArray.getString(j);
                    c.assignments.add(assignment);
                }
                JSONArray testsJsonArray = course.getJSONArray("tests");
                for (int j = 0; j < testsJsonArray.length(); j++) {
                    String test = testsJsonArray.getString(j);
                    c.tests.add(test);
                }
                s.courses.add(c);
            }
            Utility.storeName(getContext(), s.studentName);
            deleteCourses();
            storeCourses(s.courses);
            Utility.setLoggedIn(getContext(), true);
            syncIntent.putExtra(EXTRA_SYNC_STATUS, SYNC_SUCCESS);
        } catch (InterruptedException | ExecutionException | TimeoutException | JSONException e) {
            LOGE(LOG_TAG, e.toString());
            syncIntent.putExtra(EXTRA_SYNC_STATUS, SYNC_FAIL);
            Throwable cause = e.getCause();
            if (cause instanceof VolleyError) {
                VolleyError volleyError = (VolleyError) cause;
                if (volleyError.getClass() == NoConnectionError.class) {
                    syncIntent.putExtra(EXTRA_ERROR_CODE, ERROR_NO_CONNECTION);
                } else if (volleyError.getClass() == TimeoutError.class) {
                    syncIntent.putExtra(EXTRA_ERROR_CODE, ERROR_TIMEOUT);
                } else if (volleyError.getClass() == ParseError.class) {
                    syncIntent.putExtra(EXTRA_ERROR_CODE, ERROR_OTHER);
                } else {
                    // TODO: Null pointer exception possible
                    syncIntent.putExtra(EXTRA_ERROR_CODE, volleyError.networkResponse.statusCode);
                }
            } else if (e instanceof TimeoutException) {
                syncIntent.putExtra(EXTRA_ERROR_CODE, ERROR_TIMEOUT);
            } else {
                syncIntent.putExtra(EXTRA_ERROR_CODE, ERROR_OTHER);
            }
        } finally {
            LocalBroadcastManager.getInstance(getContext()).sendBroadcast(syncIntent);
        }
    }

    private void deleteCourses() {
        getContext().getContentResolver().delete(SisContract.ALL_TABLES_CONTENT_URI, null, null);
    }

    private void storeCourses(ArrayList<Course> courses) {

        ArrayList<ContentValues> courseCVArrayList = new ArrayList<>(courses.size());
        ArrayList<ContentValues> testCVArrayList = new ArrayList<>();
        ArrayList<ContentValues> assignmentCVArrayList = new ArrayList<>();
        for (int i = 0; i < courses.size(); i++) {
            ContentValues courseValues = new ContentValues();
            Course course = courses.get(i);
            courseValues.put(SisContract.CourseEntry.COLUMN_COURSE_CODE, course.courseCode);
            courseValues.put(SisContract.CourseEntry.COLUMN_COURSE_NAME, course.courseName);
            courseValues.put(SisContract.CourseEntry.COLUMN_ATTENDANCE_PERCENT, course.attendancePercent);
            courseValues.put(SisContract.CourseEntry.COLUMN_CLASSES_HELD, course.classesHeld);
            courseValues.put(SisContract.CourseEntry.COLUMN_CLASSES_ATTENDED, course.classesAttended);
            courseValues.put(SisContract.CourseEntry.COLUMN_CLASSES_ABSENT, course.classesAbsent);
            courseValues.put(SisContract.CourseEntry.COLUMN_CLASSES_REMAINING, course.classesRemaining);
            courseCVArrayList.add(courseValues);
            ArrayList<String> tests = course.tests;
            for (int j = 0; j < tests.size(); j++) {
                ContentValues testValues = new ContentValues();
                testValues.put(SisContract.TestEntry.COLUMN_COURSE_KEY, course.courseCode);
                testValues.put(SisContract.TestEntry.COLUMN_TEST_NUMBER, j);
                testValues.put(SisContract.TestEntry.COLUMN_MARKS, tests.get(j));
                testCVArrayList.add(testValues);
            }

            ArrayList<String> assignments = course.assignments;
            for (int j = 0; j < assignments.size(); j++) {
                ContentValues assignmentValues = new ContentValues();
                assignmentValues.put(SisContract.AssignmentEntry.COLUMN_COURSE_KEY, course.courseCode);
                assignmentValues.put(SisContract.AssignmentEntry.COLUMN_TEST_NUMBER, j);
                assignmentValues.put(SisContract.AssignmentEntry.COLUMN_MARKS, assignments.get(j));
                assignmentCVArrayList.add(assignmentValues);
            }
        }
        ContentValues[] courseCVs = courseCVArrayList.toArray(new ContentValues[courseCVArrayList.size()]);
        ContentValues[] testCVs = testCVArrayList.toArray(new ContentValues[testCVArrayList.size()]);
        ContentValues[] assignmentCVs = assignmentCVArrayList.toArray(new ContentValues[assignmentCVArrayList.size()]);

        getContext().getContentResolver().bulkInsert(SisContract.CourseEntry.CONTENT_URI, courseCVs);
        getContext().getContentResolver().bulkInsert(SisContract.TestEntry.CONTENT_URI, testCVs);
        getContext().getContentResolver().bulkInsert(SisContract.AssignmentEntry.CONTENT_URI, assignmentCVs);

    }
}
