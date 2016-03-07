package com.techgeekme.sis;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.techgeekme.sis.data.SisContract;
import com.techgeekme.sis.sync.SisSyncAdapter;

/**
 * Created by anirudh on 15/01/16.
 */
public class HomeActivity extends AppCompatActivity {
    private static final String LOG_TAG = HomeActivity.class.getSimpleName();
    private HomePageAdapter mHomePageAdapter;
    private ViewPager mViewPager;
    private CoordinatorLayout mCoordinatorLayout;
    private SyncStatusReceiver mSyncStatusReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mSyncStatusReceiver = new SyncStatusReceiver();

        mHomePageAdapter = new HomePageAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.home_view_pager);
        mViewPager.setAdapter(mHomePageAdapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(SisSyncAdapter.ACTION_SYNC_FINSISHED);
        LocalBroadcastManager.getInstance(this).registerReceiver(mSyncStatusReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mSyncStatusReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:
                logout();
                return true;
            case R.id.menu_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return false;
        }
    }

    private void logout() {
        SisSyncAdapter.stopAutoSync(this);
        Utility.setLoggedIn(this, false);
        getContentResolver().delete(SisContract.ALL_TABLES_CONTENT_URI, null, null);
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }

    public class SyncStatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getStringExtra(SisSyncAdapter.EXTRA_SYNC_STATUS)) {
                case SisSyncAdapter.SYNC_FAIL:
                    int statusCode = intent.getIntExtra(SisSyncAdapter.EXTRA_ERROR_CODE, -1);
                    Utility.showErrorSnackBar(mCoordinatorLayout, statusCode);
                    return;
                case SisSyncAdapter.SYNC_SUCCESS:
                    Utility.showSuccessSnackBar(mCoordinatorLayout);
                    return;
                default:
                    Log.e(LOG_TAG, "Invalid intent extra for SyncStatusReceiver");
            }

        }
    }

}
