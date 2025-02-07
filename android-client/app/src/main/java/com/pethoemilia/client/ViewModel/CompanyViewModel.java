package com.pethoemilia.client.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.pethoemilia.client.Repository.CompanyRepository;

public class CompanyViewModel extends ViewModel {
    private final CompanyRepository repository;

    public CompanyViewModel() {
        repository = new CompanyRepository();
    }

    public LiveData<String> saveCompany(String name, String address) {
        return repository.saveCompany(name, address);
    }
}
