package com.pethoemilia.client.api;

import com.pethoemilia.client.entity.Company;
import com.pethoemilia.client.entity.Group;
import com.pethoemilia.client.entity.Message;
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

public interface MessageClient {

    @GET("message/findByGroupId/{id}")
    Call<List<Message>> findByGroupId(@Path("id") long id, @Header("Authorization") String authHeader);

    @POST("message/save")
    Call<Message> save(@Body Message message, @Header("Authorization") String authHeader);

    @GET("message/findAll")
    Call<List<Message>> findAll(@Header("Authorization") String authHeader);

    @DELETE("message/delete/{id}")
    Call<Void> delete(@Path("id") long id, @Header("Authorization") String authHeader);
}