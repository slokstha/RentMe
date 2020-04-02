package com.rentme.rentme.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.rentme.rentme.R;
import com.rentme.rentme.adapters.MyPostAdapter;
import com.rentme.rentme.interfaces.ProfileAdapterClickListner;
import com.rentme.rentme.models.Room;
import com.rentme.rentme.models.User;
import com.rentme.rentme.network.Api;
import com.rentme.rentme.network.AuthenticatedJSONRequest;
import com.rentme.rentme.network.CheckConnectivity;
import com.rentme.rentme.network.HandleNetworkError;
import com.rentme.rentme.network.RestClient;
import com.rentme.rentme.utils.Constants;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private List<Room> rooms = new ArrayList<>();
    MyPostAdapter myPostAdapter;
    RecyclerView recyclerView;
    TextView name_tv, location_tv, contact_tv, email_tv, joinedData;
    Button edit_profile_btn;
    User user;
    View view;
    ShimmerFrameLayout shimmerFrameLayout;
    CircleImageView imageView;
    LinearLayout linearLayout;
    String roomData;
    Button editBtn, dltBtn, markBtn; //dialog box

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initView();
        loadUserData();
        profileEditOnClick();
        loadData();
        initRecyclerview();

    }

    private void profileEditOnClick() {
        edit_profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userData = new Gson().toJson(user);
                Intent intent = new Intent(ProfileActivity.this, UpdateProfileActivity.class);
                intent.putExtra(Constants.userData, userData);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        shimmerFrameLayout.startShimmer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        shimmerFrameLayout.stopShimmer();
    }

    private void initRecyclerview() {
        myPostAdapter = new MyPostAdapter(rooms, this, new ProfileAdapterClickListner() {
            @Override
            public void titleOnClick(int position, View view) {
                Room room = rooms.get(position);
                roomData = new Gson().toJson(room);
                Intent intent = new Intent(ProfileActivity.this, RoomDetailActivity.class);
                intent.putExtra("roomData", roomData);
                startActivity(intent);
            }

            @Override
            public void moreBtnOnClick(int position, View view) {
                Room room = rooms.get(position);
                int postId = room.getId();
                showDialogBox(postId, position);
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(myPostAdapter);
    }

    private void initView() {
        name_tv = findViewById(R.id.p_name_tv);
        shimmerFrameLayout = findViewById(R.id.shimmer_effect_profile);
        imageView = findViewById(R.id.profile_iv);
        location_tv = findViewById(R.id.p_location_tv);
        contact_tv = findViewById(R.id.p_contact_tv);
        joinedData = findViewById(R.id.J_profile_tv);
        email_tv = findViewById(R.id.p_email_tv);
        edit_profile_btn = findViewById(R.id.ProfileEdit_btn);
        linearLayout = findViewById(R.id.empty_layout);
        recyclerView = findViewById(R.id.ProfileRecyclerView);
    }

    private void showDialogBox(final int id, final int position) {
        view = getLayoutInflater().inflate(R.layout.dialog_more_layout, null);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setView(view);
        dialog.show();
        editBtn = view.findViewById(R.id.post_edit_btn);
        dltBtn = view.findViewById(R.id.post_delete_btn);
        markBtn = view.findViewById(R.id.post_mark_btn);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Room room = rooms.get(position);
                roomData = new Gson().toJson(room);
                Intent intent = new Intent(ProfileActivity.this, EditRoomActivity.class);
                intent.putExtra("roomData", roomData);
                startActivity(intent);
                finish();
            }
        });
        dltBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callDeletePostApi(id);
            }
        });
        markBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callMarkAPI(id);
            }
        });
    }

    private void callMarkAPI(int id) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait....");
        dialog.setCancelable(false);
        dialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("post_id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AuthenticatedJSONRequest request = new AuthenticatedJSONRequest(this, Request.Method.POST, Api.markPost, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.cancel();
                try {
                    if (response.getBoolean("status")) {
                        Toast.makeText(ProfileActivity.this, "Successfully marked unavailable", Toast.LENGTH_SHORT).show();
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);
                    } else {
                        Toast.makeText(ProfileActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.cancel();
                HandleNetworkError.handlerError(error, ProfileActivity.this);
            }
        });
        if (CheckConnectivity.isNetworkAvailable(ProfileActivity.this)) {
            RestClient.getInstance(ProfileActivity.this).addToRequestQueue(request);
        } else {
            dialog.cancel();
            Toast.makeText(this, "No internet connected", Toast.LENGTH_SHORT).show();
        }
    }

    private void callDeletePostApi(int id) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait....");
        dialog.setCancelable(false);
        dialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AuthenticatedJSONRequest request = new AuthenticatedJSONRequest(this, Request.Method.POST, Api.deletePost, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.cancel();
                try {
                    if (response.getBoolean("status")) {
                        Toast.makeText(ProfileActivity.this, "Successfully deleted", Toast.LENGTH_SHORT).show();
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);
                    } else {
                        Toast.makeText(ProfileActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.cancel();
                HandleNetworkError.handlerError(error, ProfileActivity.this);
            }
        });
        if (CheckConnectivity.isNetworkAvailable(ProfileActivity.this)) {
            RestClient.getInstance(ProfileActivity.this).addToRequestQueue(request);
        } else {
            dialog.cancel();
            Toast.makeText(this, "No internet connected", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUserData() {
        SharedPreferences preferences = getSharedPreferences(Constants.sharePrefName, MODE_PRIVATE);
        String userData = preferences.getString(Constants.userData, null);
        user = new Gson().fromJson(userData, User.class);
        if (user.getProfile_pic_url() != null) {
            Picasso.get().load(user.getProfile_pic_url()).placeholder(R.drawable.person).error(R.drawable.person).into(imageView);
        }

        String address = "Address: " + user.getAddress();
        String phone = "Phone: " + user.getPhone();
        String date = "Date Joined: " + user.getCreated_at();
        name_tv.setText(user.getName());
        location_tv.setText(address);
        contact_tv.setText(phone);
        if (user.getEmail() != null) {
            String email = "Email: " + user.getEmail();
            email_tv.setVisibility(View.VISIBLE);
            email_tv.setText(email);
        }
        joinedData.setText(date);

    }

    private void loadData() {
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        JSONObject object = new JSONObject();
        try {
            object.put("user_id", user.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Api.getUserPost, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                shimmerFrameLayout.setVisibility(View.GONE);
                try {
                    JSONArray array = response.getJSONArray("data");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jsonObject = array.getJSONObject(i);
                        Room room = new Gson().fromJson(jsonObject.toString(), Room.class);
                        rooms.add(room);
                        roomData = new Gson().toJson(room);
                    }
                    myPostAdapter.notifyDataSetChanged();
                    if (myPostAdapter.getItemCount() == 0) {
                        recyclerView.setVisibility(View.GONE);
                        linearLayout.setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        linearLayout.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                shimmerFrameLayout.setVisibility(View.GONE);
                Toast.makeText(ProfileActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
        if (CheckConnectivity.isNetworkAvailable(this)) {

            RestClient.getInstance(ProfileActivity.this).addToRequestQueue(request);
        } else {
            shimmerFrameLayout.setVisibility(View.GONE);
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }

    }

}
