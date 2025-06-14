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

        companyViewModel = new CompanyViewModel();

        textViewResult = findViewById(R.id.textView);
        nameEditText = findViewById(R.id.editTextName);
        addressEditText = findViewById(R.id.editTextPostalAddress);
        Button button = findViewById(R.id.button);
        Button addNewCompanyButton = findViewById(R.id.buttonAddNewCompany);

        button.setOnClickListener(view -> {
            Intent intent = new Intent(CompanyRegistrationActivity.this, UserRegistrationActivity.class);
            startActivity(intent);
        });

        addNewCompanyButton.setOnClickListener(view -> {
            String name = nameEditText.getText().toString().trim();
            String address = addressEditText.getText().toString().trim();

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

            companyViewModel.saveCompany(name, address).observe(this, result ->
                    textViewResult.setText(result)
            );
        });
    }
}
