package com.example.myneverendingservice;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface jobInterface {

    @GET("getJobs")
    Call<List<Job>> getJobs();

}
