package com.pethoemilia.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.pethoemilia.client.adapter.ChatAdapter;
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

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private MessageClient messageClient;
    private TextView chatNameTextView;
    private EditText editTextMessage;
    private Button buttonSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatAdapter(this);
        recyclerView.setAdapter(adapter);

        chatNameTextView = findViewById(R.id.chat_name);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MyConst.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        messageClient = retrofit.create(MessageClient.class);

        Group group = getGroupFromSharedPreferences();
        chatNameTextView.setText(group.getName());

        loadMessagesForGroup(group.getId());

        buttonSend.setOnClickListener(v -> sendMessage());
    }

    private User getUserFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(MyConst.SHARED_PREF_KEY, Context.MODE_PRIVATE);
        String userJson = sharedPreferences.getString(MyConst.USER, null);
        if (userJson != null) {
            Gson gson = new Gson();
            return gson.fromJson(userJson, User.class);
        }
        return null; // Return null if no user found
    }

    private Group getGroupFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(MyConst.SHARED_PREF_KEY, Context.MODE_PRIVATE);
        String groupJson = sharedPreferences.getString(MyConst.GROUP, null);
            Gson gson = new Gson();
            return gson.fromJson(groupJson, Group.class);
    }

    private void sendMessage() {
        String messageContent = editTextMessage.getText().toString().trim();
        SharedPreferences sharedPreferences = getSharedPreferences(MyConst.SHARED_PREF_KEY, Context.MODE_PRIVATE);
        String encodedcredentials = sharedPreferences.getString(MyConst.AUTH, null);
        if (!messageContent.isEmpty()) {
            Group group = getGroupFromSharedPreferences();
            User sender = getUserFromSharedPreferences();
            if (sender != null) {
                Call<Message> call = messageClient.save(new Message(messageContent,group,sender),encodedcredentials);
                call.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        if (response.isSuccessful()) {
                            editTextMessage.setText("");
                            loadMessagesForGroup(group.getId());
                        } else {
                            Log.e("ChatActivity", "Failed to send message: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<Message> call, Throwable t) {
                        Log.e("ChatActivity", "Network error", t);
                    }
                });
            } else {
                // Handle the case where user is not available
                Log.e("MainActivity2", "User not found in SharedPreferences");
            }
        }
    }

    private void loadMessagesForGroup(Long groupId) {
        SharedPreferences sharedPreferences = getSharedPreferences(MyConst.SHARED_PREF_KEY, Context.MODE_PRIVATE);
        String encodedcredentials = sharedPreferences.getString(MyConst.AUTH, null);
        Call<List<Message>> call = messageClient.findByGroupId(groupId,encodedcredentials);
        call.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                if (response.isSuccessful()) {
                    List<Message> messages = response.body();
                    if (messages != null) {
                        adapter.setMessages(messages);
                        recyclerView.scrollToPosition(adapter.getItemCount() - 1); // Scroll to the last message
                    }
                } else {
                    Log.e("ChatActivity", "API error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                Log.e("ChatActivity", "Network error", t);
            }
        });
    }
}
