package com.pethoemilia.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.pethoemilia.client.entity.User;
import com.pethoemilia.client.viewmodel.NewChatViewModel;

public class NewChatActivity extends AppCompatActivity {

    private EditText userId1EditText;
    private Button createGroupButton;
    private NewChatViewModel viewModel;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_chat);

        // View binding
        userId1EditText = findViewById(R.id.editTextUserId1);
        createGroupButton = findViewById(R.id.buttonCreateGroup);

        // ViewModel inicializálása
        viewModel = new ViewModelProvider(this).get(NewChatViewModel.class);

        // Felhasználó betöltése SharedPreferences-ből
        SharedPreferences sharedPreferences = getSharedPreferences(MyConst.SHARED_PREF_KEY, Context.MODE_PRIVATE);
        String userString = sharedPreferences.getString(MyConst.USER, null);
        if (userString != null) {
            currentUser = new Gson().fromJson(userString, User.class);
        }

        // Gomb eseménykezelő
        createGroupButton.setOnClickListener(view -> {
            String userEmail = userId1EditText.getText().toString();
            if (userEmail.isEmpty()) {
                Toast.makeText(this, "Minden mezőt ki kell tölteni!", Toast.LENGTH_SHORT).show();
                return;
            }

            String authHeader = sharedPreferences.getString(MyConst.AUTH, null);
            viewModel.createGroup(currentUser, userEmail, authHeader, this);
        });

        // Eredmények figyelése
        viewModel.getToastMessage().observe(this, message ->
                Toast.makeText(NewChatActivity.this, message, Toast.LENGTH_SHORT).show());

        viewModel.getCloseActivity().observe(this, shouldClose -> {
            if (shouldClose) finish();
        });
    }
}






//package com.pethoemilia.client;
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.text.InputType;
//import android.util.Log;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.gson.Gson;
//import com.pethoemilia.client.api.GroupClient;
//import com.pethoemilia.client.api.UserClient;
//import com.pethoemilia.client.entity.Group;
//import com.pethoemilia.client.entity.User;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//
//public class NewChatActivity extends AppCompatActivity {
//
//    private EditText groupNameEditText;
//    private EditText userId1EditText;
//    private EditText userId2EditText;
//    private Button createGroupButton;
//    private GroupClient groupClient;
//    private User currentUser;
//
//    @SuppressLint("MissingInflatedId")
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_new_chat);
//
//        // View binding
//        groupNameEditText = findViewById(R.id.editTextGroupName);
//        userId1EditText = findViewById(R.id.editTextUserId1);
//        createGroupButton = findViewById(R.id.buttonCreateGroup);
//
//        // Retrofit setup
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(MyConst.URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        groupClient = retrofit.create(GroupClient.class);
//
//        // Get current user from shared preferences
//        SharedPreferences sharedPreferences = getSharedPreferences(MyConst.SHARED_PREF_KEY, Context.MODE_PRIVATE);
//        String userString = sharedPreferences.getString(MyConst.USER, null);
//        if (userString != null) {
//            currentUser = new Gson().fromJson(userString, User.class);
//        }
//
//        // Button click listener
//        createGroupButton.setOnClickListener(view -> createGroup());
//    }
//
//    private void createGroup() {
//        String groupName = "Chat";
//        String userEmail1 = userId1EditText.getText().toString();
//
//        if (userEmail1.isEmpty()) {
//            Toast.makeText(this, "Minden mezőt ki kell tölteni!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        String authHeader = getSharedPreferences(MyConst.SHARED_PREF_KEY, Context.MODE_PRIVATE)
//                .getString(MyConst.AUTH, null);
//
//        // Lekérések a felhasználókhoz e-mail alapján
//        getUserByEmail(userEmail1, authHeader, user1 -> {
//            if (user1 != null) {
////                getUserByEmail(userEmail2, authHeader, user2 -> {
////                    if (user2 != null) {
////                        // Csoport létrehozása
////                        createGroupWithUsers(groupName, user1, user2, authHeader);
////                    } else {
////                        Toast.makeText(NewChatActivity.this, "A második e-mail cím nem található.", Toast.LENGTH_SHORT).show();
////                    }
////                });
//                createGroupWithUsers(groupName, user1, authHeader);
//            } else {
//                Toast.makeText(NewChatActivity.this, "Az első e-mail cím nem található.", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    // Felhasználó lekérése e-mail alapján
//    private void getUserByEmail(String email, String authHeader, UserCallback callback) {
//        UserClient userClient = new Retrofit.Builder()
//                .baseUrl(MyConst.URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build()
//                .create(UserClient.class);
//
//        Call<User> call = userClient.findByEmail(email, authHeader);
//        call.enqueue(new Callback<User>() {
//            @Override
//            public void onResponse(Call<User> call, Response<User> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    callback.onResult(response.body());
//                } else {
//                    callback.onResult(null);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<User> call, Throwable t) {
//                Log.e("NewGroupActivity", "Error fetching user by email: " + t.getMessage());
//                callback.onResult(null);
//            }
//        });
//    }
//
//    // Csoport létrehozása a lekért felhasználókkal
//    private void createGroupWithUsers(String groupName, User user1, String authHeader) {
//        Group newGroup = new Group();
//        newGroup.setName(groupName);
//
//        Set<User> users = new HashSet<>();
//        users.add(currentUser); // Add the current user
//        users.add(user1);
//
//        newGroup.setUsers(users);
////        newGroup.setFlag(false);
//        GroupClient groupClient = new Retrofit.Builder()
//                .baseUrl(MyConst.URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build()
//                .create(GroupClient.class);
//
//        Call<Group> call = groupClient.saveGroup(newGroup, authHeader);
//        call.enqueue(new Callback<Group>() {
//            @Override
//            public void onResponse(Call<Group> call, Response<Group> response) {
//                if (response.isSuccessful()) {
//                    Toast.makeText(NewChatActivity.this, "Csoport sikeresen létrehozva!", Toast.LENGTH_SHORT).show();
//                    finish(); // Close the activity
//                } else {
//                    Toast.makeText(NewChatActivity.this, "Hiba történt a csoport létrehozásakor.", Toast.LENGTH_SHORT).show();
//                    Log.e("NewGroupActivity", "Error: " + response.code());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Group> call, Throwable t) {
//                Toast.makeText(NewChatActivity.this, "Hálózati hiba.", Toast.LENGTH_SHORT).show();
//                Log.e("NewGroupActivity", "Failure: " + t.getMessage());
//            }
//        });
//    }
//
//    // Callback interfész a felhasználó lekéréshez
//    interface UserCallback {
//        void onResult(User user);
//    }
//}