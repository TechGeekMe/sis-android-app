package com.techgeekme.sis;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

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
                Intent intent;
            /* Create an Intent that will start the Menu-Activity. */
                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                if (sharedPref.contains("usn")) {
                    intent = new Intent(Splash.this, HomeActivity.class);
                } else {
                    intent = new Intent(Splash.this, Login.class);
                }
                startActivity(intent);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
