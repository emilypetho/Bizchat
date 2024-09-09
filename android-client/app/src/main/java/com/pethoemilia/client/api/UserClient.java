package com.pethoemilia.client.api;

import com.pethoemilia.client.entity.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserClient {

    @POST("user/save") // Assuming your endpoint to save a user is "users"
    Call<User> save(@Body User user);

    @GET("user/findAll") // Assuming your endpoint to get all users is "users"
    Call<List<User>> findAll(@Header("Authorization") String authHeader);

    @DELETE("user/delete/{id}") // Assuming your endpoint to delete a user is "users/{id}"
    Call<Void> delete(@Path("id") long id,@Header("Authorization") String authHeader);

    @GET("user/findByEmail/{email}")
    Call<User> findByEmail(@Path("email") String email,@Header("Authorization") String authHeader);

    @GET("user/checkuser")
    Call<Void> checkUser( @Header("Authorization") String authHeader);
}