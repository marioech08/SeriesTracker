package com.example.seriestracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.seriestracker.modelo.MediaItem;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddMediaDialog extends DialogFragment {
    private OnMediaAddedListener listener;

    public interface OnMediaAddedListener {
        void onMediaAdded(MediaItem newItem);
    }

    public AddMediaDialog(OnMediaAddedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Agregar Nueva Película o Serie");

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        android.view.View view = inflater.inflate(R.layout.dialog_add_media, null);
        builder.setView(view);

        EditText inputTitle = view.findViewById(R.id.inputTitle);
        Spinner spinnerType = view.findViewById(R.id.spinnerType);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.media_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

        builder.setPositiveButton("Agregar", (dialog, which) -> {
            String title = inputTitle.getText().toString();
            String type = spinnerType.getSelectedItem().toString();

            if (!title.isEmpty()) {
                MediaItem newItem = new MediaItem(title, type, false);
                listener.onMediaAdded(newItem);

                // Si es una serie, mostrar el diálogo de episodios
                if (type.equals("Serie")) {
                    int seriesId = newItem.getId(); // Asumimos que el ID es generado automáticamente
                    AddEpisodeDialog episodeDialog = new AddEpisodeDialog(seriesId);
                    episodeDialog.show(getParentFragmentManager(), "AddEpisodeDialog");
                }
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        return builder.create();
    }
}
