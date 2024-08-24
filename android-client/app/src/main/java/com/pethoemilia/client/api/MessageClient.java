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
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MessageClient {

    @GET("message/findByGroupId/{id}")
    Call<List<Message>> findByGroupId(@Path("id") long id);

    @POST("message/save")
    Call<Message> save(@Body Message message);

    @GET("message/findAll")
    Call<List<Message>> findAll();

    @DELETE("message/delete/{id}")
    Call<Void> delete(@Path("id") long id);
}