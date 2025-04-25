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
import com.pethoemilia.client.entity.Group;
import com.pethoemilia.client.entity.Message;
import com.pethoemilia.client.entity.User;

import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter adapter;
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

            // Observe the messages from ViewModel
            chatViewModel.getMessages(group.getId()).observe(this, messages -> {
                adapter.setMessages(messages);
                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
            });

            buttonSend.setOnClickListener(v -> sendMessage(group));
        }
    }

    private void sendMessage(Group group) {
        String messageContent = editTextMessage.getText().toString().trim();
        if (!messageContent.isEmpty()) {
            User sender = getUserFromSharedPreferences();
            if (sender != null) {
                chatViewModel.sendMessage(new Message(messageContent, group, sender));
                editTextMessage.setText("");
            } else {
                Log.e("ChatActivity", "User not found in SharedPreferences");
            }
        }
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
}