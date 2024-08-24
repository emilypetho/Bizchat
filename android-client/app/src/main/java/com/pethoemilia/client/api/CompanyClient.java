package com.pethoemilia.client.api;

import com.pethoemilia.client.entity.Company;
import com.pethoemilia.client.entity.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CompanyClient {

    @POST("company/save")
    Call<Company> save(@Body Company company);

    @GET("company/findAll")
    Call<List<Company>> findAll();

    @DELETE("company/delete/{id}")
    Call<Void> delete(@Path("id") long id);

    @GET("company/findByName")
    Call<Long> findByName(@Query("name") String name);

}