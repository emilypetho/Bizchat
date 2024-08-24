package com.pethoemilia.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pethoemilia.client.adapter.GroupAdapter;
import com.pethoemilia.client.api.GroupClient;
import com.pethoemilia.client.api.MessageClient;
import com.pethoemilia.client.entity.Group;
import com.pethoemilia.client.entity.GroupSession;
import com.pethoemilia.client.entity.Message;
import com.pethoemilia.client.entity.UserSession;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity2 extends AppCompatActivity {
    private RecyclerView recyclerView;
    private GroupAdapter adapter;
    private GroupClient groupClient;
    private MessageClient messageClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new GroupAdapter(new GroupAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Group selectedGroup = adapter.getGroups().get(position);
                GroupSession.setGroup(selectedGroup);
                Intent intent = new Intent(MainActivity2.this, ChatActivity.class);
                intent.putExtra("groupId", selectedGroup.getId());
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(adapter);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.111:8080/") // Backend base URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        groupClient = retrofit.create(GroupClient.class);
        messageClient = retrofit.create(MessageClient.class);

        long userId = UserSession.getUser().getId();
        loadGroups(userId);
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
                        adapter.notifyDataSetChanged(); // Frissítjük az adaptert
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
}

