package com.pethoemilia.client;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.pethoemilia.client.api.UserClient;
import com.pethoemilia.client.entity.User;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private UserClient userClient;
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

        // Load saved user if available
        //loadUserFromSharedPreferences();

        // Set up Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MyConst.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        userClient = retrofit.create(UserClient.class);

        signupButton.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, UserRegistrationActivity.class);
            startActivity(intent);
        });

        loginButton.setOnClickListener(view -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String encoded = Base64.encodeToString((email + ":" + password).getBytes(), Base64.NO_WRAP);
            String encodedcredentials = "Basic " + encoded;
            SharedPreferences sharedPref = getSharedPreferences(MyConst.SHARED_PREF_KEY, Context.MODE_PRIVATE);

            if (email.isEmpty()) {
                textViewResult.setText("Email cannot be empty");
                return;
            }

            if (password.isEmpty()) {
                textViewResult.setText("Password cannot be empty");
                return;
            }
            Log.d("EncodedCredentials", encodedcredentials);
            Log.d("Email", email);Log.d("Password", password);
            Call<User> idCall = userClient.findByEmail(email,encodedcredentials);
            idCall.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    Log.e("siker",String.valueOf(response.code()));
                    if(response.isSuccessful()){
                        Log.e("jo","jhgf");
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(MyConst.AUTH,encodedcredentials);
                        editor.putBoolean(MyConst.REMEMBER_ME,checkBox.isChecked());
                        User user = response.body();
                        Gson gson = new Gson();
                        String userString = gson.toJson(user);
                        editor.putString(MyConst.USER,userString);
                        editor.apply();
                        textViewResult.setText("");
                        Intent intent = new Intent(LoginActivity.this, GroupActivity.class);
                        startActivity(intent);
                    }
                }
                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.e("szar","jhgf");
                }
            });
        });
    }

//    // Felhasználói adatok betöltése a SharedPreferences-ből
//    private void loadUserFromSharedPreferences() {
//        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_USER, Context.MODE_PRIVATE);
//        String userJson = sharedPreferences.getString(KEY_USER, null);
//
//        if (userJson != null) {
//            Gson gson = new Gson();
//            User user = gson.fromJson(userJson, User.class); // JSON konvertálása User objektummá
//
//            // Beállítjuk az emailt és a jelszót az EditText mezőkben
//
//            // Visszaállítjuk a CheckBox állapotát
//            boolean rememberMeChecked = sharedPreferences.getBoolean("remember_me", false);
//            checkBox.setChecked(rememberMeChecked);
//
//            // Automatikus bejelentkezés, ha be van jelölve a CheckBox
//            if (rememberMeChecked) {
//                emailEditText.setText(user.getEmail());
//                passwordEditText.setText(user.getPassword());
//                loginButton.performClick(); // Automatikus bejelentkezés
//            }
//        }
//    }
//
//    // Felhasználói adatok mentése a SharedPreferences-be
//    private void saveUserToSharedPreferences(User user) {
//        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_USER, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//
//        Gson gson = new Gson();
//        String userJson = gson.toJson(user); // User objektum konvertálása JSON formátumba
//
//        editor.putString(KEY_USER, userJson);
//
//        // Mentjük a CheckBox állapotát
//        editor.putBoolean("remember_me", checkBox.isChecked());
//
//        editor.apply(); // Adatok elmentése
//    }
}