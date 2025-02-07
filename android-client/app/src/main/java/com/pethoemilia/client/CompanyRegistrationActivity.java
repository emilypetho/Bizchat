//package com.pethoemilia.client;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.pethoemilia.client.api.CompanyClient;
//import com.pethoemilia.client.databinding.ActivityCompanyRegistrationBinding;
//import com.pethoemilia.client.entity.Company;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//
//public class CompanyRegistrationActivity extends AppCompatActivity {
//
//    private ActivityCompanyRegistrationBinding binding;
//    private CompanyClient companyClient;
//    private TextView textViewResult;
//    private Button button;
//    private EditText nameEditText;
//    private EditText addressEditText;
//    private Button addNewUserButton;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        // Initialize the binding
//        binding = ActivityCompanyRegistrationBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//        setContentView(R.layout.activity_company_registration);
//
//        // Set up Retrofit
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(MyConst.URL) // Base URL for the backend
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        companyClient = retrofit.create(CompanyClient.class);
//
//        // Bind views from the layout
//        textViewResult = findViewById(R.id.textView);
//        button = findViewById(R.id.button);
//        nameEditText = findViewById(R.id.editTextName);
//        addressEditText = findViewById(R.id.editTextPostalAddress);
//        addNewUserButton = findViewById(R.id.buttonAddNewUser);
//
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(CompanyRegistrationActivity.this, UserRegistrationActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        // Set click listener for the Add New User button
//        addNewUserButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String name = nameEditText.getText().toString().trim();
//                String address = addressEditText.getText().toString().trim();
//                // Validate name and address input
//                if (name.isEmpty()) {
//                    nameEditText.setError("Name cannot be empty");
//                    return;
//                } else {
//                    nameEditText.setError(null); // Clear error
//                }
//
//                if (address.isEmpty()) {
//                    addressEditText.setError("Address cannot be empty");
//                    return;
//                } else {
//                    addressEditText.setError(null); // Clear error
//                }
//
//                Call<Company> companyCall = companyClient.save(new Company(name, address));
//
//                companyCall.enqueue(new Callback<Company>() {
//                    @Override
//                    public void onResponse(Call<Company> call, Response<Company> response) {
//                        if (response.isSuccessful()) {
//                            textViewResult.setText("Company added successfully");
//                        } else {
//                            textViewResult.setText("Failed to add company");
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<Company> call, Throwable t) {
//                        textViewResult.setText("Error saving company");
//                    }
//                });
//            }
//        });
//    }
//}




package com.pethoemilia.client;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.pethoemilia.client.databinding.ActivityCompanyRegistrationBinding;
import com.pethoemilia.client.ViewModel.CompanyViewModel;

public class CompanyRegistrationActivity extends AppCompatActivity {

    private ActivityCompanyRegistrationBinding binding;
    private CompanyViewModel companyViewModel;
    private TextView textViewResult;
    private EditText nameEditText;
    private EditText addressEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCompanyRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ViewModel példányosítása
        companyViewModel = new CompanyViewModel();

        textViewResult = findViewById(R.id.textView);
        nameEditText = findViewById(R.id.editTextName);
        addressEditText = findViewById(R.id.editTextPostalAddress);
        Button button = findViewById(R.id.button);
        Button addNewCompanyButton = findViewById(R.id.buttonAddNewCompany);

        // Navigáció másik Activity-be
        button.setOnClickListener(view -> {
            Intent intent = new Intent(CompanyRegistrationActivity.this, UserRegistrationActivity.class);
            startActivity(intent);
        });

        // Új cég hozzáadása
        addNewCompanyButton.setOnClickListener(view -> {
            String name = nameEditText.getText().toString().trim();
            String address = addressEditText.getText().toString().trim();

            // Validáció
            if (name.isEmpty()) {
                nameEditText.setError("Name cannot be empty");
                return;
            } else {
                nameEditText.setError(null);
            }

            if (address.isEmpty()) {
                addressEditText.setError("Address cannot be empty");
                return;
            } else {
                addressEditText.setError(null);
            }

            // ViewModel meghívása
            companyViewModel.saveCompany(name, address).observe(this, result ->
                    textViewResult.setText(result)
            );
        });
    }
}
