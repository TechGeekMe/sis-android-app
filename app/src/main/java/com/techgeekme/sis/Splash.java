package com.techgeekme.sis;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

/**
 * Created by anirudh on 20/07/15.
 */
public class Splash extends Activity {

    /**
     * Duration of wait *
     */
    private final int SPLASH_DISPLAY_LENGTH = 1000;
    private Intent mMainIntent;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);
    /* New Handler to start the Menu-Activity
     * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            /* Create an Intent that will start the Menu-Activity. */
                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                if (sharedPref.contains("usn")) {
                    String usn = sharedPref.getString("usn", null);
                    String dob = sharedPref.getString("dob", null);
                    View v = findViewById(R.id.splash_screen_relative_layout);
                    StudentFetcherErrorListener el = new StudentFetcherErrorListener(v) {
                        @Override
                        public void onStudentFetcherError() {
                        }
                    };

                    String url = getString(R.string.server_url) + "?usn=" + usn + "&dob=" + dob;

                    StudentFetcher studentFetcher = new StudentFetcher(url, el) {
                        @Override
                        public void onStudentResponse(Student student) {
                            mMainIntent = new Intent(Splash.this, Home.class);
                            mMainIntent.putExtra("student_object", student);
                            startActivity(mMainIntent);
                            Splash.this.startActivity(mMainIntent);
                            Splash.this.finish();
                        }
                    };
                    studentFetcher.fetchStudent();
                } else {
                    mMainIntent = new Intent(Splash.this, Login.class);
                    Splash.this.startActivity(mMainIntent);
                    Splash.this.finish();
                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
