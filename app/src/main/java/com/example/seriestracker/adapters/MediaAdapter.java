package com.example.seriestracker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.example.seriestracker.R;
import com.example.seriestracker.database.MediaDatabase;
import com.example.seriestracker.modelo.MediaItem;
import com.example.seriestracker.EpisodeDialog; // Import corregido

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder> {
    private List<MediaItem> mediaItems;
    private OnItemClickListener editListener;
    private OnItemClickListener deleteListener;

    public interface OnItemClickListener {
        void onItemClick(MediaItem item);
    }

    public MediaAdapter(List<MediaItem> mediaItems, OnItemClickListener editListener, OnItemClickListener deleteListener) {
        this.mediaItems = mediaItems;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_media, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MediaItem item = mediaItems.get(position);
        holder.title.setText(item.getTitle());
        holder.type.setText(item.getType());
        holder.watched.setChecked(item.isWatched());

        // Acción al hacer clic: distinguir entre serie y película
        holder.itemView.setOnClickListener(v -> {
            if (item.getType().equals("Serie")) {
                // Si es una serie, abrir el diálogo de episodios
                EpisodeDialog episodeDialog = new EpisodeDialog(item.getId());
                episodeDialog.show(((AppCompatActivity) v.getContext()).getSupportFragmentManager(), "EpisodeDialog");
            } else {
                // Si es una película, abrir edición
                editListener.onItemClick(item);
            }
        });

        // Guardar en la base de datos cuando se cambia el estado del CheckBox
        holder.watched.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setWatched(isChecked);
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                MediaDatabase.getInstance(holder.itemView.getContext()).mediaDao().update(item);
            });
        });

        // Acción para eliminar con pulsación larga
        holder.itemView.setOnLongClickListener(v -> {
            deleteListener.onItemClick(item);
            return true; // Indicar que el evento fue manejado
        });
    }

    @Override
    public int getItemCount() {
        return mediaItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, type;
        CheckBox watched;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textTitle);
            type = itemView.findViewById(R.id.textType);
            watched = itemView.findViewById(R.id.checkWatched);
        }
    }

    public void updateData(List<MediaItem> newItems) {
        mediaItems.clear();
        mediaItems.addAll(newItems);
        notifyDataSetChanged();
    }
}
