package com.example.seriestracker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.seriestracker.R;
import com.example.seriestracker.database.MediaDatabase;
import com.example.seriestracker.modelo.Episode;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.ViewHolder> {
    private List<Episode> episodes;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public EpisodeAdapter(List<Episode> episodes) {
        this.episodes = episodes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_episode, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Episode episode = episodes.get(position);
        holder.episodeTitle.setText(episode.getTitle());
        holder.watchedCheckbox.setChecked(episode.isWatched());

        // ✅ Guardar cambio en la base de datos cuando se marca/desmarca
        holder.watchedCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            episode.setWatched(isChecked);

            // 🔹 Guardar en la base de datos en segundo plano
            executor.execute(() -> {
                MediaDatabase.getInstance(holder.itemView.getContext()).episodeDao().update(episode);
            });
        });
    }

    @Override
    public int getItemCount() {
        return episodes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView episodeTitle;
        CheckBox watchedCheckbox;

        public ViewHolder(View itemView) {
            super(itemView);
            episodeTitle = itemView.findViewById(R.id.textEpisodeTitle);
            watchedCheckbox = itemView.findViewById(R.id.checkEpisodeWatched);
        }
    }

    public void updateEpisodes(List<Episode> newEpisodes) {
        episodes.clear();
        episodes.addAll(newEpisodes);
        notifyDataSetChanged();
    }
}
