package com.rentme.rentme.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class RestClient {
    private static RestClient mInstance; //static instance of RestClient (Singleton) class
    private RequestQueue mRequestQueue;
    private static Context mContext;

    private RestClient(Context context) { //private constructor to prevent, creating new object of RestClient class
        // Specify the application context
        mContext = context;
        // Get the request queue
        mRequestQueue = getRequestQueue();
    }

    public static synchronized RestClient getInstance(Context context) {
        // If Instance is null then initialize new Instance
        if (mInstance == null) mInstance = new RestClient(context); //create object only when object is null otherwise use same object
        // Return MySingleton new Instance
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        // If RequestQueue is null the initialize new RequestQueue
        if (mRequestQueue == null) mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());

        // Return RequestQueue
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request) {
        // Add the specified request to the request queue
        getRequestQueue().add(request);
    }
}