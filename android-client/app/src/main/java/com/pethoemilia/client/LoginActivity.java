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

import org.mindrot.jbcrypt.BCrypt;

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
//            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
//            Log.d("HashedPassword", hashedPassword);
//
//            String encoded = Base64.encodeToString((email + ":" + hashedPassword).getBytes(), Base64.NO_WRAP);
//            String encodedcredentials = "Basic " + encoded;

            String encoded = Base64.encodeToString((email + ":" + password).getBytes(), Base64.NO_WRAP);
            String encodedcredentials = "Basic " + encoded;

            Log.d("EncodedCredentials", encodedcredentials);
            Log.d("Email", email);
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
                    if(response.isSuccessful()){
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
                    }else{
                        textViewResult.setText("helytelen emailcim vagy jelszo");
                    }
                }
                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.e("LoginActivity","Error with login authentication");
                }
            });
        });
    }
}