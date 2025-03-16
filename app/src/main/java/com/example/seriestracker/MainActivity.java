package com.example.seriestracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.util.Log;
import androidx.fragment.app.Fragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import android.content.SharedPreferences;


import com.example.seriestracker.adapters.MainPagerAdapter;
import com.example.seriestracker.database.MediaDatabase;
import com.example.seriestracker.ui.MediaListFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.example.seriestracker.AddMediaDialog;
import com.example.seriestracker.modelo.MediaItem;

import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity implements AddMediaDialog.OnMediaAddedListener {
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private FloatingActionButton fabAdd, fabSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        // Configurar la Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Inicializar FABs
        fabAdd = findViewById(R.id.fabAdd);
        fabSettings = findViewById(R.id.fabSettings);

        // Configurar pestañas con ViewPager2 y TabLayout
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager.setAdapter(new MainPagerAdapter(this));

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText(getString(R.string.tab_series));
                fabAdd.show();
            } else {
                tab.setText(getString(R.string.tab_movies));
                fabAdd.show();
            }
        }).attach();


        // Acción del FAB Añadir (abrir diálogo de agregar media)
        fabAdd.setOnClickListener(v -> {
            AddMediaDialog dialog = new AddMediaDialog(this);
            dialog.show(getSupportFragmentManager(), "AddMediaDialog");
        });

        // Acción del FAB Ajustes (abrir actividad de ajustes)
        fabSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        NotificationHelper.createNotificationChannel(this);
        scheduleDailyNotification();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onMediaAdded(MediaItem newItem) {
        new Thread(() -> {
            MediaDatabase.getInstance(this).mediaDao().insert(newItem);
            runOnUiThread(() -> {
                // Obtener el fragmento actual y recargar datos
                int currentItem = viewPager.getCurrentItem();
                MediaListFragment fragment = (MediaListFragment) getSupportFragmentManager()
                        .findFragmentByTag("f" + currentItem);

                if (fragment != null) {
                    fragment.reloadData();
                }
            });
        }).start();
    }

    private void scheduleDailyNotification() {
        SharedPreferences preferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        boolean notificationsEnabled = preferences.getBoolean("notifications", true); // Leer si están activadas

        if (!notificationsEnabled) {
            Log.d("MainActivity", "Notificaciones deshabilitadas en ajustes, cancelando WorkManager.");
            WorkManager.getInstance(this).cancelAllWorkByTag("daily_episode_reminder");
            return;
        }

        Log.d("MainActivity", "Notificaciones habilitadas, programando WorkManager.");

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                EpisodeReminderWorker.class, 24, TimeUnit.HOURS)
                .setConstraints(new Constraints.Builder().setRequiresBatteryNotLow(true).build())
                .addTag("daily_episode_reminder") // Asegurar un identificador único
                .build();

        WorkManager.getInstance(getApplicationContext())
                .enqueueUniquePeriodicWork("daily_episode_reminder",
                        ExistingPeriodicWorkPolicy.REPLACE, workRequest);
    }



}

