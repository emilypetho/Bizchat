package com.pethoemilia.client.api;

import com.pethoemilia.client.entity.Group;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface GroupClient {
    @GET("group/findByUserId/{id}")
    Call<List<Group>> findByUserId(@Path("id") long id, @Header("Authorization") String authHeader);

    @POST("group/save")
    Call<Group> saveGroup(@Body Group group, @Header("Authorization") String authHeader);

    @GET("group/summarize/{id}")
    Call<String> summarize(@Path("id") long id, @Header("Authorization") String authHeader);

    @POST("group/addUser/{groupId}/{userId}")
    Call<Void> addUserToGroup(@Path("groupId") long groupId, @Path("userId") long userId, @Header("Authorization") String authHeader);

    @GET("groups/{id}")
    Call<Group> findById(@Path("id") long groupId, @Header("Authorization") String authHeader);

    @DELETE("group/removeUser/{groupId}/{userId}")
    Call<Void> removeUserFromGroup(@Path("groupId") long groupId, @Path("userId") long userId, @Header("Authorization") String authHeader);


}
