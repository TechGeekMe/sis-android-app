package com.techgeekme.sis;

import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

/**
 * Created by anirudh on 10/12/15.
 */
public abstract class StudentFetcherErrorListener implements Response.ErrorListener {

    @Override
    public void onErrorResponse(VolleyError error) {
        View rootView = ((ViewGroup) SisApplication.getInstance().currentActivityWeakReference.get().findViewById(android.R.id.content)).getChildAt(0);
        String message = null;
        if (error.getClass() == NoConnectionError.class) {
            message = "Check your internet connection and try again";
        } else if (error.getClass() == TimeoutError.class) {
            message = "Could not connect to server, try again later";
        } else if (error.networkResponse.statusCode == 504) {
            message = "MSRIT SIS Server is down, try again later";
        } else if (error.networkResponse.statusCode == 500) {
            message = "Oops! Something went wrong, try again later";
        } else if (error.networkResponse.statusCode == 401) {
            message = "Incorrect USN or DOB";
        }
        Snackbar.make(rootView, message, Snackbar.LENGTH_INDEFINITE).show();
        onStudentFetcherError();
    }

    public abstract void onStudentFetcherError();
}
