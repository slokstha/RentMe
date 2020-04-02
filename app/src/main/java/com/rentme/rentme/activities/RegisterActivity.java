package com.rentme.rentme.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import com.rentme.rentme.R;
import com.rentme.rentme.network.Api;
import com.rentme.rentme.network.CheckConnectivity;
import com.rentme.rentme.network.HandleNetworkError;
import com.rentme.rentme.utils.FileUtils;
import com.rentme.rentme.utils.ImageConverter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {
    EditText etName, etEmail, etPhone, etAddress, etPassword, etConfirmPassword;
    Button btnRegister;
    CircleImageView register_iv;
    private static final int IMAGE_PICKER_REQ_CODE = 976;
    private static final int READ_REQ_CODE = 154;
    String profile_pic_url;
    String selectedImagePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initViews();
        imageonClick();
        onClick();

    }

    private void onClick() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etName.getText().toString();
                String email = etEmail.getText().toString();
                String phone = etPhone.getText().toString();
                String address = etAddress.getText().toString();
                String password = etPassword.getText().toString();
                String password_confirmation = etConfirmPassword.getText().toString();
                if (!name.isEmpty() && !phone.isEmpty() && !address.isEmpty() && !password.isEmpty()) {
                    if (password.equals(password_confirmation)) {
                        callRegisterApi(name, email, phone, address, password, password_confirmation);
                    } else {
                        etConfirmPassword.setError("password do not match");
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "All fields with red error are required", Toast.LENGTH_SHORT).show();
                    etName.setError("field is required");
                    etPhone.setError("field is required");
                    etAddress.setError("field is required");
                    etPassword.setError("field is required");
                }

            }
        });
    }

    private void callRegisterApi(String name, String email, String phone, String address, String password, String password_confirmation) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();
        RequestQueue request = Volley.newRequestQueue(RegisterActivity.this);
        final JSONObject object = new JSONObject();
        try {
            object.put("name", name);
            object.put("email", email);
            object.put("address", address);
            if (profile_pic_url != null) {
                object.put("profile_pic_url", profile_pic_url);
            }
            object.put("phone", phone);
            object.put("password", password);
            object.put("password_confirmation", password_confirmation);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.registerUrl, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                try {
                    if (response.getBoolean("status")) {
                        Toast.makeText(RegisterActivity.this, "Register successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, response.getString("error"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                HandleNetworkError.handlerError(error, RegisterActivity.this);
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


    private void initViews() {
        register_iv = findViewById(R.id.register_iv);
        etName = findViewById(R.id.nameEt);
        etEmail = findViewById(R.id.emailEt);
        etPhone = findViewById(R.id.phoneEt);
        etAddress = findViewById(R.id.addressEt);
        etPassword = findViewById(R.id.passwordEt);
        etConfirmPassword = findViewById(R.id.cnfm_passwordEt);
        btnRegister = findViewById(R.id.signupBtn);
    }

    public void backbtn(View view) {
        onBackPressed();
    }

    private void imageonClick() {
        register_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
            }
        });
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) { //if permission not granted ask for new permission
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_REQ_CODE);
            } else {
                readImage();
            }
        }
    }

    private void readImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_PICKER_REQ_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_REQ_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                readImage();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            switch (requestCode) {

                case IMAGE_PICKER_REQ_CODE:
                    if (resultCode == Activity.RESULT_OK) {
                        //data gives you the image uri.
                        Uri selectedImageUri = data.getData();
                        selectedImagePath = FileUtils.getPath(this, selectedImageUri);
//                        System.out.println("Image Path : " + selectedImagePath);
                        register_iv.setImageURI(selectedImageUri);
                        //Converting to bitmap
                        profile_pic_url = ImageConverter.imageConvert(RegisterActivity.this, selectedImageUri);
//                        System.out.println("base 64??--------> " + profile_pic_url);
                        break;
                    } else if (resultCode == Activity.RESULT_CANCELED) {
                        Log.e(this.getLocalClassName(), "Invalid Image");
                    }
                    break;
            }
        } catch (Exception e) {
            Log.e(this.getLocalClassName(), "Exception in onActivityResult : " + e.getMessage());
        }
    }
}
