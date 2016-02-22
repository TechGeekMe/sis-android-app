package com.techgeekme.sis;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by anirudh on 31/07/15.
 */
public class SisApplication extends Application {

    private static SisApplication sInstance;
    private RequestQueue mRequestQueue;

    public synchronized static SisApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mRequestQueue = Volley.newRequestQueue(this);

        sInstance = this;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }
}
