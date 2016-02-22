package com.techgeekme.sis.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by anirudh on 20/02/16.
 */
public class SisSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static SisSyncAdapter sSisSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sSisSyncAdapter == null) {
                sSisSyncAdapter = new SisSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSisSyncAdapter.getSyncAdapterBinder();
    }
}
