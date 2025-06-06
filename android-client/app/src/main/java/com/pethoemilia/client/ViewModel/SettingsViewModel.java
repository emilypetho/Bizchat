package com.pethoemilia.client.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pethoemilia.client.Repository.SettingsRepository;

public class SettingsViewModel extends AndroidViewModel {

    private final SettingsRepository repository;

    private final MutableLiveData<Boolean> notificationsEnabled = new MutableLiveData<>();
    private final MutableLiveData<Boolean> darkModeEnabled = new MutableLiveData<>();

    public SettingsViewModel(@NonNull Application application) {
        super(application);
        repository = new SettingsRepository(application);
        notificationsEnabled.setValue(repository.isNotificationsEnabled());
        darkModeEnabled.setValue(repository.isDarkModeEnabled());
    }

    public LiveData<Boolean> getNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(boolean enabled) {
        repository.setNotificationsEnabled(enabled);
        notificationsEnabled.setValue(enabled);
    }

    public LiveData<Boolean> getDarkModeEnabled() {
        return darkModeEnabled;
    }

    public void setDarkModeEnabled(boolean enabled) {
        repository.setDarkModeEnabled(enabled);
        darkModeEnabled.setValue(enabled);
    }
}
