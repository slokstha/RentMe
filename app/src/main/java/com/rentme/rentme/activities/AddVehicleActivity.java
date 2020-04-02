package com.rentme.rentme.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
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
import com.rentme.rentme.models.Vehicle;
import com.rentme.rentme.network.Api;
import com.rentme.rentme.network.AuthenticatedJSONRequest;
import com.rentme.rentme.network.CheckConnectivity;
import com.rentme.rentme.network.HandleNetworkError;
import com.rentme.rentme.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AddVehicleActivity extends AppCompatActivity {
    EditText etTitle, etContact, etodName, etPrice,locationEt;
    Button btn_save;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Vehicle");
        initVars();
        onClick();
    }
    private void initVars() {
        etTitle = findViewById(R.id.v_title_et);
        etContact = findViewById(R.id.v_contact_et);
        etodName = findViewById(R.id.v_owner_et);
        etPrice = findViewById(R.id.v_price_et);
        btn_save = findViewById(R.id.v_submit_btn);
        locationEt = findViewById(R.id.v_location_et);
    }

    private void onClick() {
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = etTitle.getText().toString();
                String price = etPrice.getText().toString();
                String owenerName = etodName.getText().toString();
                String contact = etContact.getText().toString();
                String serviceArea = locationEt.getText().toString();

                if (!title.isEmpty() && !price.isEmpty() && !owenerName.isEmpty() && !contact.isEmpty() && !serviceArea.isEmpty()){
                    callAddVehicleApi(title, price, owenerName, contact,serviceArea);
                }
                else {
                    Toast.makeText(AddVehicleActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    etTitle.setError("Field is required");
                    etPrice.setError("Field is required");
                    locationEt.setError("Field is required");
                    etContact.setError("Field is required");
                    etodName.setError("Field is required");
                }
            }
        });
    }

    private void callAddVehicleApi(String title, String price, String ownerName, String contact,String serviceArea) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();
        RequestQueue request = Volley.newRequestQueue(AddVehicleActivity.this);
        final JSONObject object = new JSONObject();
        try {
            object.put("title", title);
            object.put("price", price);
            object.put("owner_name", ownerName);
            object.put("contact", contact);
            object.put("service_area", serviceArea);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AuthenticatedJSONRequest jsonObjectRequest = new AuthenticatedJSONRequest(this,Request.Method.POST, Api.addvehicle, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                try {
                    if (response.getBoolean("status")) {
                        Toast.makeText(AddVehicleActivity.this, "Added successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AddVehicleActivity.this, VehicleDetailActivity.class));
                        finish();
                    } else {
                        Toast.makeText(AddVehicleActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                HandleNetworkError.handlerError(error, AddVehicleActivity.this);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        onBackPressed();
        return true;
    }
}
