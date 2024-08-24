package com.pethoemilia.client;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
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

public class MainActivity extends AppCompatActivity {

//    private ActivityMainBinding binding;
    private ActivityMainBinding binding;
    private UserClient userClient;
    private CompanyClient companyClient;
    private TextView textViewResult;
    private Button button;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText nameEditText;
    private EditText addressEditText;
    private EditText telEditText;
    private Button addNewUserButton;
    private MaterialAutoCompleteTextView autoCompleteCompany;
    private MaterialAutoCompleteTextView autoCompleteRole;
    private TextInputLayout companyTextInputLayout;
    private TextInputLayout roleTextInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        // Initialize the binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.111:8080/") // Base URL for the backend
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Initialize API clients
        userClient = retrofit.create(UserClient.class);
        companyClient = retrofit.create(CompanyClient.class);

        // Bind views from the layout
        textViewResult = findViewById(R.id.textView);
        button = findViewById(R.id.button);
        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        nameEditText = findViewById(R.id.editTextName);
        addressEditText = findViewById(R.id.editTextPostalAddress);
        telEditText = findViewById(R.id.editTextphonenumber);
        addNewUserButton = findViewById(R.id.buttonAddNewUser);

        // Bind the auto-complete text views and their parent layouts
        autoCompleteCompany = findViewById(R.id.inputcompany);
        autoCompleteRole = findViewById(R.id.inputrole);
        companyTextInputLayout = findViewById(R.id.editTextCompany);
        roleTextInputLayout = findViewById(R.id.editRole);

        // Load company and role data for the dropdowns
        loadCompanies();
        loadRoles();

        button.setOnClickListener(new View.OnClickListener() { // beteszek egy click listenert a buttonhoz, amikor megnyomodik a gomb vegrehajtja ami a mtodusban van
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MainActivity3.class);
                startActivity(intent);
            }
        });

        // Set click listener for the Add New User button
        addNewUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selectedCompanyName = autoCompleteCompany.getText().toString();
                String selectedRole = autoCompleteRole.getText().toString();

                    Call<List<Company>> call = companyClient.findAll();
                    call.enqueue(new Callback<List<Company>>() {
                        @Override
                        public void onResponse(Call<List<Company>> call, Response<List<Company>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                User.Role role;
                                try {
                                    role = User.Role.valueOf(selectedRole.toUpperCase()); // Az enum érték keresése
                                } catch (IllegalArgumentException e) {
                                    textViewResult.setText("Invalid role selected");
                                    return; // Ha nem található a szerepkör, nem folytatjuk
                                }
                                List<Company> companies = response.body();
                                for (Company c : companies){
                                    if(c.getName().equals(selectedCompanyName)){
                                        // Hívjuk meg az új felhasználó létrehozásához szükséges végpontot
                                        Call<User> userCall = userClient.save(new User(
                                                emailEditText.getText().toString(),
                                                passwordEditText.getText().toString(),
                                                nameEditText.getText().toString(),
                                                addressEditText.getText().toString(),
                                                telEditText.getText().toString(),
                                                c,
                                                role
                                        ));

                                        userCall.enqueue(new Callback<User>() {
                                            @Override
                                            public void onResponse(Call<User> call, Response<User> response) {
                                                if (response.isSuccessful()) {
                                                    textViewResult.setText("User added successfully");
//                                                    button.performClick(); // Refresh user list
                                                } else {
                                                    textViewResult.setText("Failed to add user");
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<User> call, Throwable t) {
                                                // Handle failure
                                                textViewResult.setText("Error saving user");
                                            }
                                        });
                                    }
                                }
                                Intent intent = new Intent(MainActivity.this, MainActivity4.class);
                                startActivity(intent);
                            } else {
                                companyTextInputLayout.setError("Company not found");
                                textViewResult.setText("company,find by name");
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Company>> call, Throwable t) {
                            textViewResult.setText("find by name");
                        }
                    });
            }
        });

        // Set focus change listeners to clear errors on focus
        autoCompleteCompany.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    companyTextInputLayout.setHint(null);
                    companyTextInputLayout.setError(null);
                } else {
//                    companyTextInputLayout.setHint("company");
                    companyTextInputLayout.setError(null);
                }
            }
        });

        autoCompleteRole.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    roleTextInputLayout.setHint(null);
                    roleTextInputLayout.setError(null);
                } else {
//                    roleTextInputLayout.setHint("role");
                    roleTextInputLayout.setError(null);
                }
            }
        });
    }

    private void loadCompanies() {
        Call<List<Company>> call = companyClient.findAll();
        call.enqueue(new Callback<List<Company>>() {
            @Override
            public void onResponse(Call<List<Company>> call, Response<List<Company>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> companyNames = new ArrayList<>();
                    for (Company company : response.body()) {
                        companyNames.add(company.getName());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, companyNames);
                    autoCompleteCompany.setAdapter(adapter);
                } else {
                    textViewResult.setText("Sikertelen válasz a szervertől.");
                }
            }

            @Override
            public void onFailure(Call<List<Company>> call, Throwable t) {
                // További hibakezelés
                textViewResult.setText("Hiba történt: " + t.getMessage());
            }
        });
    }

    private void loadRoles() {
        List<String> roleNames = new ArrayList<>();
        for (User.Role role : User.Role.values()) {
            roleNames.add(role.name());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, roleNames);
        autoCompleteRole.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
