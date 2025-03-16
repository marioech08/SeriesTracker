package com.example.seriestracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
    private OnEpisodeAddedListener listener;

    public interface OnEpisodeAddedListener {
        void onEpisodeAdded(Episode newEpisode);
    }

    public AddEpisodeDialog(int seriesId, OnEpisodeAddedListener listener) {
        this.seriesId = seriesId;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_episode, null);
        builder.setView(view);

        EditText inputEpisodeTitle = view.findViewById(R.id.inputEpisodeTitle);
        Button buttonSave = view.findViewById(R.id.buttonSaveEpisode);
        Button buttonCancel = view.findViewById(R.id.buttonCancelEpisode);

        builder.setTitle(getString(R.string.add_episode_title));

        buttonSave.setOnClickListener(v -> {
            String episodeTitle = inputEpisodeTitle.getText().toString();

            if (!episodeTitle.isEmpty()) {
                Episode newEpisode = new Episode(seriesId, episodeTitle, false); // ✅ Agregado boolean "watched"

                // ✅ Guardar en la base de datos
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {
                    MediaDatabase.getInstance(getActivity()).episodeDao().insert(newEpisode);
                    getActivity().runOnUiThread(() -> {
                        listener.onEpisodeAdded(newEpisode);
                        dismiss();
                    });
                });
            }
        });

        buttonCancel.setOnClickListener(v -> dismiss());

        return builder.create();
    }
}
