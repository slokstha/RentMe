package com.rentme.rentme.interfaces;

import com.rentme.rentme.models.UserData;

import org.json.JSONObject;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface APIEndPoints {

    @Multipart
    @POST("create-post")
//    @Headers("Content-Type: application/json")
//    @Headers({"Content-Type: multipart/form-data"})
    Call<JSONObject> createPost(
            @Part("title") RequestBody titleId,
            @Part("description") RequestBody description,
            @Part("price") RequestBody price,
            @Part("location") RequestBody location,
            @Part("city") RequestBody city,
            @Part("facilities") RequestBody facilities,
            @Part("property_type") RequestBody property_type,
            @Part MultipartBody.Part[] file);
}
