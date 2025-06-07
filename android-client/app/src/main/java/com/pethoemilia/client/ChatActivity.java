package com.pethoemilia.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private ChatViewModel chatViewModel;
    private TextView chatNameTextView;
    private EditText editTextMessage;
    private Button buttonSend;
    private EditText editTextEmail;
    private Button buttonAddUser;
    private LinearLayout addUserLayout;

    private Group currentGroup;

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
        editTextEmail = findViewById(R.id.editTextEmail);
        buttonAddUser = findViewById(R.id.buttonAddUser);
        addUserLayout = findViewById(R.id.add_user);

        ImageView buttonToggleAddUser = findViewById(R.id.buttonAdd); // Plusz ikon
        ImageView buttonDeleteGroup = findViewById(R.id.buttonDeleteGroup); // Kuka ikon

        currentGroup = getGroupFromSharedPreferences();
        if (currentGroup != null) {
            User currentUser = getUserFromSharedPreferences();
            if (currentGroup.getUsers().size() == 2) {
                for (User member : currentGroup.getUsers()) {
                    if (!member.getId().equals(currentUser.getId())) {
                        chatNameTextView.setText(member.getName());
                        break;
                    }
                }
            } else {
                chatNameTextView.setText(currentGroup.getName());
            }

            chatViewModel.getMessages(currentGroup.getId()).observe(this, messages -> {
                adapter.setMessages(messages);
                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
            });

            buttonSend.setOnClickListener(v -> sendMessage());
            buttonAddUser.setOnClickListener(v -> addUserByEmail());
            buttonToggleAddUser.setOnClickListener(v -> {
                if (addUserLayout.getVisibility() == View.GONE) {
                    addUserLayout.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    editTextMessage.setVisibility(View.GONE);
                    buttonSend.setVisibility(View.GONE);
                } else {
                    addUserLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    editTextMessage.setVisibility(View.VISIBLE);
                    buttonSend.setVisibility(View.VISIBLE);
                }
            });

            buttonDeleteGroup.setOnClickListener(v -> {
                Toast.makeText(this, "Csoport törlés funkció még nincs implementálva", Toast.LENGTH_SHORT).show();
                // Ide jöhet a törlési logika, ha később szükséges
            });
        }
    }

    private void sendMessage() {
        String messageContent = editTextMessage.getText().toString().trim();
        if (!messageContent.isEmpty()) {
            User sender = getUserFromSharedPreferences();
            if (sender != null && currentGroup != null) {
                chatViewModel.sendMessage(new Message(messageContent, currentGroup, sender));
                editTextMessage.setText("");
            } else {
                Log.e("ChatActivity", "Missing user or group");
            }
        }
    }

    private void addUserByEmail() {
        String email = editTextEmail.getText().toString().trim();
        if (!email.isEmpty()) {
            chatViewModel.addUserToGroupByEmail(currentGroup.getId(), email,
                    () -> {
                        currentGroup = getGroupFromSharedPreferences();
                        Toast.makeText(this, "Sikeresen hozzáadva", Toast.LENGTH_SHORT).show();
                    },
                    () -> Toast.makeText(this, "Nem sikerült hozzáadni", Toast.LENGTH_SHORT).show()
            );
        } else {
            Toast.makeText(this, "Írj be egy email címet", Toast.LENGTH_SHORT).show();
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
