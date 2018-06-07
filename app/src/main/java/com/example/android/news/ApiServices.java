package com.example.android.news;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface ApiServices {

    @GET
    Call<Model> getNews(@Url String url);


}
