package com.rentme.rentme.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.rentme.rentme.R;

public class EditVehicleActivity extends AppCompatActivity {
EditText etTitle, etContact, etPrice, etDriverDetail;
Button btnSave;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_vehicle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Vehicle");
        etTitle = findViewById(R.id.EtTitle_edit);
        etContact = findViewById(R.id.EtContact_edit);
        etPrice = findViewById(R.id.EtPrice_edit);
        etDriverDetail = findViewById(R.id.EtODName_edit);
        btnSave = findViewById(R.id.btn_Save_edit);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        onBackPressed();
        return true;
    }
}
