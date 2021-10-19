package com.mohitsharmaji.newsproject.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import com.mohitsharmaji.newsproject.R;

import static com.mohitsharmaji.newsproject.MainActivity.NAME_SHARED_PREFERENCE;
import static com.mohitsharmaji.newsproject.MainActivity.isDarkTheme_prefs;

public class SettingsActivity extends AppCompatActivity {
    Toolbar toolbar;
    SwitchCompat darkModeToggle;
    ImageView settings_img;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor_sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        settings_img = findViewById(R.id.settings_img);
        toolbar = findViewById(R.id.settingsToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = getSharedPreferences(NAME_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        editor_sharedPref = sharedPreferences.edit();
        darkModeToggle = findViewById(R.id.toggleDarkMode);
        if(sharedPreferences.getBoolean(isDarkTheme_prefs, false)){
            darkModeToggle.setChecked(true);
            settings_img.setVisibility(View.GONE);
        }else{
            darkModeToggle.setChecked(false);
            settings_img.setVisibility(View.VISIBLE);
        }
        darkModeToggle.setOnClickListener(view -> {
            if(!darkModeToggle.isChecked()){
                // Set  Theme
                settings_img.setVisibility(View.GONE);
                editor_sharedPref.putBoolean(isDarkTheme_prefs, false);
                editor_sharedPref.commit();
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                Toast.makeText(SettingsActivity.this, "Light Theme: Enabled", Toast.LENGTH_SHORT).show();
            }else{
                // Set DarkLight Theme
                settings_img.setVisibility(View.VISIBLE);
                editor_sharedPref.putBoolean(isDarkTheme_prefs, true);
                editor_sharedPref.commit();
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                Toast.makeText(SettingsActivity.this, "Dark Theme: Enabled", Toast.LENGTH_SHORT).show();
            }
        });
    }
}