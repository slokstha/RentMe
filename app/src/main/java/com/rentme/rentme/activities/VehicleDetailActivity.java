package com.rentme.rentme.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.rentme.rentme.R;
import com.rentme.rentme.adapters.VehicleAdapter;
import com.rentme.rentme.interfaces.AdapterClickListner;
import com.rentme.rentme.models.Vehicle;
import com.rentme.rentme.network.Api;
import com.rentme.rentme.network.CheckConnectivity;
import com.rentme.rentme.network.HandleNetworkError;
import com.rentme.rentme.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class VehicleDetailActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<Vehicle> vehicles = new ArrayList<>();
    private VehicleAdapter vehicleAdapter;
    Button btn_add_vehicle;
    SharedPreferences preferences;
    String userData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Vehicle Owner Information");
        initViews();
        loadData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        onBackPressed();
        return true;
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_transport);
        btn_add_vehicle = findViewById(R.id.btn_add_vehicle);
        vehicleAdapter = new VehicleAdapter(vehicles, this, new AdapterClickListner() {
            @Override
            public void onClick(int position, View view) {
                Vehicle vehicle = vehicles.get(position);
                callDelteApi(vehicle.getId());
            }
        });
        preferences = getSharedPreferences(Constants.sharePrefName, MODE_PRIVATE);
        userData = preferences.getString(Constants.userData, null);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(vehicleAdapter);

        btn_add_vehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userData == null) {
                    Intent intent = new Intent(VehicleDetailActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(VehicleDetailActivity.this, AddVehicleActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void callDelteApi(int id) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();
        RequestQueue request = Volley.newRequestQueue(VehicleDetailActivity.this);
        JSONObject object = new JSONObject();
        try {

            object.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.deleteVehicle, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                try {
                    if (response.getBoolean("status")) {
                        Toast.makeText(VehicleDetailActivity.this, "Successfully deleted", Toast.LENGTH_SHORT).show();
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);
                    } else {
                        Toast.makeText(VehicleDetailActivity.this, "An error occurred please try again later", Toast.LENGTH_SHORT).show();
                    }
                    vehicleAdapter.notifyDataSetChanged();
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                HandleNetworkError.handlerError(error, VehicleDetailActivity.this);
                dialog.dismiss();

            }
        });
        if (CheckConnectivity.isNetworkAvailable(this)) {
            request.add(jsonObjectRequest);
        } else {
            dialog.dismiss();
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadData() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();
        RequestQueue request = Volley.newRequestQueue(VehicleDetailActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Api.getVehicleList, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                try {
                    JSONArray array = response.getJSONArray("data");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        Vehicle vehicle = new Gson().fromJson(object.toString(), Vehicle.class);
                        vehicles.add(vehicle);
                    }
                    vehicleAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                HandleNetworkError.handlerError(error, VehicleDetailActivity.this);
                dialog.dismiss();

            }
        });
        if (CheckConnectivity.isNetworkAvailable(this)) {
            request.add(jsonObjectRequest);
        } else {
            dialog.dismiss();
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }
}
