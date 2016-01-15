package com.techgeekme.sis;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.lang.ref.WeakReference;


public class Home extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Student mStudent;
    private String mDob;
    private DatabaseManager mDatabaseManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private CoordinatorLayout mCoordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        mDatabaseManager = new DatabaseManager(this);

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        mStudent = new Student();
        mStudent.usn = sharedPref.getString("usn", null);
        mStudent.studentName = sharedPref.getString("name", null);
        mDob = sharedPref.getString("dob", null);
        mStudent.courses = mDatabaseManager.getCourses();

        mRecyclerView = (RecyclerView) findViewById(R.id.home_recycler_view);
        // TODO Is this valid in this case
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new HomeRecyclerViewAdapter(mStudent.courses);
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
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

        SisApplication.getInstance().currentActivityWeakReference = new WeakReference<Activity>(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return true;
    }

    private void logout() {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();
        mDatabaseManager.deleteAll();
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }

    private void refresh() {

        StudentFetcherErrorListener el = new StudentFetcherErrorListener() {
            @Override
            public void onStudentFetcherError() {
            }
        };

        String url = getString(R.string.server_url) + "?usn=" + mStudent.usn + "&dob=" + mDob;

        StudentFetcher studentFetcher = new StudentFetcher(url, el) {
            @Override
            public void onStudentResponse(Student s) {
                mSwipeRefreshLayout.setRefreshing(false);
                // TODO Too much to process on main thread, make it run in a seperate thread
                mDatabaseManager.deleteAll();
                mDatabaseManager.putCourses(s.courses);
                mStudent.courses.clear();
                mStudent.courses.addAll(s.courses);
                mAdapter.notifyDataSetChanged();
                Snackbar.make(mCoordinatorLayout, "Refreshed", Snackbar.LENGTH_SHORT).show();
            }
        };

        studentFetcher.fetchStudent();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout_button) {
            logout();
        } else if (item.getItemId() == R.id.refresh_button) {
            mSwipeRefreshLayout.setRefreshing(true);
            refresh();
        }
        return true;
    }
}
