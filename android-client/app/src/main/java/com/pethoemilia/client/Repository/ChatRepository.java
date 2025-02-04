package com.pethoemilia.client.Repository;
import android.util.Base64;
import android.util.Log;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.pethoemilia.client.MyConst;
import com.pethoemilia.client.api.MessageClient;
import com.pethoemilia.client.api.UserClient;
import com.pethoemilia.client.entity.Message;
import com.pethoemilia.client.entity.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatRepository {
    private static ChatRepository instance;
    private MessageClient messageClient;
    private SharedPreferences sharedPref;

    private ChatRepository(Context context) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MyConst.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        messageClient = retrofit.create(MessageClient.class);
        sharedPref = context.getSharedPreferences(MyConst.SHARED_PREF_KEY, Context.MODE_PRIVATE);
    }

    public static ChatRepository getInstance(Context context) {
        if (instance == null) {
            instance = new ChatRepository(context);
        }
        return instance;
    }

    public User getUserFromSharedPreferences() {
        String userJson = sharedPref.getString(MyConst.USER, null);
        if (userJson != null) {
            Gson gson = new Gson();
            return gson.fromJson(userJson, User.class);
        }
        return null;
    }

    public void loadMessages(Long groupId, MutableLiveData<List<Message>> messages) {
        String encodedcredentials = sharedPref.getString(MyConst.AUTH, null);
        Call<List<Message>> call = messageClient.findByGroupId(groupId, encodedcredentials);
        call.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                if (response.isSuccessful()) {
                    messages.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                // Handle failure
            }
        });
    }

    public void sendMessage(Message message) {
        String encodedcredentials = sharedPref.getString(MyConst.AUTH, null);
        Call<Message> call = messageClient.save(message, encodedcredentials);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if (!response.isSuccessful()) {
                    // Handle failure
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                // Handle failure
            }
        });
    }
}
