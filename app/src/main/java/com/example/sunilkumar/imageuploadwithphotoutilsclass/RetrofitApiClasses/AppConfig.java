package com.example.sunilkumar.imageuploadwithphotoutilsclass.RetrofitApiClasses;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class AppConfig {

//    private static String BASE_URL = "http://mushtaq.16mb.com/";
   private static String BASE_URL = "http:/192.168.10.127/";
//    private static String BASE_URL = "holostikapp.com/GEOMVCAPI/api/PictureUploadController/";

   public static Retrofit getRetrofit() {

        return new Retrofit.Builder()
                .baseUrl(AppConfig.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
    }
}
