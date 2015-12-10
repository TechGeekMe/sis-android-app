package com.techgeekme.sis;

import android.support.design.widget.Snackbar;
import android.view.View;

import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;

/**
 * Created by anirudh on 10/12/15.
 */
public abstract class StudentFetcherErrorListener implements Response.ErrorListener {
    private View mView;

    public StudentFetcherErrorListener(View v) {
        mView = v;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if (error.getClass() == NoConnectionError.class) {
            Snackbar.make(mView, "Check your internet connection and try again", Snackbar.LENGTH_INDEFINITE).show();
        } else if (error.networkResponse.statusCode == 504) {
            Snackbar.make(mView, "MSRIT SIS Server is down, try again later", Snackbar.LENGTH_INDEFINITE).show();
        } else if (error.networkResponse.statusCode == 500) {
            Snackbar.make(mView, "Oops! Something went wrong, try again later", Snackbar.LENGTH_INDEFINITE).show();
        } else if (error.networkResponse.statusCode == 401) {
            Snackbar.make(mView, "Incorrect USN or DOB", Snackbar.LENGTH_INDEFINITE).show();
        }
        onStudentFetcherError();
    }

    public abstract void onStudentFetcherError();
}
