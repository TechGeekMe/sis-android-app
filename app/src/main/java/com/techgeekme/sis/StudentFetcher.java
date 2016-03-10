package com.techgeekme.sis;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;

/**
 * Created by anirudh on 10/12/15.
 */
public class StudentFetcher extends JsonObjectRequest {

    private Listener mListener;

    public StudentFetcher(String url, Listener listener, ErrorListener errorListener) {
        super(url, listener, errorListener);
        mListener = listener;
        setRetryPolicy(new DefaultRetryPolicy(70000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public void fetchStudent() {
        RequestQueue requestQueue = SisApplication.getInstance().getRequestQueue();
        requestQueue.add(this);

    }


}



