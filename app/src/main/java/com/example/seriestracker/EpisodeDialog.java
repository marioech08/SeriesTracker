package com.example.seriestracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.seriestracker.adapters.EpisodeAdapter;
import com.example.seriestracker.database.MediaDatabase;
import com.example.seriestracker.modelo.Episode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EpisodeDialog extends DialogFragment {
    private final int seriesId;
    private EpisodeAdapter adapter;
    private MediaDatabase database;
    private List<Episode> episodes;

    public EpisodeDialog(int seriesId) {
        this.seriesId = seriesId;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Episodios");

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_episode_list, null);
        builder.setView(view);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewEpisodes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        database = MediaDatabase.getInstance(getActivity());

        // 🔹 Inicializar lista y adaptador para evitar NullPointerException
        episodes = new ArrayList<>();
        adapter = new EpisodeAdapter(episodes);
        recyclerView.setAdapter(adapter);

        loadEpisodes(); // Cargar episodios desde la base de datos

        // Botón para añadir episodio
        TextView buttonAddEpisode = view.findViewById(R.id.buttonAddEpisode);
        buttonAddEpisode.setOnClickListener(v -> {
            AddEpisodeDialog addEpisodeDialog = new AddEpisodeDialog(seriesId, this::addEpisodeToList);
            addEpisodeDialog.show(getParentFragmentManager(), "AddEpisodeDialog");
        });

        // Botón para eliminar el último episodio
        TextView buttonDeleteEpisode = view.findViewById(R.id.buttonDeleteEpisode);
        buttonDeleteEpisode.setOnClickListener(v -> {
            if (!episodes.isEmpty()) {
                Episode lastEpisode = episodes.get(episodes.size() - 1);
                ExecutorService deleteExecutor = Executors.newSingleThreadExecutor();
                deleteExecutor.execute(() -> {
                    database.episodeDao().delete(lastEpisode);
                    requireActivity().runOnUiThread(this::loadEpisodes);
                });
            }
        });

        return builder.create();
    }

    public void loadEpisodes() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<Episode> loadedEpisodes = database.episodeDao().getEpisodesForSeries(seriesId);
            requireActivity().runOnUiThread(() -> {
                if (loadedEpisodes != null) {
                    episodes.clear();
                    episodes.addAll(loadedEpisodes);
                    adapter.notifyDataSetChanged(); // 🔹 Refrescar la lista en la UI
                }
            });
        });
    }

    public void addEpisodeToList(Episode newEpisode) {
        if (episodes == null) {
            episodes = new ArrayList<>();
        }

        episodes.add(newEpisode);

        if (adapter != null) {
            adapter.notifyItemInserted(episodes.size() - 1);
        } else {
            Log.e("EpisodeDialog", "Adapter is null, cannot update UI");
        }
    }
}
