package com.example.myneverendingservice;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface jobInterface {
//starata verzija za backend_1 ->   @GET("getJobs")
    @GET("getJobs/emulator")
    Call<List<Job>> getJobs();

}
