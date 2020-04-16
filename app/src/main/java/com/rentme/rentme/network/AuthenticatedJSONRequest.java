package com.rentme.rentme.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.rentme.rentme.models.Token;
import com.rentme.rentme.utils.Constants;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

    public class AuthenticatedJSONRequest extends JsonObjectRequest {
        String token;
        Context context;
        String TAG=getClass().getName();
        SharedPreferences preferences;
        //Constructor with required arguments to sent request
        public AuthenticatedJSONRequest(Context context, int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
            super(method, url, jsonRequest, listener, errorListener); //Initializing parameters
            this.context = context;
            if(jsonRequest!=null) Log.i(TAG, "payload: "+jsonRequest);
            preferences = context.getSharedPreferences(Constants.sharePrefName, Context.MODE_PRIVATE);
            String tokenString = preferences.getString(Constants.userToken, null);
            Token tokenObj = new Gson().fromJson(tokenString, Token.class);
            this.token = tokenObj.getAccess_token();
            setRetryPolicy(new DefaultRetryPolicy(5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        }
        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + token);
            headers.put("Content-Type", "application/json");
            return headers;
        }
}
