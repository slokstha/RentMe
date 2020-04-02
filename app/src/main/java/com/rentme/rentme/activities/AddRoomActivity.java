package com.rentme.rentme.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.rentme.rentme.R;
import com.rentme.rentme.adapters.ImageAdapter;
import com.rentme.rentme.interfaces.APIEndPoints;
import com.rentme.rentme.network.Api;
import com.rentme.rentme.utils.FileUtils;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Url;

public class AddRoomActivity extends AppCompatActivity {
    private static final int READ_REQ_CODE = 100;
    EditText eTitle, eDesc, ePrice, efacility, locationEt;
    Spinner city_sp, property_sp;
    Button bpost;
    ImageView imgs;
    RecyclerView recyclerView;
    ArrayList<Uri> uris = new ArrayList<>();
    int PICK_IMAGE_MULTIPLE = 1;
    ArrayList<File> imagesEncodedList = new ArrayList<>();
    ImageAdapter adapter;
    File file;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add a new room");
        initVars();
        onClick();
    }

    private void initVars() {
        eTitle = findViewById(R.id.ETitle);
        eDesc = findViewById(R.id.EDesc);
        ePrice = findViewById(R.id.EPrice);
        bpost = findViewById(R.id.b_Post);
        imgs = findViewById(R.id.image_post);
        city_sp = findViewById(R.id.City_Spinner);
        locationEt = findViewById(R.id.location_et);
        property_sp = findViewById(R.id.Property_Spinner);
        efacility = findViewById(R.id.EFacility);
        String[] cities = new String[]{"Kathmandu", "Lalitpur", "Bhaktapur"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, cities);
        city_sp.setAdapter(adapter);
        String[] properties = new String[]{" Room", "Flat", "House"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, properties);
        property_sp.setAdapter(adapter1);
//        String selected = spinner.getSelectedItem().toString();
//        spinner2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                String o = adapterView.getItemAtPosition(i).toString();
//            }
//        });
        bpost = findViewById(R.id.b_Post);
        recyclerView = findViewById(R.id.RecyclerAddImage);
    }

    private void onClick() {
        imgs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
            }
        });
        bpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = eTitle.getText().toString();
                String descrption = eDesc.getText().toString();
                String location = locationEt.getText().toString();
                String price = ePrice.getText().toString();
                String city = city_sp.getSelectedItem().toString();
                String facility = efacility.getText().toString();
                String propertyType = property_sp.getSelectedItem().toString();
                if (!title.isEmpty() && !descrption.isEmpty() && !location.isEmpty() && !price.isEmpty() && !city.isEmpty() && !facility.isEmpty() && !propertyType.isEmpty()){
                    callPostRoomAPI(title, descrption, location, price, city, facility, propertyType);
                }
                else {
                    Toast.makeText(AddRoomActivity.this, "All field are required", Toast.LENGTH_SHORT).show();
                    eTitle.setError("field is required");
                    eDesc.setError("field is required");
                    locationEt.setError("field is required");
                    ePrice.setError("field is required");
                    efacility.setError("field is required");
                }
            }
        });
    }

    private void readImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
    }


    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //if permission not granted
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_REQ_CODE);
            } else {
                readImage();
            }
        }
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
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        onBackPressed();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK
                    && null != data) {
                if (data.getData() != null) {
                    Uri mImageUri = data.getData();
                    uris.add(mImageUri);
                    file = new File(FileUtils.getPath(this, mImageUri));
                    imagesEncodedList.add(file);
                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            uris.add(uri);
                            file = new File(FileUtils.getPath(this, uri)); //gets path of image
                            imagesEncodedList.add(file);
                        }
                    }
                }
            }
//            else {
//                Toast.makeText(this, "You haven't picked Image",
//                        Toast.LENGTH_LONG).show();
//            }
            recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
            adapter = new ImageAdapter(uris, this);
            recyclerView.setAdapter(adapter);

        } catch (Exception e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG)
                    .show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void callPostRoomAPI(String title, String descrption, String location, String price, String city, String facility, String propertyType) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();
        MultipartBody.Part[] multipartTypedOutput = new MultipartBody.Part[imagesEncodedList.size()];

        for (int index = 0; index < imagesEncodedList.size(); index++) {
            Log.d("Upload request", "requestUploadSurvey: survey image " + index + "  " + imagesEncodedList.get(index));
            File file2 = imagesEncodedList.get(index);
            RequestBody surveyBody = RequestBody.create(MediaType.parse("image/*"), file2);
            multipartTypedOutput[index] = MultipartBody.Part.createFormData("img[]", file2.getPath(), surveyBody);
        }
        RequestBody reqTitle = RequestBody.create(MediaType.parse("text/plain"), title);
        RequestBody reqDescription = RequestBody.create(MediaType.parse("text/plain"), descrption);
        RequestBody reqLocation = RequestBody.create(MediaType.parse("text/plain"), location);
        RequestBody reqPrice = RequestBody.create(MediaType.parse("text/plain"), price);
        RequestBody reqCity = RequestBody.create(MediaType.parse("text/plain"), city);
        RequestBody reqPropertyType = RequestBody.create(MediaType.parse("text/plain"), propertyType);
        RequestBody reqFacility = RequestBody.create(MediaType.parse("text/plain"), facility);
        APIEndPoints apiEndPoints = Api.getHeaderInstance(this).create(APIEndPoints.class);
        Call<JSONObject> call = apiEndPoints.createPost(reqTitle, reqDescription, reqPrice, reqLocation, reqCity, reqFacility, reqPropertyType, multipartTypedOutput);
        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                dialog.dismiss();
                Toast.makeText(AddRoomActivity.this, "Successfully created", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AddRoomActivity.this, HomeActivity.class));
                finish();
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(AddRoomActivity.this, "Error" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
