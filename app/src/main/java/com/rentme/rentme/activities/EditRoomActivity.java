package com.rentme.rentme.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.rentme.rentme.R;
import com.rentme.rentme.models.Room;
import com.rentme.rentme.network.Api;
import com.rentme.rentme.network.AuthenticatedJSONRequest;
import com.rentme.rentme.network.CheckConnectivity;
import com.rentme.rentme.network.HandleNetworkError;
import com.rentme.rentme.network.RestClient;

import org.json.JSONException;
import org.json.JSONObject;

public class EditRoomActivity extends AppCompatActivity {
    EditText eTitle, eDesc, ePrice, efacility,eLocationEt;
    Spinner citySippiner, propertyTypeSp;
    Button btn_Post;
    String roomData;
    Room room;

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        onBackPressed();
        return true;
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_room);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Room");
        initViews();
        handleIntent();
        loadDatas();
    }

    private void initViews() {
        eTitle = findViewById(R.id.edit_title_et);
        eDesc = findViewById(R.id.edit_desc_et);
        eLocationEt = findViewById(R.id.edit_location_et);
        ePrice = findViewById(R.id.edit_price_et);
        citySippiner = findViewById(R.id.edit_city_Spinner);
        propertyTypeSp = findViewById(R.id.edit_property_Spinner);
        efacility = findViewById(R.id.edit_facilities_et);
        String[] cities = new String[]{"Kathmandu", "Lalitpur", "Bhaktapur"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, cities);
        citySippiner.setAdapter(adapter);
        String[] properties = new String[]{"Room", "Flat", "House"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, properties);
        propertyTypeSp.setAdapter(adapter1);
        btn_Post = findViewById(R.id.edit_post_btn);
        btn_Post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = room.getId();
                String description = eDesc.getText().toString();
                int price = Integer.parseInt(ePrice.getText().toString());
                String location = eLocationEt.getText().toString();
                String city = citySippiner.getSelectedItem().toString();
                String facilities = efacility.getText().toString();
                String property_type = propertyTypeSp.getSelectedItem().toString();
                callEditPostApi(id,description,price,location,city,facilities,property_type);
            }
        });
    }

    private void handleIntent() {
        roomData = getIntent().getExtras().getString("roomData", "");
        room = new Gson().fromJson(roomData, Room.class);

    }

    private void loadDatas() {
        eTitle.setText(room.getTitle());
        eLocationEt.setText(room.getLocation());
        eDesc.setText(room.getDescription());
        if (room.getProperty_type().equals("Room")){
            propertyTypeSp.setSelection(0);
        }
        else if (room.getProperty_type().equals("Flat")){
            propertyTypeSp.setSelection(1);
        }
        else if (room.getProperty_type().equals("House")){
            propertyTypeSp.setSelection(2);
        }
        String price = ""+room.getPrice();
        ePrice.setText(price);
        efacility.setText(room.getFacilities());
        if (room.getCity().equals("Kathmandu")){
            citySippiner.setSelection(0);
        }
        else if (room.getCity().equals("Lalitpur")){
            citySippiner.setSelection(1);
        }
        else if (room.getCity().equals("Bhaktapur")){
            citySippiner.setSelection(2);
        }
    }
    private void callEditPostApi(int id, String description, int price, String location, String city, String facilities, String property_type) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait....");
        dialog.setCancelable(false);
        dialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("id",id);
            object.put("description",description);
            object.put("price",price);
            object.put("location",location);
            object.put("city",city);
            object.put("facilities",facilities);
            object.put("property_type",property_type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AuthenticatedJSONRequest request = new AuthenticatedJSONRequest(this, Request.Method.POST, Api.updatePost, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.cancel();
                try {
                    if (response.getBoolean("status")){
                        Toast.makeText(EditRoomActivity.this, "Successfully updated", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(EditRoomActivity.this,ProfileActivity.class));
                        finish();
                    }
                    else {
                        Toast.makeText(EditRoomActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.cancel();
                HandleNetworkError.handlerError(error,EditRoomActivity.this);
            }
        });
        if (CheckConnectivity.isNetworkAvailable(EditRoomActivity.this)){
            RestClient.getInstance(EditRoomActivity.this).addToRequestQueue(request);
        }
        else {
            dialog.cancel();
            Toast.makeText(this, "No internet connected", Toast.LENGTH_SHORT).show();
        }
    }
}
