package com.example.seriestracker;

import android.app.AlertDialog;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import com.example.seriestracker.adapters.MediaAdapter;
import com.example.seriestracker.database.MediaDatabase;
import com.example.seriestracker.modelo.MediaItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements AddMediaDialog.OnMediaAddedListener {
    private RecyclerView recyclerView;
    private MediaAdapter adapter;
    private MediaDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        database = MediaDatabase.getInstance(this);

        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v -> {
            AddMediaDialog dialog = new AddMediaDialog(this);
            dialog.show(getSupportFragmentManager(), "AddMediaDialog");
        });

        loadMediaItems();

        // Pedir permisos de notificación en Android 13+
        requestNotificationPermission();

        // Crear el canal de notificaciones
        NotificationHelper.createNotificationChannel(this);

        // Programar la notificación automática
        scheduleDailyNotification();

        // Verificar episodios pendientes al abrir la app
        checkEpisodesOnAppOpen();
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }

    private void loadMediaItems() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            List<MediaItem> mediaItems = database.mediaDao().getAllMediaItems();
            runOnUiThread(() -> {
                adapter = new MediaAdapter(mediaItems, item -> {
                    // Editar elemento
                    EditMediaDialog editDialog = new EditMediaDialog(item, updatedItem -> {
                        loadMediaItems();
                    });
                    editDialog.show(getSupportFragmentManager(), "EditMediaDialog");
                }, item -> {
                    // Eliminar elemento
                    showDeleteDialog(item);
                });
                recyclerView.setAdapter(adapter);
            });
        });
    }

    @Override
    public void onMediaAdded(MediaItem newItem) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            database.mediaDao().insert(newItem);
            runOnUiThread(this::loadMediaItems);
        });
    }

    private void scheduleDailyNotification() {
        Calendar calendar = Calendar.getInstance();
        long currentTime = System.currentTimeMillis();
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        long initialDelay = calendar.getTimeInMillis() - currentTime;
        if (initialDelay < 0) {
            initialDelay += TimeUnit.DAYS.toMillis(1); // Si ya pasó la hora, programa para el siguiente día
        }

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                EpisodeReminderWorker.class,
                1, TimeUnit.DAYS // Intervalo de repetición
        )
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .setConstraints(new Constraints.Builder().setRequiresBatteryNotLow(true).build())
                .build();

        WorkManager.getInstance(getApplicationContext()).enqueueUniquePeriodicWork(
                "daily_episode_reminder",
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
        );
    }

    private void checkEpisodesOnAppOpen() {
        new Thread(() -> {
            int pendingEpisodes = database.episodeDao().countUnwatchedEpisodes();
            if (pendingEpisodes > 0) {
                runOnUiThread(() -> NotificationHelper.showNotification(getApplicationContext()));
            }
        }).start();
    }

    private void showDeleteDialog(MediaItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar")
                .setMessage("¿Seguro que deseas eliminar " + item.getTitle() + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    ExecutorService executorService = Executors.newSingleThreadExecutor();
                    executorService.execute(() -> {
                        database.mediaDao().delete(item);
                        runOnUiThread(this::loadMediaItems);
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
