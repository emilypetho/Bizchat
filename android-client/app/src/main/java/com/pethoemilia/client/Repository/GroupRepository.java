package com.pethoemilia.client.Repository;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.pethoemilia.client.LoginActivity;
import com.pethoemilia.client.MyConst;
import com.pethoemilia.client.api.GroupClient;
import com.pethoemilia.client.api.MessageClient;
import com.pethoemilia.client.entity.Group;
import com.pethoemilia.client.entity.Message;
import com.pethoemilia.client.entity.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GroupRepository {
    private final GroupClient groupClient;
    private final MessageClient messageClient;

    public interface GroupCallback {
        void onGroupsLoaded(List<Group> groups);
    }
    public interface GroupCreationCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }
    public GroupRepository() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MyConst.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        groupClient = retrofit.create(GroupClient.class);
        messageClient = retrofit.create(MessageClient.class);
    }

    public void createChatWithUser(String groupName, User currentUser, User user1, String authHeader, GroupCreationCallback callback) {
        Group newGroup = new Group();
        newGroup.setName(groupName);

        Set<User> users = new HashSet<>();
        users.add(currentUser);
        users.add(user1);

        newGroup.setUsers(users);

        Call<Group> call = groupClient.saveGroup(newGroup, authHeader);
        call.enqueue(new Callback<Group>() {
            @Override
            public void onResponse(Call<Group> call, Response<Group> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onFailure("Csoport létrehozása sikertelen, kód: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Group> call, Throwable t) {
                callback.onFailure("Hálózati hiba: " + t.getMessage());
            }
        });
    }
    public void createGroupWithUsers(String groupName, User currentUser, List<User> users, String authHeader, GroupCreationCallback callback) {
        Group newGroup = new Group();
        newGroup.setName(groupName);

        Set<User> userSet = new HashSet<>();
        userSet.add(currentUser);  // Add the current user
        userSet.addAll(users);      // Add the fetched users
        newGroup.setUsers(userSet);

        Call<Group> call = groupClient.saveGroup(newGroup, authHeader);
        call.enqueue(new Callback<Group>() {
            @Override
            public void onResponse(Call<Group> call, Response<Group> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onFailure("Hiba történt a csoport létrehozásakor.");
                    Log.e("GroupRepository", "Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Group> call, Throwable t) {
                callback.onFailure("Hálózati hiba.");
                Log.e("GroupRepository", "Failure: " + t.getMessage());
            }
        });
    }

    public void loadGroups(long userId, Context context, GroupCallback callback) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MyConst.SHARED_PREF_KEY, Context.MODE_PRIVATE);
        String encodedCredentials = sharedPreferences.getString(MyConst.AUTH, null);

        Call<List<Group>> call = groupClient.findByUserId(userId, encodedCredentials);
        call.enqueue(new Callback<List<Group>>() {
            @Override
            public void onResponse(Call<List<Group>> call, Response<List<Group>> response) {
                if (response.isSuccessful()) {
                    List<Group> groups = response.body();
                    if (groups != null) {
                        loadMessagesForGroups(groups, context, callback);
                    } else {
                        callback.onGroupsLoaded(null);
                    }
                } else {
                    handleUnauthorized(context);
                }
            }

            @Override
            public void onFailure(Call<List<Group>> call, Throwable t) {
                Log.e("GroupRepository", "Network error", t);
            }
        });
    }

    private void loadMessagesForGroups(List<Group> groups, Context context, GroupCallback callback) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MyConst.SHARED_PREF_KEY, Context.MODE_PRIVATE);
        String encodedCredentials = sharedPreferences.getString(MyConst.AUTH, null);

        for (Group group : groups) {
            Call<List<Message>> call = messageClient.findByGroupId(group.getId(), encodedCredentials);
            call.enqueue(new Callback<List<Message>>() {
                @Override
                public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                    if (response.isSuccessful()) {
                        group.setMessages(response.body());
                    } else {
                        handleUnauthorized(context);
                    }
                    callback.onGroupsLoaded(groups);

                    // Notify the app that groups have been updated
                    Intent intent = new Intent("com.pethoemilia.client.UPDATE_GROUPS");
                    context.sendBroadcast(intent);
                }

                @Override
                public void onFailure(Call<List<Message>> call, Throwable t) {
                    Log.e("GroupRepository", "Network error", t);
                }
            });
        }
    }

    private void handleUnauthorized(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }
}
//