package com.pethoemilia.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.pethoemilia.client.ViewModel.ChatViewModel;
import com.pethoemilia.client.adapter.ChatAdapter;
import com.pethoemilia.client.api.MessageClient;
import com.pethoemilia.client.entity.Group;
import com.pethoemilia.client.entity.Message;
import com.pethoemilia.client.entity.User;
import com.pethoemilia.client.service.RefreshService;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;
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
    private ChatViewModel chatViewModel;
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

        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        chatNameTextView = findViewById(R.id.chat_name);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MyConst.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        messageClient = retrofit.create(MessageClient.class);

        Group group = getGroupFromSharedPreferences();

        if (group != null) {
            User currentUser = getUserFromSharedPreferences();
            if (group.getUsers().size() == 2) {
                for (User member : group.getUsers()) {
                    if (!member.getId().equals(currentUser.getId())) {
                        chatNameTextView.setText(member.getName());
                        break;
                    }
                }
            } else {
                chatNameTextView.setText(group.getName());
            }
        } else {
            Log.e("ChatActivity", "Group not found in SharedPreferences");
        }

        chatViewModel.getMessages().observe(this, messages -> {
            adapter.setMessages(messages);
            recyclerView.scrollToPosition(adapter.getItemCount() - 1);
        });

        loadMessagesForGroup(group.getId());

        buttonSend.setOnClickListener(v -> sendMessage());
    }

    private User getUserFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(MyConst.SHARED_PREF_KEY, Context.MODE_PRIVATE);
        String userJson = sharedPreferences.getString(MyConst.USER, null);
        if (userJson != null) {
            return new Gson().fromJson(userJson, User.class);
        }
        return null;
    }

    private Group getGroupFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(MyConst.SHARED_PREF_KEY, Context.MODE_PRIVATE);
        String groupJson = sharedPreferences.getString(MyConst.GROUP, null);
        return new Gson().fromJson(groupJson, Group.class);
    }

    private void sendMessage() {
        String messageContent = editTextMessage.getText().toString().trim();
        SharedPreferences sharedPreferences = getSharedPreferences(MyConst.SHARED_PREF_KEY, Context.MODE_PRIVATE);
        String encodedCredentials = sharedPreferences.getString(MyConst.AUTH, null);

        if (!messageContent.isEmpty()) {
            Group group = getGroupFromSharedPreferences();
            User sender = getUserFromSharedPreferences();

            if (sender != null) {
                Call<Message> call = messageClient.save(new Message(messageContent, group, sender), encodedCredentials);
                call.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            editTextMessage.setText("");
                            chatViewModel.addMessage(response.body());
                            recyclerView.scrollToPosition(adapter.getItemCount() - 1);
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
                Log.e("ChatActivity", "User not found in SharedPreferences");
            }
        }
    }

//    private void sendMessageToQueue(String message) {
//        new Thread(() -> {
//            try {
//                Log.d("RabbitMQ", "Sending message: " + message); // Naplózás
//                ConnectionFactory factory = new ConnectionFactory();
//                factory.setUsername("guest");
//                factory.setPassword("guest");
//                factory.setHost("192.168.0.112");
//                factory.setPort(5672);
//
//                Connection connection = factory.newConnection();
//                Channel channel = connection.createChannel();
//
//                channel.basicPublish("", "chatQueue", null, message.getBytes(StandardCharsets.UTF_8));
//                Log.d("RabbitMQ", "Message sent successfully"); // Naplózás
//
//                channel.close();
//                connection.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//                Log.e("RabbitMQ", "Error sending message: " + e.getMessage());
//            }
//        }).start();
//    }

    private void loadMessagesForGroup(Long groupId) {
        SharedPreferences sharedPreferences = getSharedPreferences(MyConst.SHARED_PREF_KEY, Context.MODE_PRIVATE);
        String encodedCredentials = sharedPreferences.getString(MyConst.AUTH, null);
        Call<List<Message>> call = messageClient.findByGroupId(groupId, encodedCredentials);
        call.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    chatViewModel.setMessages(response.body());
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
