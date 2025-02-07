package com.pethoemilia.client;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.pethoemilia.client.ViewModel.GroupViewModel;
import com.pethoemilia.client.adapter.GroupAdapter;
import com.pethoemilia.client.entity.Group;
import com.pethoemilia.client.entity.User;
import com.pethoemilia.client.service.RefreshService;

import java.util.List;

public class GroupActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private GroupAdapter adapter;
    private GroupViewModel groupViewModel;
    private SharedPreferences sharedPreferences;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        sharedPreferences = getSharedPreferences(MyConst.SHARED_PREF_KEY, Context.MODE_PRIVATE);
        if (sharedPreferences.getString(MyConst.AUTH, "").isEmpty()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        adapter = new GroupAdapter(position -> {
            Group selectedGroup = adapter.getGroups().get(position);
            saveGroupToSharedPreferences(selectedGroup);
            Intent intent = new Intent(GroupActivity.this, ChatActivity.class);
            intent.putExtra("groupId", selectedGroup.getId());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
        user = getUserFromSharedPreferences();

        groupViewModel = new ViewModelProvider(this).get(GroupViewModel.class);
        groupViewModel.getGroupsLiveData().observe(this, groups -> {
            adapter.setGroups(groups);
            for (Group group : groups) {
                groupViewModel.loadMessagesForGroup(group.getId());
            }
            user.sortGroupsByLastMessage();
            adapter.notifyDataSetChanged();
        });

        if (user != null) {
            groupViewModel.loadGroups(user.getId());
        } else {
            Log.e("GroupActivity", "User not found in SharedPreferences");
        }

        startService(new Intent(this, RefreshService.class));
    }

    private void saveGroupToSharedPreferences(Group group) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        editor.putString(MyConst.GROUP, gson.toJson(group));
        editor.apply();
    }

    private User getUserFromSharedPreferences() {
        String userJson = sharedPreferences.getString(MyConst.USER, null);
        return userJson != null ? new Gson().fromJson(userJson, User.class) : null;
    }
}
