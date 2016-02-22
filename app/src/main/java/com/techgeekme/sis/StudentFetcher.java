package com.techgeekme.sis;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Created by anirudh on 10/12/15.
 */
public class StudentFetcher extends Request<byte[]> {

    private Listener mListener;

    public StudentFetcher(String url, Listener listener, ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        mListener = listener;
        setRetryPolicy(new DefaultRetryPolicy(70000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    protected Response<byte[]> parseNetworkResponse(NetworkResponse networkResponse) {
        return Response.success(networkResponse.data, HttpHeaderParser.parseCacheHeaders(networkResponse));
    }

    @Override
    protected void deliverResponse(byte[] responseBytes) {
        ByteArrayInputStream bais = new ByteArrayInputStream(responseBytes);
        ObjectInputStream ois;
        Student s;
        try {
            ois = new ObjectInputStream(bais);
            s = (Student) ois.readObject();
//            onStudentResponse(s);
            mListener.onResponse(s);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    public void fetchStudent() {
        RequestQueue requestQueue = SisApplication.getInstance().getRequestQueue();
        requestQueue.add(this);

    }


}



