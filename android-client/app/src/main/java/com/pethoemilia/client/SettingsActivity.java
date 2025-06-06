package com.pethoemilia.client;

import android.os.Bundle;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;

import com.pethoemilia.client.ViewModel.SettingsViewModel;

public class SettingsActivity extends AppCompatActivity {

    private SettingsViewModel settingsViewModel;
    private boolean initialTheme;
    private boolean isSwitchBeingUpdatedProgrammatically = false; // fontos flag

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applySavedTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Switch switchNotifications = findViewById(R.id.switch_notifications);
        Switch switchDarkMode = findViewById(R.id.switch_dark_mode);

        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        // Megfigyelők
        settingsViewModel.getNotificationsEnabled().observe(this, enabled -> {
            isSwitchBeingUpdatedProgrammatically = true;
            switchNotifications.setChecked(enabled);
            isSwitchBeingUpdatedProgrammatically = false;
        });

        settingsViewModel.getDarkModeEnabled().observe(this, isDark -> {
            isSwitchBeingUpdatedProgrammatically = true;
            switchDarkMode.setChecked(isDark);
            initialTheme = isDark;
            isSwitchBeingUpdatedProgrammatically = false;
        });

        // Listener értesítésekhez
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isSwitchBeingUpdatedProgrammatically) return; // Ne reagáljon a programmatic változásra
            settingsViewModel.setNotificationsEnabled(isChecked);
        });

        // Listener témához
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isSwitchBeingUpdatedProgrammatically) return; // Ne reagáljon a programmatic változásra
            settingsViewModel.setDarkModeEnabled(isChecked);

            if (isChecked != initialTheme) {
                recreate();
            }
        });
    }

    private void applySavedTheme() {
        boolean isDarkMode = getSharedPreferences("bizchat_settings", MODE_PRIVATE)
                .getBoolean("dark_mode_enabled", false);

        AppCompatDelegate.setDefaultNightMode(
                isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }
}
