package com.pethoemilia.client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pethoemilia.client.adapter.GroupAdapter;
import com.pethoemilia.client.entity.Group;
import com.pethoemilia.client.entity.User;
import com.pethoemilia.client.ViewModel.GroupViewModel;
import com.pethoemilia.client.service.RefreshService;

import java.util.Collections;
import java.util.UUID;


public class GroupActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private GroupAdapter adapter;
    private GroupViewModel viewModel;
    private Button logoutButton;
    private User user;

    private final BroadcastReceiver groupUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (user != null) {
                viewModel.loadGroups(user.getId(), GroupActivity.this); // Újratölti a csoportokat
            }
        }
    };

    private boolean isReceiverRegistered = false;
    @Override
    protected void onResume() {
        super.onResume();
        if (!isReceiverRegistered) {
            IntentFilter filter = new IntentFilter("com.pethoemilia.UPDATE_GROUPS");
            registerReceiver(groupUpdateReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
            isReceiverRegistered = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isReceiverRegistered) {
            unregisterReceiver(groupUpdateReceiver);
            isReceiverRegistered = false;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences sharedPref = getSharedPreferences(MyConst.SHARED_PREF_KEY, Context.MODE_PRIVATE);
        if (sharedPref.getString(MyConst.AUTH, "").isEmpty()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        manageApplicationKey();

        viewModel = new ViewModelProvider(this).get(GroupViewModel.class);
        user = viewModel.getUserFromSharedPreferences(this);

        adapter = new GroupAdapter(position -> {
            Group selectedGroup = adapter.getGroups().get(position);
            viewModel.saveGroupToSharedPreferences(this, selectedGroup);
            Intent intent = new Intent(GroupActivity.this, ChatActivity.class);
            intent.putExtra("groupId", selectedGroup.getId());
            startActivity(intent);
        });

        adapter.setUser(user);
        recyclerView.setAdapter(adapter);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        viewModel.getGroups().observe(this, groups -> {
//            adapter.setGroups(groups);
//            adapter.notifyDataSetChanged();
//        });

        viewModel.getGroups().observe(this, groups -> {
            if (groups != null && !groups.isEmpty()) {
                Log.d("GroupActivity", "Kapott csoportok száma: " + groups.size());

                for (Group group : groups) {
                    Log.d("GroupActivity", "Csoport: " + group.getName() + ", Timestamp: " + group.getLastMessageTimestamp());
                }

                try {
                    // Null értékek kezelése a rendezésnél
                    Collections.sort(groups, (g1, g2) -> {
                        Long t1 = g1.getLastMessageTimestamp();
                        Long t2 = g2.getLastMessageTimestamp();

                        if (t1 == null && t2 == null) return 0;  // Ha mindkettő null, maradjon az eredeti sorrend
                        if (t1 == null) return 1;  // Ha az első null, a második elé kerül
                        if (t2 == null) return -1; // Ha a második null, az első elé kerül

                        return Long.compare(t2, t1); // Csökkenő sorrend
                    });

                    Log.d("GroupActivity", "Csoportok rendezés után:");
                    for (Group group : groups) {
                        Log.d("GroupActivity", "Csoport: " + group.getName() + ", Timestamp: " + group.getLastMessageTimestamp());
                    }

                    adapter.setGroups(groups);
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Log.e("GroupActivity", "Hiba a csoportok rendezésekor: " + e.getMessage());
                }
            } else {
                Log.e("GroupActivity", "Nem érkezett csoportlista vagy üres.");
            }
        });

        if (user != null) {
            viewModel.loadGroups(user.getId(), this);
        } else {
            Log.e("GroupActivity", "User not found in SharedPreferences");
        }

        startService(new Intent(this, RefreshService.class));
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

    private void manageApplicationKey(){
        SharedPreferences sharedPref = getSharedPreferences(MyConst.SHARED_PREF_KEY, Context.MODE_PRIVATE);
        if (!sharedPref.contains(MyConst.APPLICATION_KEY)){
            String applicationKey = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(MyConst.APPLICATION_KEY, applicationKey);
            editor.apply();
        }

    }

}
//