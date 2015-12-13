package com.techgeekme.sis;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;


public class Home extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Student mStudent;
    private String mDob;
    private DatabaseManager mDatabaseManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        mDatabaseManager = new DatabaseManager(this);

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        ArrayList<Course> courses = mDatabaseManager.getCourses();
        mStudent = new Student();
        mStudent.usn = sharedPref.getString("usn", null);
        mStudent.studentName = sharedPref.getString("name", null);
        mDob = sharedPref.getString("dob", null);
        mStudent.courses = courses;

        mRecyclerView = (RecyclerView) findViewById(R.id.home_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new HomeRecyclerViewAdapter(mStudent.courses);
        mRecyclerView.setAdapter(mAdapter);
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
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        DatabaseManager dm = new DatabaseManager(this);
        dm.deleteAll();
        finish();
    }

    private void refresh() {
        mDatabaseManager.deleteAll();

        StudentFetcherErrorListener el = new StudentFetcherErrorListener() {
            @Override
            public void onStudentFetcherError() {
            }
        };

        String url = getString(R.string.server_url) + "?usn=" + mStudent.usn + "&dob=" + mDob;

        StudentFetcher studentFetcher = new StudentFetcher(url, el) {
            @Override
            public void onStudentResponse(Student s) {
                Snackbar.make(mRecyclerView, "Refreshed", Snackbar.LENGTH_SHORT).show();
                mDatabaseManager.putCourses(s.courses);
                mStudent.courses.clear();
                mStudent.courses.addAll(s.courses);
                mAdapter.notifyDataSetChanged();
            }
        };

        studentFetcher.fetchStudent();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout_button) {
            logout();
        } else {
            refresh();
        }
        return true;
    }
}
