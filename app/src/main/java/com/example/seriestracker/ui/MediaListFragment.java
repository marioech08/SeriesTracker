package com.example.seriestracker.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.seriestracker.R;
import com.example.seriestracker.adapters.MediaAdapter;
import com.example.seriestracker.database.MediaDatabase;
import com.example.seriestracker.modelo.MediaItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.example.seriestracker.AddMediaDialog;
import com.example.seriestracker.ui.EditMediaDialog;
import android.util.Log;

public class MediaListFragment extends Fragment {
    private RecyclerView recyclerView;
    private MediaAdapter adapter;
    private MediaDatabase database;
    private FloatingActionButton fabAdd; // Agregar referencia al FAB

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_media_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewMedia);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        database = MediaDatabase.getInstance(getContext());

        // 💡 Volver a inicializar el FAB dentro del Fragment
        FloatingActionButton fabAdd = view.findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v -> {
            AddMediaDialog dialog = new AddMediaDialog(mediaItem -> {
                insertMediaItem(mediaItem); // ✅ Guardar y actualizar UI
            });
            dialog.show(getParentFragmentManager(), "AddMediaDialog");
        });

        loadMediaItems();
        return view;
    }

    // ✅ Método para guardar en BD y actualizar UI
    private void insertMediaItem(MediaItem mediaItem) {
        new Thread(() -> {
            database.mediaDao().insert(mediaItem);
            getActivity().runOnUiThread(this::loadMediaItems);
        }).start();
    }


    private void loadMediaItems() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<MediaItem> mediaItems = database.mediaDao().getAllMediaItems();
            getActivity().runOnUiThread(() -> {
                adapter = new MediaAdapter(mediaItems, item -> {
                    EditMediaDialog editDialog = new EditMediaDialog(item, updatedItem -> reloadData());
                    editDialog.show(getParentFragmentManager(), "EditMediaDialog");
                }, this::showDeleteDialog);
                recyclerView.setAdapter(adapter);
            });
        });
    }

    private void showDeleteDialog(MediaItem item) {
        new AlertDialog.Builder(getContext())
                .setTitle("Eliminar")
                .setMessage("¿Seguro que deseas eliminar " + item.getTitle() + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> deleteMediaItem(item))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    public void deleteMediaItem(MediaItem item) {
        new Thread(() -> {
            MediaDatabase.getInstance(getContext()).mediaDao().delete(item);
            getActivity().runOnUiThread(() -> reloadData());
        }).start();
    }

    public void reloadData() {
        Log.d("MediaListFragment", "reloadData() se está ejecutando");
        new Thread(() -> {
            List<MediaItem> updatedList = MediaDatabase.getInstance(getContext()).mediaDao().getAllMediaItems();
            getActivity().runOnUiThread(() -> {
                Log.d("MediaListFragment", "Lista actualizada: " + updatedList.size());
                if (adapter == null) {
                    adapter = new MediaAdapter(updatedList, item -> {
                        EditMediaDialog editDialog = new EditMediaDialog(item, updatedItem -> reloadData());
                        editDialog.show(getParentFragmentManager(), "EditMediaDialog");
                    }, this::deleteMediaItem);
                    recyclerView.setAdapter(adapter);
                } else {
                    adapter.updateData(updatedList);
                }
            });
        }).start();
    }




}
