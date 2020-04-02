package com.rentme.rentme.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.rentme.rentme.R;
import com.rentme.rentme.network.Api;
import com.rentme.rentme.interfaces.APIEndPoints;
import com.rentme.rentme.models.UserData;
import com.rentme.rentme.network.CheckConnectivity;
import com.rentme.rentme.utils.Constants;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;

public class LoginActivity extends AppCompatActivity {
    EditText etPhone, etPassword;
    Button btnLogin;
    TextView create_act_tv;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        onClick();


    }
    private void initViews() {
        etPhone = findViewById(R.id.emailorphoneEt);
        etPassword = findViewById(R.id.loginPwdEt);
        btnLogin = findViewById(R.id.signInBtn);
        create_act_tv = findViewById(R.id.create_act_tv);
        sharedPreferences = getSharedPreferences(Constants.sharePrefName,MODE_PRIVATE);
    }
    private void onClick() {

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = etPhone.getText().toString();
                String password = etPassword.getText().toString();
                if (!phone.isEmpty() && !password.isEmpty()) {
                    callLoginAPI(phone, password);
                } else {
                    etPhone.setError("Phone number is required");
                    etPassword.setError("Password is required");
                }

            }
        });
        create_act_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });
    }

    private void callLoginAPI(String phone, String password) {
        final ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phone", phone);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.loginUrl, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                try {
                    if (response.getBoolean("status")) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        JSONObject object = response.getJSONObject("data");
                        JSONObject userObject = object.getJSONObject("users");
                        JSONObject tokenObject = object.getJSONObject("token");
                        editor.putString("userDetails",userObject.toString());
                        editor.putString("tokenDetails",tokenObject.toString());
                        editor.apply();
                        Toast.makeText(LoginActivity.this, "Successfully Logged in", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid phone number or password", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(LoginActivity.this, "Server error", Toast.LENGTH_SHORT).show();
            }
        });
        if (CheckConnectivity.isNetworkAvailable(this)){
            RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
            requestQueue.add(jsonObjectRequest);
        }
        else {
            dialog.dismiss();
            Toast.makeText(this, "No internet connetion", Toast.LENGTH_SHORT).show();
        }

    }


}
