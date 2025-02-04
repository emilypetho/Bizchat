package com.pethoemilia.client;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.pethoemilia.client.ViewModel.LoginViewModel;
import com.pethoemilia.client.entity.User;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private Button loginButton;
    private Button signupButton;
    private EditText emailEditText;
    private EditText passwordEditText;
    private TextView textViewResult;
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Bind views from the layout
        passwordEditText = findViewById(R.id.editTextPassword);
        emailEditText = findViewById(R.id.editTextEmail);
        loginButton = findViewById(R.id.login);
        signupButton = findViewById(R.id.singup);
        textViewResult = findViewById(R.id.textView);
        checkBox = findViewById(R.id.checkboxRememberMe);

        // Initialize ViewModel
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // Observe login result
        loginViewModel.getLoginResult().observe(this, result -> {
            textViewResult.setText(result);
            if (result.equals("Success")) {
                Intent intent = new Intent(LoginActivity.this, GroupActivity.class);
                startActivity(intent);
            }
        });

        // Betölti a mentett felhasználót, ha létezik
        User savedUser = loginViewModel.getSavedUser(this);
        if (savedUser != null) {
            emailEditText.setText(savedUser.getEmail());  // Például email betöltése
        }

        signupButton.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, UserRegistrationActivity.class);
            startActivity(intent);
        });

        loginButton.setOnClickListener(view -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            loginViewModel.loginUser(this, email, password);
        });
    }
}
