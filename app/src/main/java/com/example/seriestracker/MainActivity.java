package com.example.seriestracker;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.seriestracker.adapters.MediaAdapter;
import com.example.seriestracker.database.MediaDatabase;
import com.example.seriestracker.modelo.MediaItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements AddMediaDialog.OnMediaAddedListener {
    private RecyclerView recyclerView;
    private MediaAdapter adapter;
    private MediaDatabase database;
    private List<MediaItem> mediaItems;

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
    }

    private void loadMediaItems() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            mediaItems = database.mediaDao().getAllMediaItems();
            runOnUiThread(() -> {
                adapter = new MediaAdapter(mediaItems, item -> {
                    // Editar elemento
                    EditMediaDialog editDialog = new EditMediaDialog(item, updatedItem -> {
                        refreshMediaItems();
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

    private void refreshMediaItems() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            mediaItems = database.mediaDao().getAllMediaItems();
            runOnUiThread(() -> adapter.updateData(mediaItems));
        });
    }

    private void showDeleteDialog(MediaItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar")
                .setMessage("¿Seguro que deseas eliminar " + item.getTitle() + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    ExecutorService executorService = Executors.newSingleThreadExecutor();
                    executorService.execute(() -> {
                        database.mediaDao().delete(item);
                        refreshMediaItems();
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onMediaAdded(MediaItem newItem) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            database.mediaDao().insert(newItem);
            refreshMediaItems();
        });
    }
}
