package com.rentme.rentme.network;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.rentme.rentme.models.Token;
import com.rentme.rentme.utils.Constants;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Api  {
    public static final String base_url = "https://rentme.whoamie.com/api/";
    public static final String registerUrl = base_url+"auth/signup";
    public static final String loginUrl = base_url+"auth/login";
    public static final String chnagePwd = base_url+"auth/changepwd";
    public static final String getAllPost = base_url+"all-posts";
    public static final String getUserPost = base_url+"users-posts";
    public static final String deletePost = base_url+"delete-post";
    public static final String updatePost = base_url+"update-post";
    public static final String markPost = base_url+"sold-request";
    public static final String updateProfile = base_url+"auth/update";
    public static final String addvehicle = base_url+"add-vehicle";
    public static final String getVehicleList = base_url+"all-vehicles";
    public static final String deleteVehicle = base_url+"delete-vehicle";
    public static Retrofit retrofit;

    public static Retrofit getInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(base_url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        }
        return retrofit;
    }
    public static Retrofit getHeaderInstance(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.sharePrefName,Context.MODE_PRIVATE);
        String userToken = preferences.getString(Constants.userToken,null);
        Token token = new Gson().fromJson(userToken,Token.class);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        final String tokenString = token.getAccess_token();
        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request newRequest  = chain.request().newBuilder()
                        .addHeader("Authorization", " Bearer " + tokenString)
                        .build();
                return chain.proceed(newRequest);
            }
        });
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(base_url)
                    .client(builder.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
