package com.pethoemilia.client;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.pethoemilia.client.api.GroupClient;
import com.pethoemilia.client.api.UserClient;
import com.pethoemilia.client.entity.Group;
import com.pethoemilia.client.entity.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewGroupActivity extends AppCompatActivity {

    private EditText groupNameEditText;
    private LinearLayout userLinearLayout;
    private Button createGroupButton;
    private Button addUserButton;
    private GroupClient groupClient;
    private User currentUser;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        // View binding
        groupNameEditText = findViewById(R.id.editTextGroupName);
        userLinearLayout = findViewById(R.id.linearLayoutUserId);
        createGroupButton = findViewById(R.id.buttonCreateGroup);
        addUserButton = findViewById(R.id.buttonAddUser);

        // Retrofit setup
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MyConst.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        groupClient = retrofit.create(GroupClient.class);

        // Get current user from shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences(MyConst.SHARED_PREF_KEY, Context.MODE_PRIVATE);
        String userString = sharedPreferences.getString(MyConst.USER, null);
        if (userString != null) {
            currentUser = new Gson().fromJson(userString, User.class);
        }

        // Add new user input field when the button is clicked
        addUserButton.setOnClickListener(v -> addNewUserInput());

        // Button click listener for creating group
        createGroupButton.setOnClickListener(view -> createGroup());
    }

    // Method to add a new EditText field dynamically
    private void addNewUserInput() {
        EditText newUserInput = new EditText(this);
        newUserInput.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        newUserInput.setHint("Felhasználó e-mail");
        newUserInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        userLinearLayout.addView(newUserInput);
    }

    private void createGroup() {
        String groupName = groupNameEditText.getText().toString();

        if (groupName.isEmpty()) {
            Toast.makeText(this, "A csoport név nem lehet üres!", Toast.LENGTH_SHORT).show();
            return;
        }

        // List to hold user emails
        List<String> userEmails = new ArrayList<>();

        // Iterate over all EditText fields to get the emails
        for (int i = 0; i < userLinearLayout.getChildCount(); i++) {
            EditText editText = (EditText) userLinearLayout.getChildAt(i);
            String email = editText.getText().toString().trim();
            if (!email.isEmpty()) {
                userEmails.add(email);
            }
        }

        if (userEmails.isEmpty()) {
            Toast.makeText(this, "Adj hozzá legalább egy felhasználót!", Toast.LENGTH_SHORT).show();
            return;
        }

        String authHeader = getSharedPreferences(MyConst.SHARED_PREF_KEY, Context.MODE_PRIVATE)
                .getString(MyConst.AUTH, null);

        // Request users by email
        getUsersByEmails(userEmails, authHeader, users -> {
            if (users.size() == userEmails.size()) {
                createGroupWithUsers(groupName, users, authHeader);
            } else {
                Toast.makeText(NewGroupActivity.this, "Néhány e-mail cím nem található.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Fetch multiple users by email
    private void getUsersByEmails(List<String> emails, String authHeader, UsersCallback callback) {
        UserClient userClient = new Retrofit.Builder()
                .baseUrl(MyConst.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UserClient.class);

        List<Call<User>> calls = new ArrayList<>();
        for (String email : emails) {
            calls.add(userClient.findByEmail(email, authHeader));
        }

        List<User> users = new ArrayList<>();  // List to store users from the responses

        // Iterate over each call and handle the response correctly
        for (Call<User> call : calls) {
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        users.add(response.body());  // Add the user to the list

                        // If all users have been fetched, invoke the callback
                        if (users.size() == emails.size()) {
                            callback.onResult(users);
                        }
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.e("NewGroupActivity", "Error fetching user by email: " + t.getMessage());
                }
            });
        }
    }

    // Create group with users
    private void createGroupWithUsers(String groupName, List<User> users, String authHeader) {
        Group newGroup = new Group();
        newGroup.setName(groupName);

        Set<User> userSet = new HashSet<>();
        userSet.add(currentUser);  // Add the current user to the group
        userSet.addAll(users);      // Add the fetched users

        newGroup.setUsers(userSet);
        newGroup.setFlag(true);

        GroupClient groupClient = new Retrofit.Builder()
                .baseUrl(MyConst.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GroupClient.class);

        Call<Group> call = groupClient.saveGroup(newGroup, authHeader);
        call.enqueue(new Callback<Group>() {
            @Override
            public void onResponse(Call<Group> call, Response<Group> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(NewGroupActivity.this, "Csoport sikeresen létrehozva!", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity
                } else {
                    Toast.makeText(NewGroupActivity.this, "Hiba történt a csoport létrehozásakor.", Toast.LENGTH_SHORT).show();
                    Log.e("NewGroupActivity", "Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Group> call, Throwable t) {
                Toast.makeText(NewGroupActivity.this, "Hálózati hiba.", Toast.LENGTH_SHORT).show();
                Log.e("NewGroupActivity", "Failure: " + t.getMessage());
            }
        });
    }

    // Callback interface to handle multiple users
    interface UsersCallback {
        void onResult(List<User> users);
    }
}
