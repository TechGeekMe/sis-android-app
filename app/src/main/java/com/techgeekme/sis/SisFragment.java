package com.techgeekme.sis;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


public class SisFragment extends Fragment {

    // TODO Implement all network calls in a service

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Student mStudent;
    private String mDob;
    private CoordinatorLayout mCoordinatorLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private DatabaseManager mDatabaseManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sis, container, false);

        setRetainInstance(true);

        mDatabaseManager = new DatabaseManager(getContext());

        mCoordinatorLayout = (CoordinatorLayout) v.findViewById(R.id.coordinator_layout);


        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        mStudent = new Student();
        mStudent.usn = sharedPref.getString("usn", null);
        mStudent.studentName = sharedPref.getString("name", null);
        mDob = sharedPref.getString("dob", null);
        mStudent.courses = mDatabaseManager.getCourses();

        mRecyclerView = (RecyclerView) v.findViewById(R.id.home_recycler_view);
        // TODO Is this valid in this case
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new HomeRecyclerViewAdapter(mStudent.courses);
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


}
