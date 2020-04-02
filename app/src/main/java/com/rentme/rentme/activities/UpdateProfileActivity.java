package com.rentme.rentme.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import com.google.gson.Gson;
import com.rentme.rentme.R;
import com.rentme.rentme.models.Room;
import com.rentme.rentme.models.User;
import com.rentme.rentme.network.Api;
import com.rentme.rentme.network.AuthenticatedJSONRequest;
import com.rentme.rentme.network.CheckConnectivity;
import com.rentme.rentme.network.HandleNetworkError;
import com.rentme.rentme.utils.Constants;
import com.rentme.rentme.utils.FileUtils;
import com.rentme.rentme.utils.ImageConverter;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateProfileActivity extends AppCompatActivity {
    EditText etName, etEmail, etAddress;
    TextView tvChangePwd, updtTextIv;
    CircleImageView updtPrflIv;
    Button btn_Save;
    private static final int IMAGE_PICKER_REQ_CODE = 976;
    private static final int READ_REQ_CODE = 154;
    String profile_pic_url;
    String selectedImagePath;
    User user;
    SharedPreferences preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        initView();
        handleIntent();
        onClick();
        loadfieled();
    }

    private void handleIntent() {
        String userData = getIntent().getExtras().getString(Constants.userData, "");
        user = new Gson().fromJson(userData, User.class);
    }
    private void loadfieled() {
        etName.setText(user.getName());
        if (user.getProfile_pic_url()!=null){
            Picasso.get().load(user.getProfile_pic_url()).placeholder(R.drawable.person).error(R.drawable.person).into(updtPrflIv);
        }
        etAddress.setText(user.getAddress());
        etEmail.setText(user.getEmail());
    }


    private void initView() {
        preferences = getSharedPreferences(Constants.sharePrefName,MODE_PRIVATE);
        etName = findViewById(R.id.updt_et_profile_name);
        updtPrflIv = findViewById(R.id.updt_prfl_iv);
        updtTextIv = findViewById(R.id.updt_text_iv);
        etAddress = findViewById(R.id.updt_et_profile_address);
        etEmail = findViewById(R.id.updt_et_profile_email);
        tvChangePwd = findViewById(R.id.tv_profile_change_pwd);
        btn_Save = findViewById(R.id.updt_btn_profile_save);

    }

    private void onClick() {
        tvChangePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UpdateProfileActivity.this, ChangePasswordActivity.class));
            }
        });
        updtTextIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
            }
        });
        btn_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString();
                String email = etEmail.getText().toString();
                String address = etAddress.getText().toString();
                if (!name.isEmpty() && !address.isEmpty()){
                    callRegisterApi(name,email,address);
                }
                else {
                    Toast.makeText(UpdateProfileActivity.this, "All fields with * are required", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void callRegisterApi(String name, String email, String address) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();
        RequestQueue request = Volley.newRequestQueue(UpdateProfileActivity.this);
        final JSONObject object = new JSONObject();
        try {
            object.put("name", name);
            object.put("email", email);
            object.put("address", address);
            if (profile_pic_url != null) {
                object.put("profile_pic_url", profile_pic_url);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AuthenticatedJSONRequest jsonObjectRequest = new AuthenticatedJSONRequest(this,Request.Method.POST, Api.updateProfile, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                try {
                    if (response.getBoolean("status")){
                        SharedPreferences.Editor editor = preferences.edit();
                        JSONObject jsonObject = response.getJSONObject("data");
                        User userNew = new Gson().fromJson(jsonObject.toString(),User.class);
                        String oldUserData = preferences.getString(Constants.userData,null);
                        User userOld = new Gson().fromJson(oldUserData,User.class);
                        userOld.setName(userNew.getName());
                        userOld.setAddress(userNew.getAddress());
                        userOld.setEmail(userNew.getEmail());
                        userOld.setProfile_pic_url(userNew.getProfile_pic_url());
                        String newUserData = new Gson().toJson(userOld);
                        editor.putString(Constants.userData,newUserData);
                        editor.apply();
                        Toast.makeText(UpdateProfileActivity.this, "updated successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(UpdateProfileActivity.this,ProfileActivity.class));
                        finish();
                    }
                    else {
                        Toast.makeText(UpdateProfileActivity.this, response.getString("an error occurred"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                HandleNetworkError.handlerError(error,UpdateProfileActivity.this);
                dialog.dismiss();

            }

        }
        );
        if (CheckConnectivity.isNetworkAvailable(this)){
            request.add(jsonObjectRequest);
        }
        else {
            dialog.dismiss();
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }

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
                        selectedImagePath = FileUtils.getPath(this,selectedImageUri);
//                        System.out.println("Image Path : " + selectedImagePath);
                        updtPrflIv.setImageURI(selectedImageUri);
                        //Converting to bitmap
                        profile_pic_url = ImageConverter.imageConvert(UpdateProfileActivity.this, selectedImageUri);
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
