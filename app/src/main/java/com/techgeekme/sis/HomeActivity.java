package com.techgeekme.sis;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.lang.ref.WeakReference;

/**
 * Created by anirudh on 15/01/16.
 */
public class HomeActivity extends AppCompatActivity {

    private DatabaseManager mDatabaseManager;
    private HomePageAdapter mHomePageAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mHomePageAdapter = new HomePageAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.home_view_pager);
        mViewPager.setAdapter(mHomePageAdapter);
        mDatabaseManager = new DatabaseManager(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);

        SisApplication.getInstance().currentActivityWeakReference = new WeakReference<Activity>(this);
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
            default:
                return false;
        }
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


}
