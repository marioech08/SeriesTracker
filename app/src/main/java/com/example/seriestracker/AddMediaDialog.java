package com.example.seriestracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.seriestracker.modelo.MediaItem;

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
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        android.view.View view = inflater.inflate(R.layout.dialog_add_media, null);
        builder.setView(view);

        // ✅ Verificar que los IDs existan en dialog_add_media.xml
        EditText inputTitle = view.findViewById(R.id.inputTitle);
        Spinner spinnerType = view.findViewById(R.id.spinnerType);
        Button buttonAdd = view.findViewById(R.id.buttonAdd);
        Button buttonCancel = view.findViewById(R.id.buttonCancel);

        // ✅ Configurar textos correctamente
        builder.setTitle(getString(R.string.add_media_title));
        inputTitle.setHint(getString(R.string.hint_title));
        buttonAdd.setText(getString(R.string.confirm_add));
        buttonCancel.setText(getString(R.string.cancel_add));

        // ✅ Cargar tipos de medios en el Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.media_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

        // ✅ Configurar eventos de botones
        buttonAdd.setOnClickListener(v -> {
            String title = inputTitle.getText().toString();
            String type = spinnerType.getSelectedItem().toString();

            if (!title.isEmpty()) {
                MediaItem newItem = new MediaItem(title, type, false);
                listener.onMediaAdded(newItem);
                dismiss();
            }
        });

        buttonCancel.setOnClickListener(v -> dismiss());

        return builder.create();
    }
}
