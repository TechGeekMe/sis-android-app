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

    public StudentFetcherErrorListener() {
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        View rootView = ((ViewGroup) SisApplication.getInstance().currentActivityWeakReference.get().findViewById(android.R.id.content)).getChildAt(0);
        if (error.getClass() == NoConnectionError.class) {
            Snackbar.make(rootView, "Check your internet connection and try again", Snackbar.LENGTH_INDEFINITE).show();
        } else if (error.getClass() == TimeoutError.class) {
            Snackbar.make(rootView, "Could not connect to server, try again later", Snackbar.LENGTH_INDEFINITE).show();
        } else if (error.networkResponse.statusCode == 504) {
            Snackbar.make(rootView, "MSRIT SIS Server is down, try again later", Snackbar.LENGTH_INDEFINITE).show();
        } else if (error.networkResponse.statusCode == 500) {
            Snackbar.make(rootView, "Oops! Something went wrong, try again later", Snackbar.LENGTH_INDEFINITE).show();
        } else if (error.networkResponse.statusCode == 401) {
            Snackbar.make(rootView, "Incorrect USN or DOB", Snackbar.LENGTH_INDEFINITE).show();
        }
        onStudentFetcherError();
    }

    public abstract void onStudentFetcherError();
}
