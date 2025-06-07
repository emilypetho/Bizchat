package com.pethoemilia.client.Repository;
import android.util.Base64;
import android.util.Log;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.pethoemilia.client.MyConst;
import com.pethoemilia.client.api.GroupClient;
import com.pethoemilia.client.api.MessageClient;
import com.pethoemilia.client.api.UserClient;
import com.pethoemilia.client.entity.Group;
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
    private GroupClient groupClient;
    private UserClient userClient;


    private ChatRepository(Context context) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MyConst.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        messageClient = retrofit.create(MessageClient.class);
        groupClient = retrofit.create(GroupClient.class);
        userClient = retrofit.create(UserClient.class);
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

    public void sendMessage(Message message, MessageCallback callback) {
        String encodedcredentials = sharedPref.getString(MyConst.AUTH, null);
        Call<Message> call = messageClient.save(message, encodedcredentials);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onFailure();
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                callback.onFailure();
            }
        });
    }
    public void addUserToGroup(long groupId, long userId, GroupCallback callback) {
        String encodedCredentials = sharedPref.getString(MyConst.AUTH, null);
        Call<Void> call = groupClient.addUserToGroup(groupId, userId, encodedCredentials);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d("AddUser", "HTTP code: " + response.code());
                if (response.code() == 200 || response.code() == 204) {
                    // itt a felhasználó sikeresen hozzáadva, callback.onSuccess meghívása, függetlenül a csoport frissítésétől
                    callback.onSuccess();

                    // továbbra is frissítjük a csoportot, de ha nem sikerül, csak logoljuk, nem hívjuk onFailure-t
                    Call<Group> groupCall = groupClient.findById(groupId, encodedCredentials);
                    groupCall.enqueue(new Callback<Group>() {
                        @Override
                        public void onResponse(Call<Group> call, Response<Group> groupResponse) {
                            if (groupResponse.isSuccessful() && groupResponse.body() != null) {
                                Group updatedGroup = groupResponse.body();
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString(MyConst.GROUP, new Gson().toJson(updatedGroup));
                                editor.apply();
                                Log.d("AddUser", "Csoport frissítve.");
                            } else {
                                Log.e("AddUser", "Group frissítés sikertelen. Kód: " + groupResponse.code());
                            }
                        }

                        @Override
                        public void onFailure(Call<Group> call, Throwable t) {
                            Log.e("AddUser", "Group lekérés hiba: " + t.getMessage());
                        }
                    });
                } else {
                    Log.e("AddUser", "AddUserToGroup sikertelen. HTTP kód: " + response.code());
                    callback.onFailure();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("AddUser", "Hálózati hiba: " + t.getMessage());
                callback.onFailure();
            }
        });
    }


    public void findUserIdByEmail(String email, UserIdCallback callback) {
        String authHeader = sharedPref.getString(MyConst.AUTH, null);
        Call<User> call = userClient.findByEmail(email, authHeader);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getId());
                } else {
                    callback.onFailure();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                callback.onFailure();
            }
        });
    }

    public interface UserIdCallback {
        void onSuccess(long userId);
        void onFailure();
    }


    public interface GroupCallback {
        void onSuccess();
        void onFailure();
    }


    public interface MessageCallback {
        void onSuccess();
        void onFailure();
    }

}