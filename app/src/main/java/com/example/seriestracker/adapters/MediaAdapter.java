package com.example.seriestracker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.seriestracker.R;
import com.example.seriestracker.modelo.MediaItem;
import java.util.List;

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

        // Clic para editar
        holder.itemView.setOnClickListener(v -> editListener.onItemClick(item));

        // Pulsación larga para eliminar
        holder.itemView.setOnLongClickListener(v -> {
            deleteListener.onItemClick(item);
            return true;
        });

        // Marcar como visto/no visto
        holder.watched.setOnCheckedChangeListener((buttonView, isChecked) -> item.setWatched(isChecked));
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

    // Método para actualizar la lista después de agregar/eliminar elementos
    public void updateData(List<MediaItem> newItems) {
        mediaItems.clear();
        mediaItems.addAll(newItems);
        notifyDataSetChanged();
    }
}
