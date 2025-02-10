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
import com.pethoemilia.client.ViewModel.NewChatViewModel;

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