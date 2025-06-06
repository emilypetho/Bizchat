package com.pethoemilia.client.Repository;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.appcompat.app.AppCompatDelegate;

import com.pethoemilia.client.MyConst;

public class SettingsRepository {

    private static final String PREF_NAME = "bizchat_settings";
    private static final String KEY_NOTIFICATIONS = "notifications_enabled";
    private static final String KEY_DARK_MODE = "dark_mode_enabled";

    private final SharedPreferences sharedPreferences;
    private final Context context;

    public SettingsRepository(Context context) {
        this.context = context.getApplicationContext();
        sharedPreferences = this.context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean isNotificationsEnabled() {
        return sharedPreferences.getBoolean(KEY_NOTIFICATIONS, true);
    }

    public void setNotificationsEnabled(boolean enabled) {
        sharedPreferences.edit().putBoolean(KEY_NOTIFICATIONS, enabled).apply();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                // Töröljük az előzőt és újra létrehozzuk új beállítással
                manager.deleteNotificationChannel(MyConst.CHANNEL_ID);

                NotificationChannel channel = new NotificationChannel(
                        MyConst.CHANNEL_ID,
                        "BizChat",
                        enabled ? NotificationManager.IMPORTANCE_DEFAULT : NotificationManager.IMPORTANCE_NONE
                );
                channel.setDescription("Chat értesítések");
                manager.createNotificationChannel(channel);
            }
        }
    }

    public boolean isDarkModeEnabled() {
        return sharedPreferences.getBoolean(KEY_DARK_MODE, false);
    }

    public void setDarkModeEnabled(boolean enabled) {
        sharedPreferences.edit().putBoolean(KEY_DARK_MODE, enabled).apply();

        AppCompatDelegate.setDefaultNightMode(
                enabled ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }
}
