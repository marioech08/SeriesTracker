package com.example.seriestracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.seriestracker.database.MediaDatabase;
import com.example.seriestracker.modelo.Episode;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddEpisodeDialog extends DialogFragment {
    private final int seriesId;

    public AddEpisodeDialog(int seriesId) {
        this.seriesId = seriesId;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Añadir Episodio");

        EditText input = new EditText(requireActivity());
        input.setHint("Título del episodio");
        builder.setView(input);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String title = input.getText().toString().trim();
            if (!title.isEmpty()) {
                Episode newEpisode = new Episode(seriesId, title, false);

                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {
                    MediaDatabase.getInstance(requireActivity()).episodeDao().insert(newEpisode);

                    // 🔹 ACTUALIZAR INSTANTÁNEAMENTE LA LISTA
                    requireActivity().runOnUiThread(() -> {
                        if (getParentFragment() instanceof EpisodeDialog) {
                            ((EpisodeDialog) getParentFragment()).addEpisodeToList(newEpisode);
                        }
                    });
                });
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        return builder.create();
    }
}
