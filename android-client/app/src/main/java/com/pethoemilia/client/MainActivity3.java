package com.pethoemilia.client;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.pethoemilia.client.api.CompanyClient;
import com.pethoemilia.client.api.UserClient;
import com.pethoemilia.client.databinding.ActivityMainBinding;
import com.pethoemilia.client.entity.Company;
import com.pethoemilia.client.entity.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity3 extends AppCompatActivity {

    private ActivityMainBinding binding;
    private CompanyClient companyClient;
    private TextView textViewResult;
    private Button button;
    private EditText nameEditText;
    private EditText addressEditText;
    private Button addNewUserButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setContentView(R.layout.activity_main3);

        // Set up Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.111:8080/") // Base URL for the backend
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        companyClient = retrofit.create(CompanyClient.class);

        // Bind views from the layout
        textViewResult = findViewById(R.id.textView);
        button = findViewById(R.id.button);
        nameEditText = findViewById(R.id.editTextName);
        addressEditText = findViewById(R.id.editTextPostalAddress);
        addNewUserButton = findViewById(R.id.buttonAddNewUser);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity3.this, MainActivity.class);
                startActivity(intent);
//                Call<List<Company>> companyCall = companyClient.findAll();
//                companyCall.enqueue(new Callback<List<Company>>() {
//                    @Override
//                    public void onResponse(Call<List<Company>> call, Response<List<Company>> response) {
//                        if (response.isSuccessful()) {
//                            Gson gson = new Gson();
//                            String json = gson.toJson(response.body());
//                            textViewResult.setText(json);
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<List<Company>> call, Throwable t) {
//                        textViewResult.setText("Problem appeared while fetching data");
//                    }
//                });
            }
        });

        // Set click listener for the Add New User button
        addNewUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameEditText.getText().toString().trim();
                String address = addressEditText.getText().toString().trim();
                // Validate name and address input
                if (name.isEmpty()) {
                    nameEditText.setError("Name cannot be empty");
                    return;
                } else {
                    nameEditText.setError(null); // Clear error
                }

                if (address.isEmpty()) {
                    addressEditText.setError("Address cannot be empty");
                    return;
                } else {
                    addressEditText.setError(null); // Clear error
                }

                Call<Company> companyCall = companyClient.save(new Company(name, address));

                companyCall.enqueue(new Callback<Company>() {
                    @Override
                    public void onResponse(Call<Company> call, Response<Company> response) {
                        if (response.isSuccessful()) {
                            textViewResult.setText("Company added successfully");
                        } else {
                            textViewResult.setText("Failed to add company");
                        }
                    }

                    @Override
                    public void onFailure(Call<Company> call, Throwable t) {
                        textViewResult.setText("Error saving company");
                    }
                });
            }
        });
    }
}
