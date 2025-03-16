package com.example.seriestracker.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.seriestracker.R;
import com.example.seriestracker.database.MediaDatabase;
import com.example.seriestracker.modelo.MediaItem;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditMediaDialog extends DialogFragment {
    private MediaItem mediaItem;
    private OnMediaEditedListener listener;

    public interface OnMediaEditedListener {
        void onMediaEdited(MediaItem updatedItem);
    }

    public EditMediaDialog(MediaItem mediaItem, OnMediaEditedListener listener) {
        this.mediaItem = mediaItem;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_media, null);
        builder.setView(view);

        EditText inputEditTitle = view.findViewById(R.id.inputEditTitle);
        Spinner spinnerEditType = view.findViewById(R.id.spinnerEditType);
        Button buttonCancel = view.findViewById(R.id.buttonCancelEdit);
        Button buttonConfirm = view.findViewById(R.id.buttonConfirmEdit);

        inputEditTitle.setText(mediaItem.getTitle());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.media_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEditType.setAdapter(adapter);

        int position = adapter.getPosition(mediaItem.getType());
        spinnerEditType.setSelection(position);

        buttonConfirm.setText(getString(R.string.confirm_edit));
        buttonCancel.setText(getString(R.string.cancel));

        buttonConfirm.setOnClickListener(v -> {
            String newTitle = inputEditTitle.getText().toString();
            String newType = spinnerEditType.getSelectedItem().toString();

            if (!newTitle.isEmpty()) {
                mediaItem.setTitle(newTitle);
                mediaItem.setType(newType);

                // ✅ ACTUALIZAR EN LA BASE DE DATOS
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {
                    MediaDatabase.getInstance(getActivity()).mediaDao().update(mediaItem);
                    getActivity().runOnUiThread(() -> {
                        listener.onMediaEdited(mediaItem);
                        dismiss();
                    });
                });
            }
        });

        buttonCancel.setOnClickListener(v -> dismiss());

        return builder.create();
    }
}
