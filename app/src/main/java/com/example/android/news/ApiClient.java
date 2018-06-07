package com.example.android.news;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    public static String BASE_URL_DETAIL = "https://content.guardianapis.com/";
    static Retrofit retrofit = null;

    public static Retrofit getApi() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL_DETAIL).addConverterFactory(GsonConverterFactory.create())
                    .build();

        }

        return retrofit;
    }
}
