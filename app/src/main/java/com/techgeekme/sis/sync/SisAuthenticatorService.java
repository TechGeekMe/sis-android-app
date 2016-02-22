package com.techgeekme.sis.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by anirudh on 20/02/16.
 */
public class SisAuthenticatorService extends Service {
    // Instance field that stores the authenticator object
    private SisAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new SisAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
