package com.pethoemilia.client.Repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pethoemilia.client.api.CompanyClient;
import com.pethoemilia.client.entity.Company;
import com.pethoemilia.client.MyConst;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CompanyRepository {
    private final CompanyClient companyClient;

    public CompanyRepository() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MyConst.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        companyClient = retrofit.create(CompanyClient.class);
    }

    public LiveData<String> saveCompany(String name, String address) {
        MutableLiveData<String> resultLiveData = new MutableLiveData<>();

        Call<Company> companyCall = companyClient.save(new Company(name, address));
        companyCall.enqueue(new Callback<Company>() {
            @Override
            public void onResponse(Call<Company> call, Response<Company> response) {
                if (response.isSuccessful()) {
                    resultLiveData.setValue("Company added successfully");
                } else {
                    resultLiveData.setValue("Failed to add company");
                }
            }

            @Override
            public void onFailure(Call<Company> call, Throwable t) {
                resultLiveData.setValue("Error saving company");
            }
        });

        return resultLiveData;
    }
}
