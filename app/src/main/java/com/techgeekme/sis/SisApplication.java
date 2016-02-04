package com.techgeekme.sis;

import android.app.Activity;
import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.lang.ref.WeakReference;

/**
 * Created by anirudh on 31/07/15.
 */
public class SisApplication extends Application {

    private static SisApplication sInstance;
    public WeakReference<LoadingDialogFragment> loadingDialogFragmentWeakReference;
    public WeakReference<Activity> currentActivityWeakReference;
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
