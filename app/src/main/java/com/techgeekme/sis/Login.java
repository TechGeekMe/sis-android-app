package com.techgeekme.sis;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import android.content.SharedPreferences;

public class Login extends AppCompatActivity {
    Button loginButton;
    public static final String PREFS_NAME = "Credentials";
    DatabaseManager database = new DatabaseManager(getApplicationContext());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        loginButton = (Button) findViewById(R.id.login_button);
    }
    public void displaySis(Student student) {
        Intent mIntent = new Intent(this, Home.class);
        mIntent.putExtra("student_object", student);
        startActivity(mIntent);
    }
    public void login(View v) {
        loginButton.setEnabled(false);
        final EditText usernameEditText = (EditText) findViewById(R.id.usn_edit_text);
        final EditText passwordEditText = (EditText) findViewById(R.id.password_edit_text);
        SharedPreferences credentialsSharedPref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        if(credentialsSharedPref.contains(usernameEditText.getText()+"") && credentialsSharedPref.contains(passwordEditText.getText()+"")) {
            Student student = database.getStudent();
            displaySis(student);
        }
        else {
            ErrorListener el = new ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Login", error.toString());
                }
            };
            String url = getString(R.string.server_url) + "?usn=" + usernameEditText.getText() + "&dob=" + passwordEditText.getText();
            Request<byte[]> req = new Request<byte[]>(Method.GET, url, el) {

                @Override
                protected Response<byte[]> parseNetworkResponse(NetworkResponse networkResponse) {
                    SharedPreferences credentialsSharedPref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = credentialsSharedPref.edit();
                    editor.putString("usn", usernameEditText.getText()+"");
                    editor.putString("password", passwordEditText.getText()+"");
                    editor.commit();
                    return Response.success(networkResponse.data, HttpHeaderParser.parseCacheHeaders(networkResponse));

                }

                @Override
                protected void deliverResponse(byte[] responseBytes) {

                    Log.i("Login", new String(responseBytes));
                    ByteArrayInputStream bais = new ByteArrayInputStream(responseBytes);
                    ObjectInputStream ois = null;
                    Student s = null;
                    try {
                        ois = new ObjectInputStream(bais);
                        s = (Student) ois.readObject();
                        database.addStudent(s);
                        displaySis(s);
                    } catch (ClassNotFoundException | IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            req.setRetryPolicy(new DefaultRetryPolicy(100000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueue requestQueue = VolleyApplication.getInstance().getRequestQueue();
            requestQueue.add(req);
        }



    }

}
