package com.techgeekme.sis;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.techgeekme.sis.sync.SisSyncAdapter;

/**
 * Created by anirudh on 20/02/16.
 */

public class Utility {
    public static String getUsnFromSharedPref(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(context.getString(R.string.pref_key_usn), null);
    }

    public static String getDobFromSharedPref(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(context.getString(R.string.pref_key_dob), null);
    }

    public static int getUpdateFrequencyFromSharedPref(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return Integer.parseInt(
                sharedPref.getString(context.getString(R.string.pref_key_update_frequency),
                        context.getString(R.string.pref_default_update_frequency)));
    }

    public static void showErrorSnackBar(View view, int statusCode) {
        String message = null;
        if (statusCode == SisSyncAdapter.ERROR_OTHER) {
            message = "Oops! Something went wrong, try again later";
        } else if (statusCode == SisSyncAdapter.ERROR_NO_CONNECTION) {
            message = "Check your internet connection and try again";
        } else if (statusCode == SisSyncAdapter.ERROR_TIMEOUT) {
            message = "Could not connect to server, try again later";
        } else if (statusCode == 504) {
            message = "MSRIT SIS Server is down, try again later";
        } else if (statusCode == 500) {
            message = "Oops! Something went wrong, try again later";
        } else if (statusCode == 401) {
            message = "Incorrect USN or DOB";
        }
        Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE).show();
    }

    public static void showSuccessSnackBar(View view) {
        String message = "Refreshed";
        Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE).show();
    }

    public static void setLoggedIn(Context context, boolean loggedIn) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(context.getString(R.string.pref_key_logged_in), loggedIn);
        editor.commit();
    }

    public static void storeName(Context context, String name) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(context.getString(R.string.pref_key_name), name);
        editor.commit();
    }

    public static boolean isLoggedIn(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getBoolean(context.getString(R.string.pref_key_logged_in), false);
    }

    public static void storeLoginDetails(Context context, String usn, String dob) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(context.getString(R.string.pref_key_usn), usn);
        editor.putString(context.getString(R.string.pref_key_dob), dob);
        editor.commit();
    }
}
