package com.rentme.rentme.activities;

import android.os.Bundle;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.gson.Gson;
import com.rentme.rentme.R;
import com.rentme.rentme.models.Room;
import com.squareup.picasso.Picasso;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class PhotoZoomViewActivity extends AppCompatActivity {
    PhotoView photoView;
    String url;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_zoom_view);
        handleIntent();
        photoView = findViewById(R.id.photo_view);
        Picasso.get().load(url).into(photoView);
    }
    private void handleIntent() {
        url = getIntent().getExtras().getString("url", "");
    }
}
