package com.techgeekme.sis;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.techgeekme.sis.Home;
import com.techgeekme.sis.Login;
import com.techgeekme.sis.R;

/**
 * Created by anirudh on 20/07/15.
 */
public class Splash extends Activity {

    /**
     * Duration of wait *
     */
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.splashscreen);
        final DatabaseManager DBobj = new DatabaseManager(this);

    /* New Handler to start the Menu-Activity
     * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            /* Create an Intent that will start the Menu-Activity. */
                SharedPreferences credentialsSharedPref = getSharedPreferences(Login.PREFS_NAME, MODE_PRIVATE);
                Intent mainIntent;
                if (credentialsSharedPref.contains("usn")) {
                    Log.i("Splash","inside if");
                    Student student = new Student();
                    student = DBobj.getStudent();
                    mainIntent = new Intent(Splash.this, Home.class);
                    mainIntent.putExtra("student_object", student);
                } else {
                    Log.i("Splash","inside else");
                    mainIntent = new Intent(Splash.this, Login.class);
                }
                Splash.this.startActivity(mainIntent);
                Splash.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
