package com.techgeekme.sis;

import android.accounts.Account;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SyncStatusObserver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.techgeekme.sis.sync.SisSyncAdapter;

import java.util.ArrayList;

import static com.techgeekme.sis.LogUtils.LOGD;


public class SisFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<Course>> {
    private static final int SIS_LOADER = 0;
    private static final String LOG_TAG = SisFragment.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Student mStudent;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SyncStatusReceiver mSyncStatusReceiver;

    private SyncStatusObserver mSyncStatusObserver = new SyncStatusObserver() {
        @Override
        public void onStatusChanged(int which) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Account account = SisSyncAdapter.getSyncAccount(getContext());
                    String authority = getString(R.string.content_authority);
                    boolean syncActive = ContentResolver.isSyncActive(
                            account, authority);
                    boolean syncPending = ContentResolver.isSyncPending(
                            account, authority);
                    onRefreshingStateChanged(syncActive || syncPending);
                    LOGD(LOG_TAG, "Sync status changed Active: " + syncActive + " Pending: " + syncPending);
                }
            });

        }
    };

    private Object mSyncObserverHandle;


    public SisFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSyncStatusReceiver = new SyncStatusReceiver();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mSyncStatusReceiver);

        ContentResolver.removeStatusChangeListener(mSyncObserverHandle);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(SisSyncAdapter.ACTION_SYNC_FINSISHED);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mSyncStatusReceiver, intentFilter);
        getLoaderManager().initLoader(SIS_LOADER, null, this);

        // Watch for sync state changes
        mSyncStatusObserver.onStatusChanged(0);
        final int mask = ContentResolver.SYNC_OBSERVER_TYPE_PENDING |
                ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE;
        mSyncObserverHandle = ContentResolver.addStatusChangeListener(mask, mSyncStatusObserver);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sis, container, false);

        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.shared_pref_file), Context.MODE_PRIVATE);
        mStudent = new Student();
        mStudent.usn = sharedPref.getString("usn", null);
        mStudent.studentName = sharedPref.getString("name", null);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.home_recycler_view);
        // TODO Is this valid in this case
        // Optimization valid only if the size of the RecyclerView does not change due to change
        // in adapter content. Setting it to true even though in this case the size does change
        // since there is no other view at the same level that may get affected due to the change in size
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new SisRecyclerViewAdapter(mStudent.courses);
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_dark,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_red_dark);

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_sis, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                refresh();
                return true;
        }
        return false;
    }

    private void refresh() {
        SisSyncAdapter.syncImmediately(getContext());
    }


    @Override
    public Loader<ArrayList<Course>> onCreateLoader(int id, Bundle args) {
        LOGD(LOG_TAG, "On create loader called");
        return new SisLoader(getContext());
    }

    private void onRefreshingStateChanged(final boolean refreshing) {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(refreshing);
            }
        });
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Course>> loader, ArrayList<Course> data) {
        LOGD(LOG_TAG, "Load finished, size: " + mStudent.courses.size());
        mStudent.courses.clear();
        mStudent.courses.addAll(data);
        mAdapter.notifyDataSetChanged();

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Course>> loader) {
        LOGD(LOG_TAG, "On loader reset called");
        mStudent.courses.clear();
    }

    public class SyncStatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LOGD(LOG_TAG, "Broadcast received");
            getLoaderManager().restartLoader(SIS_LOADER, null, SisFragment.this);
        }
    }
}
