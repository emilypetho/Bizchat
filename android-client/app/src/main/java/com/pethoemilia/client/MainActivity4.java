package com.pethoemilia.client;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.pethoemilia.client.api.UserClient;
import com.pethoemilia.client.entity.User;
import com.pethoemilia.client.entity.UserSession;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity4 extends AppCompatActivity {

    private UserClient userClient;
    private Button loginButton;
    private Button signupButton;
    private EditText emailEditText;
    private EditText passwordEditText;
    private TextView textViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        // Set up Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.111:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        userClient = retrofit.create(UserClient.class);

        // Bind views from the layout
        passwordEditText = findViewById(R.id.editTextPassword);
        emailEditText = findViewById(R.id.editTextEmail);
        loginButton = findViewById(R.id.login);
        signupButton = findViewById(R.id.singup);
        textViewResult = findViewById(R.id.textView);

        signupButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity4.this, MainActivity.class);
            startActivity(intent);
        });

        loginButton.setOnClickListener(view -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (email.isEmpty()) {
                textViewResult.setText("Email cannot be empty");
                return;
            }

            if (password.isEmpty()) {
                textViewResult.setText("Password cannot be empty");
                return;
            }

            Call<List<User>> userCall = userClient.findAll();
            userCall.enqueue(new Callback<List<User>>() {
                @Override
                public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<User> users = response.body();
                        for (User user : users) {
                            if (Objects.equals(user.getEmail(), email)) {
                                if (Objects.equals(user.getPassword(), password)) {
                                    UserSession.setUser(user); // Azonosító mentése
                                    Intent intent = new Intent(MainActivity4.this, MainActivity2.class);
                                    startActivity(intent);
                                    return; // Kilép, ha a felhasználó megtalálása sikeres
                                } else {
                                    textViewResult.setText("Invalid password");
                                    return;
                                }
                            }
                        }
                        textViewResult.setText("Invalid email");
                    } else {
                        textViewResult.setText("Invalid email");
                    }
                }

                @Override
                public void onFailure(Call<List<User>> call, Throwable t) {
                    textViewResult.setText("Error: " + t.getMessage());
                }
            });
        });
    }
}
