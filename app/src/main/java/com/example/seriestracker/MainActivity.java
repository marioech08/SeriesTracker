package com.example.seriestracker;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.seriestracker.R;
import com.example.seriestracker.adapters.MediaAdapter;
import com.example.seriestracker.database.MediaDatabase;
import com.example.seriestracker.modelo.MediaItem;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity {
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

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            List<MediaItem> items = database.mediaDao().getAllMediaItems();

            // Si la base de datos está vacía, insertamos datos de prueba
            if (items.isEmpty()) {
                database.mediaDao().insert(new MediaItem("Breaking Bad", "Serie", true));
                database.mediaDao().insert(new MediaItem("Interstellar", "Película", false));
                database.mediaDao().insert(new MediaItem("Dark", "Serie", true));

                // Volvemos a cargar los datos después de la inserción
                items = database.mediaDao().getAllMediaItems();
            }

            List<MediaItem> finalItems = items;
            runOnUiThread(() -> {
                adapter = new MediaAdapter(finalItems);
                recyclerView.setAdapter(adapter);
            });
        });
    }


}
