package com.example.seriestracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import java.util.Locale;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

public class SettingsActivity extends AppCompatActivity {
    private SwitchCompat switchDarkMode;
    private SwitchCompat switchNotifications;
    private SwitchCompat switchLanguage;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);

        // Vincular switches con el XML
        switchDarkMode = findViewById(R.id.switchDarkMode);
        switchNotifications = findViewById(R.id.switchNotifications);
        switchLanguage = findViewById(R.id.switchLanguage);

        // Cargar configuraciones guardadas
        loadSettings();

        // Listener para cambiar el modo oscuro
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("dark_mode", isChecked);
            editor.apply();
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
        });

        // Listener para activar/desactivar notificaciones
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("notifications", isChecked);
            editor.apply();
        });

        // Listener para cambiar idioma
        switchLanguage.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String newLanguage = isChecked ? "en" : "es"; // Inglés o español
            setLocale(newLanguage);

            // Guardar idioma en SharedPreferences
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("language", newLanguage);
            editor.apply();

            // Reiniciar la actividad principal para aplicar cambios
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    // Cargar configuraciones guardadas al abrir ajustes
    private void loadSettings() {
        SharedPreferences preferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);

        // ✅ Cargar modo oscuro correctamente
        boolean isDarkMode = preferences.getBoolean("dark_mode", false);
        switchDarkMode.setChecked(isDarkMode);

        // ✅ Cargar notificaciones correctamente
        boolean notificationsEnabled = preferences.getBoolean("notifications", true);
        switchNotifications.setChecked(notificationsEnabled);

        // ✅ Manejar posible error con `language` si se guardó mal en el pasado
        Object languageObj = preferences.getAll().get("language");
        String language = "es"; // Español por defecto

        if (languageObj instanceof String) {
            language = (String) languageObj;
        }

        switchLanguage.setChecked(language.equals("en"));
    }


    // Cambiar el idioma de la aplicación
    private void setLocale(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }
}
