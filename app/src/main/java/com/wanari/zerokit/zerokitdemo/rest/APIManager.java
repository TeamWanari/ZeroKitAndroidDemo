package com.wanari.zerokit.zerokitdemo.rest;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.wanari.zerokit.zerokitdemo.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIManager {

    private static APIManager instance;

    private final APIInterface service;

    private final OkHttpClient okHttpClient;

    public static APIManager getInstance() {
        if (instance == null) {
            instance = new APIManager();
        }
        return instance;
    }

    private APIManager() {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG) {
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            logging.setLevel(HttpLoggingInterceptor.Level.NONE);
        }

        okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .readTimeout(40, TimeUnit.SECONDS)
                .connectTimeout(40, TimeUnit.SECONDS)
                .build();

        Retrofit restAdapter = new Retrofit.Builder()
                .baseUrl(APIConstants.BASE_URL)
                .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();
        service = restAdapter.create(APIInterface.class);
    }

    public APIInterface getService() {
        return service;
    }
}
