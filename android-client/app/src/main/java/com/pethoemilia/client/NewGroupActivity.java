package com.pethoemilia.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.pethoemilia.client.ViewModel.NewGroupViewModel;
import com.pethoemilia.client.entity.User;

import java.util.ArrayList;
import java.util.List;

public class NewGroupActivity extends AppCompatActivity {

    private EditText groupNameEditText;
    private LinearLayout userLinearLayout;
    private Button createGroupButton;
    private Button addUserButton;
    private NewGroupViewModel viewModel;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        groupNameEditText = findViewById(R.id.editTextGroupName);
        userLinearLayout = findViewById(R.id.linearLayoutUserId);
        createGroupButton = findViewById(R.id.buttonCreateGroup);
        addUserButton = findViewById(R.id.buttonAddUser);

        // ViewModel és felhasználó betöltése
        viewModel = new ViewModelProvider(this).get(NewGroupViewModel.class);

        SharedPreferences sharedPreferences = getSharedPreferences(MyConst.SHARED_PREF_KEY, Context.MODE_PRIVATE);
        String userString = sharedPreferences.getString(MyConst.USER, null);
        if (userString != null) {
            currentUser = new Gson().fromJson(userString, User.class);
        }

        addUserButton.setOnClickListener(v -> addNewUserInput());

        createGroupButton.setOnClickListener(view -> {
            String groupName = groupNameEditText.getText().toString();
            if (groupName.isEmpty()) {
                Toast.makeText(this, "A csoport név nem lehet üres!", Toast.LENGTH_SHORT).show();
                return;
            }

            List<String> userEmails = new ArrayList<>();
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

            String authHeader = sharedPreferences.getString(MyConst.AUTH, null);
            viewModel.createGroup(currentUser, groupName, userEmails, authHeader, this);
        });

        viewModel.getToastMessage().observe(this, message -> Toast.makeText(NewGroupActivity.this, message, Toast.LENGTH_SHORT).show());
        viewModel.getCloseActivity().observe(this, shouldClose -> {
            if (shouldClose) finish();
        });
    }

    private void addNewUserInput() {
        EditText newUserInput = new EditText(this);
        newUserInput.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        newUserInput.setHint("Felhasználó e-mail");
        newUserInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        userLinearLayout.addView(newUserInput);
    }
}