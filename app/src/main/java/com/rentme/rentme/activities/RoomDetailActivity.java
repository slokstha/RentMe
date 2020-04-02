package com.rentme.rentme.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.rentme.rentme.R;
import com.rentme.rentme.models.Room;
import com.squareup.picasso.Picasso;

public class RoomDetailActivity extends AppCompatActivity {
    private static final int READ_REQ_CODE = 100;
    TextView etRoomDetail, etName, etDate, etLocationName, etContact, etEmail, etDesc, etFacility, tv_Email, tvAvailable;
    Button btn_Call;
    Room room;
    String roomData;
    ImageView vf_right_btn, vf_left_btn,not_available_iv;
    ViewFlipper viewFlipper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_details);
        initVars();
        handleIntent();
        loadData();
        onBtnClick();
    }

    private void initVars() {
        viewFlipper = findViewById(R.id.viewFlipper); // get the reference of ViewFlipper
        etRoomDetail = findViewById(R.id.title_tv_room_details);
        etName = findViewById(R.id.tvName);
        not_available_iv = findViewById(R.id.not_available_iv);
        etDate = findViewById(R.id.tvDate);
        tv_Email = findViewById(R.id.tv_Email);
        etLocationName = findViewById(R.id.tv_LocationName);
        etContact = findViewById(R.id.tvNumber);
        etEmail = findViewById(R.id.tvEmailId);
        etDesc = findViewById(R.id.tv_RoomDescription);
        etFacility = findViewById(R.id.tv_Facility);
        tvAvailable = findViewById(R.id.tvAvailable);
        btn_Call = findViewById(R.id.book_btn);
        vf_right_btn = findViewById(R.id.vf_right_btn);
        vf_left_btn = findViewById(R.id.vf_left_btn);
    }

    private void loadData() {
        etRoomDetail.setText(room.getTitle());
        if (room.getStatus()==1){
            not_available_iv.setVisibility(View.VISIBLE);
        }
        etName.setText(room.getUser().getName());
        etDate.setText(room.getUser().getCreated_at());
        String location = room.getCity()+": "+room.getLocation();
        etLocationName.setText(location);
        etContact.setText(room.getUser().getPhone());
        if (room.getUser().getEmail() != null) {
            etEmail.setVisibility(View.VISIBLE);
            tv_Email.setVisibility(View.VISIBLE);
            etEmail.setText(room.getUser().getEmail());
        }
        etDesc.setText(room.getDescription());
        etFacility.setText(room.getFacilities());
        if (room.getStatus() == 1) {
            String status = "Not Available";
            tvAvailable.setText(status);
            tvAvailable.setTextColor(ContextCompat.getColor(RoomDetailActivity.this, R.color.colorRed));
        }
        if (room.getImages() != null) {
            viewFlipper.startFlipping(); // start the flipping of views
            for (int i = 0; i < room.getImages().size(); i++) {
                ImageView image = new ImageView(RoomDetailActivity.this);
                image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Picasso.get().load(room.getImages().get(i)).into(image);
                viewFlipper.addView(image);
                final int temp = i;
                viewFlipper.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(RoomDetailActivity.this, PhotoZoomViewActivity.class);
                        intent.putExtra("url", room.getImages().get(temp));
                        startActivity(intent);
                    }
                });
                vf_left_btn.setVisibility(View.VISIBLE);
                vf_right_btn.setVisibility(View.VISIBLE);
                vf_right_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewFlipper.showNext();
                    }
                });
                vf_left_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewFlipper.showPrevious();
                    }
                });
            }
        } else {
            ImageView image = new ImageView(RoomDetailActivity.this);
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            image.setBackgroundResource(R.drawable.room_placeholder);
            viewFlipper.addView(image);
        }
    }

    private void handleIntent() {
        roomData = getIntent().getExtras().getString("roomData", "");
        room = new Gson().fromJson(roomData, Room.class);
    }

    private void onBtnClick() {
        btn_Call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissions();
            }
        });
    }

    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(RoomDetailActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, READ_REQ_CODE);
            }
        } else {
            openCallActivity();
        }
    }

    private void openCallActivity() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + room.getUser().getPhone()));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
        }
        startActivity(callIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_REQ_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCallActivity();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

}
