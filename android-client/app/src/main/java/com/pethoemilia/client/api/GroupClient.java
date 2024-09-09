package com.pethoemilia.client.api;

import com.pethoemilia.client.entity.Group;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface GroupClient {
    @GET("group/findByUserId/{id}")
    Call<List<Group>> findByUserId(@Path("id") long id, @Header("Authorization") String authHeader);
}
