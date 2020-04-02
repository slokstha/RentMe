package com.rentme.rentme.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.rentme.rentme.R;
import com.rentme.rentme.models.Token;
import com.rentme.rentme.models.User;
import com.rentme.rentme.network.Api;
import com.rentme.rentme.network.AuthenticatedJSONRequest;
import com.rentme.rentme.network.CheckConnectivity;
import com.rentme.rentme.network.HandleNetworkError;
import com.rentme.rentme.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ChangePasswordActivity extends AppCompatActivity {
    EditText oldPwd_et, newPwd_et, confPwd_et;
    Button change_btn;
    SharedPreferences preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwd);
        initView();
        onClick();
    }

    private void onClick() {

        change_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = oldPwd_et.getText().toString();
                String newPassword = newPwd_et.getText().toString();
                String confirmPassword = confPwd_et.getText().toString();
                if (!oldPassword.isEmpty() && !newPassword.isEmpty()) {
                    if (newPassword.equals(confirmPassword)) {
                        callChangePwdApi(oldPassword, newPassword, confirmPassword);
                    } else {
                        Toast.makeText(ChangePasswordActivity.this, "Confirm password does not match", Toast.LENGTH_SHORT).show();
                        confPwd_et.setError("Confirm password does not match");
                    }
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    oldPwd_et.setError("field is required");
                    newPwd_et.setError("field is required");
                    confPwd_et.setError("field is required");
                }
            }
        });

    }

    private void initView() {
        oldPwd_et = findViewById(R.id.old_pwd_et);
        newPwd_et = findViewById(R.id.new_pwd_et);
        confPwd_et = findViewById(R.id.confirm_pwd_et);
        change_btn = findViewById(R.id.change_pwd_btn);
    }

    private void callChangePwdApi(String oldPwd, String newPwd, String cnfPwd) {
        final ProgressDialog dialog = new ProgressDialog(this);
        preferences = getSharedPreferences(Constants.sharePrefName,MODE_PRIVATE);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();
        RequestQueue request = Volley.newRequestQueue(ChangePasswordActivity.this);
        final JSONObject object = new JSONObject();
        try {
            object.put("old_password", oldPwd);
            object.put("new_password", newPwd);
            object.put("confirm_new_password", cnfPwd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AuthenticatedJSONRequest jsonObjectRequest = new AuthenticatedJSONRequest(this,Request.Method.POST, Api.chnagePwd, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                try {
                    if (response.getBoolean("status")) {
                        JSONObject jsonObject = response.getJSONObject("data");
                        JSONObject tokenObject = jsonObject.getJSONObject("token");
                        Token newToken = new Gson().fromJson(tokenObject.toString(),Token.class);
                        SharedPreferences.Editor editor = preferences.edit();
                        String tokenData = preferences.getString(Constants.userToken,null);
                        Token tokenOld = new Gson().fromJson(tokenData,Token.class);
                        tokenOld.setAccess_token(newToken.getAccess_token());
                        String tokeOldString = new Gson().toJson(tokenOld);
                        editor.putString(Constants.userToken,tokeOldString);
                        editor.apply();
                        Toast.makeText(ChangePasswordActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ChangePasswordActivity.this, ProfileActivity.class));
                        finish();
                    } else {
                        Toast.makeText(ChangePasswordActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                HandleNetworkError.handlerError(error, ChangePasswordActivity.this);
                dialog.dismiss();

            }

        }
        );
        if (CheckConnectivity.isNetworkAvailable(this)) {
            request.add(jsonObjectRequest);
        } else {
            dialog.dismiss();
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }


}
