package com.rentme.rentme.network;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

public class HandleNetworkError {
    public static void handlerError(VolleyError error,Context context) {
        try {
            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                //This indicates that the reuest has either time out or there is no connection
                Toast.makeText(context, "Connection Timeout !!", Toast.LENGTH_SHORT).show();
            } else if (error instanceof AuthFailureError) {
                Toast.makeText(context, "Unauthorized Access !!", Toast.LENGTH_SHORT).show();
                //Error indicating that there was an Authentication Failure while performing the request
            } else if (error instanceof ServerError) {
                Toast.makeText(context, "Error Response From Server !!", Toast.LENGTH_SHORT).show();
                //Indicates that the server responded with a error response
            } else if (error instanceof NetworkError) {
                Toast.makeText(context, "Network Error !! Check Your Connections", Toast.LENGTH_SHORT).show();
                //Indicates that there was network error while performing the request
            } else if (error instanceof ParseError) {
                Toast.makeText(context, "Parser Error !!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Unknown Error Occurred !! Try Again Later", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, "An Exception Occurred !! Try Again Later", Toast.LENGTH_SHORT).show();
        }
    }
}
