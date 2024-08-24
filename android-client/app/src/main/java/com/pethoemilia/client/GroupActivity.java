package com.pethoemilia.client;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.pethoemilia.client.adapter.GroupAdapter;
import com.pethoemilia.client.api.GroupClient;
import com.pethoemilia.client.api.MessageClient;
import com.pethoemilia.client.entity.Group;
import com.pethoemilia.client.entity.Message;
import com.pethoemilia.client.entity.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GroupActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private GroupAdapter adapter;
    private GroupClient groupClient;
    private MessageClient messageClient;

    private static final String PREFS_GROUP = "GroupPrefs";
    private static final String KEY_GROUP = "group";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadGroupFromSharedPreferences();

        adapter = new GroupAdapter(new GroupAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Group selectedGroup = adapter.getGroups().get(position);
                saveGroupToSharedPreferences(selectedGroup);
                Intent intent = new Intent(GroupActivity.this, ChatActivity.class);
                intent.putExtra("groupId", selectedGroup.getId());
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(adapter);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.104:8080/") // Backend base URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        groupClient = retrofit.create(GroupClient.class);
        messageClient = retrofit.create(MessageClient.class);

        // Load user from SharedPreferences
        User user = getUserFromSharedPreferences();
        if (user != null) {
            long userId = user.getId();
            loadGroups(userId);
        } else {
            // Handle the case where user is not available
            Log.e("MainActivity2", "User not found in SharedPreferences");
        }
    }

    private User getUserFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userJson = sharedPreferences.getString("user", null);
        if (userJson != null) {
            Gson gson = new Gson();
            return gson.fromJson(userJson, User.class);
        }
        return null; // Return null if no user found
    }

    private void loadGroups(long userId) {
        Call<List<Group>> call = groupClient.findByUserId(userId);
        call.enqueue(new Callback<List<Group>>() {
            @Override
            public void onResponse(Call<List<Group>> call, Response<List<Group>> response) {
                if (response.isSuccessful()) {
                    List<Group> groups = response.body();
                    if (groups != null) {
                        adapter.setGroups(groups);
                        for (Group group : groups) {
                            loadMessagesForGroup(group.getId());
                        }
                    }
                } else {
                    // Handle API errors
                }
            }

            @Override
            public void onFailure(Call<List<Group>> call, Throwable t) {
                // Handle network errors
            }
        });
    }

    private void loadMessagesForGroup(Long groupId) {
        Call<List<Message>> call = messageClient.findByGroupId(groupId);
        call.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                if (response.isSuccessful()) {
                    List<Message> messages = response.body();
                    Log.d("MainActivity2", "Messages for group " + groupId + ": " + messages);
                    if (messages != null) {
                        for (Group group : adapter.getGroups()) {
                            if (group.getId().equals(groupId)) {
                                group.setMessages(messages);
                                break;
                            }
                        }
                        adapter.notifyDataSetChanged(); // Update adapter
                    }
                } else {
                    // Handle API errors
                    Log.e("MainActivity2", "API error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                // Handle network errors
                Log.e("MainActivity2", "Network error", t);
            }
        });
    }
    private void loadGroupFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_GROUP, Context.MODE_PRIVATE);
        String userJson = sharedPreferences.getString(KEY_GROUP, null);

        if (userJson != null) {
            Gson gson = new Gson();
            Group group = gson.fromJson(userJson, Group.class); // JSON konvertálása User objektummá
        }
    }


    private void saveGroupToSharedPreferences(Group group) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_GROUP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String groupJson = gson.toJson(group); // User objektum konvertálása JSON formátumba

        editor.putString(KEY_GROUP, groupJson);
        editor.apply(); // Adatok elmentése
    }
}
