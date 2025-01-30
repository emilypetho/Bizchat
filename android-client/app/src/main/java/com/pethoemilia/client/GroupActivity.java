package com.pethoemilia.client;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

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
import com.pethoemilia.client.service.RefreshService;

import java.util.HashSet;
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
    private Button logoutButton;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("ellenorzes", getUserFromSharedPreferences().getEmail());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences sharedPref = getSharedPreferences(MyConst.SHARED_PREF_KEY, Context.MODE_PRIVATE);
//!sharedPref.getBoolean(MyConst.REMEMBER_ME,false) ||
        if(sharedPref.getString(MyConst.AUTH, "").isEmpty()){
            Intent intent = new Intent(GroupActivity.this, LoginActivity.class);
            startActivity(intent);
        }

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

        //recyclerView.setAdapter(adapter);

//        Group group = getGroupFromSharedPreferences();
//        if (group != null) {
//            User currentUser = getUserFromSharedPreferences();
//            if (group.getUsers().size() == 2) {
//                for (User member : group.getUsers()) {
//                    if (!member.getId().equals(currentUser.getId())) {
//                        adapter.setUser(member);
//                        break;
//                    }
//                }
//            }
//        } else {
//            Log.e("ChatActivity", "Group not found in SharedPreferences");
//        }

        User currentUser = getUserFromSharedPreferences();
        adapter.setUser(currentUser);
        recyclerView.setAdapter(adapter);

//        Group group = getGroupFromSharedPreferences();
//        //chatNameTextView.setText(group.getName());
//        if (group != null) {
//            User currentUser = getUserFromSharedPreferences();
//            if (group.getUsers() != null && !group.getUsers().isEmpty()) {
//                for (User member : group.getUsers()) {
//                    if (!member.getId().equals(currentUser.getId())) {
//                        //chatNameTextView.setText(member.getName());
//                        adapter.setUser(member);
//                        break;
//                    }
//                }
//            } else {
//                //chatNameTextView.setText("No members in the group");
//            }
//        } else {
//            Log.e("ChatActivity", "Group not found in SharedPreferences");
//        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MyConst.URL) // Backend base URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        groupClient = retrofit.create(GroupClient.class);
        messageClient = retrofit.create(MessageClient.class);

        // Load user from SharedPreferences
        user = getUserFromSharedPreferences();
        if (user != null) {
            long userId = user.getId();
            loadGroups(userId);
        } else {
            // Handle the case where user is not available
            Log.e("MainActivity2", "User not found in SharedPreferences");
        }
//        logoutButton.setOnClickListener(view -> {
//            Intent intent = new Intent(GroupActivity.this, LoginActivity.class);
//            startActivity(intent);
//        });
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        startService(new Intent(this, RefreshService.class));
    }

    private Group getGroupFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(MyConst.SHARED_PREF_KEY, Context.MODE_PRIVATE);
        String groupJson = sharedPreferences.getString(MyConst.GROUP, null);
        Gson gson = new Gson();
        return gson.fromJson(groupJson, Group.class);
    }

    private User getUserFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(MyConst.SHARED_PREF_KEY, Context.MODE_PRIVATE);
        String userJson = sharedPreferences.getString(MyConst.USER, null);
        if (userJson != null) {
            Gson gson = new Gson();
            return gson.fromJson(userJson, User.class);
        }
        return null;
    }
    private void loadGroups(long userId) {
        SharedPreferences sharedPreferences = getSharedPreferences(MyConst.SHARED_PREF_KEY, Context.MODE_PRIVATE);
        String encodedcredentials = sharedPreferences.getString(MyConst.AUTH, null);
        Call<List<Group>> call = groupClient.findByUserId(userId,encodedcredentials);
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
                        // Rendezés az utolsó üzenet szerint
                        user.sortGroupsByLastMessage();
                        adapter.setGroups(groups);  // Az adapter frissítése a rendezett csoportokkal
                        recyclerView.setAdapter(adapter);  // Adapter beállítása
                    }
                } else {
                    Log.e("Group","load group♥h "+response);
                    if(response.code() == 401){
                        Intent intent = new Intent(GroupActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Group>> call, Throwable t) {
                // Handle network errors
            }
        });
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
                    Log.d("MainActivity2", "Messages for group " + groupId + ": " + messages);
                    if (messages != null) {
                        for (Group group : adapter.getGroups()) {
                            if (group.getId().equals(groupId)) {
                                group.setMessages(messages);
                                break;
                            }
                        }
                        user.sortGroupsByLastMessage();
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    // Handle API errors
                    Log.e("MainActivity2", "API error: " + response.message());
                    Log.e("Group","load message");
                    if(response.code() == 401){
                        Intent intent = new Intent(GroupActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
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
        SharedPreferences sharedPreferences = getSharedPreferences(MyConst.SHARED_PREF_KEY, Context.MODE_PRIVATE);
        String userJson = sharedPreferences.getString(MyConst.GROUP, null);
        if (userJson != null) {
            Gson gson = new Gson();
            Group group = gson.fromJson(userJson, Group.class); // JSON konvertálása User objektummá
        }
    }


    private void saveGroupToSharedPreferences(Group group) {
        SharedPreferences sharedPreferences = getSharedPreferences(MyConst.SHARED_PREF_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String groupJson = gson.toJson(group); // User objektum konvertálása JSON formátumba

        editor.putString(MyConst.GROUP, groupJson);
        editor.apply(); // Adatok elmentése
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_new_chat) {
            Intent intent = new Intent(this, NewChatActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_new_group) {
            Intent intent = new Intent(this, NewGroupActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_logout) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    private void logout() {
//        SharedPreferences sharedPreferences = getSharedPreferences(MyConst.SHARED_PREF_KEY, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.clear();
//        editor.apply();
//
//        Intent intent = new Intent(this, LoginActivity.class);
//        startActivity(intent);
//        finish();
//    }

}
