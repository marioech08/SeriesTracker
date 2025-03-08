package com.example.seriestracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.seriestracker.database.MediaDatabase;
import com.example.seriestracker.modelo.MediaItem;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditMediaDialog extends DialogFragment {
    private MediaItem mediaItem;
    private OnMediaUpdatedListener listener;

    public interface OnMediaUpdatedListener {
        void onMediaUpdated(MediaItem updatedItem);
    }

    public EditMediaDialog(MediaItem item, OnMediaUpdatedListener listener) {
        this.mediaItem = item;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Editar " + mediaItem.getTitle());

        EditText input = new EditText(getActivity());
        input.setText(mediaItem.getTitle());
        builder.setView(input);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String newTitle = input.getText().toString();
            if (!newTitle.isEmpty()) {
                mediaItem.setTitle(newTitle);
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                executorService.execute(() -> {
                    MediaDatabase.getInstance(getActivity()).mediaDao().update(mediaItem);
                    if (listener != null) {
                        listener.onMediaUpdated(mediaItem);
                    }
                });
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        return builder.create();
    }
}
