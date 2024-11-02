package com.hassan.statussaver.application;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.File;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Check if it's the first time the app is launched
        SharedPreferences sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE);
        boolean isFirstLaunch = sharedPreferences.getBoolean("isFirstLaunch", true);

        if (isFirstLaunch) {
            Log.d("MyApp", "First launch");
            // Perform your one-time actions (like clearing settings)
            clearAppSettings();

            // Mark that the first launch actions are done
            sharedPreferences.edit().putBoolean("isFirstLaunch", false).apply();
        }
    }

    private void clearAppSettings() {
        // Clear shared preferences or other data
        File sharedPreferencesDir = new File(getApplicationContext().getFilesDir().getParentFile(), "shared_prefs");
        if (sharedPreferencesDir.exists() && sharedPreferencesDir.isDirectory()) {
            File[] files = sharedPreferencesDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
        }
    }
}