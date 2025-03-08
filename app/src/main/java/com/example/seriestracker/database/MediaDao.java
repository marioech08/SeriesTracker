package com.example.seriestracker.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.seriestracker.modelo.MediaItem;
import java.util.List;

@Dao
public interface MediaDao {
    @Insert
    void insert(MediaItem mediaItem);

    @Query("SELECT * FROM media_items")
    List<MediaItem> getAllMediaItems();
}
