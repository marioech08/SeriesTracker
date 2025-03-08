package com.example.seriestracker.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import com.example.seriestracker.modelo.MediaItem;
import java.util.List;

@Dao
public interface MediaDao {
    @Insert
    void insert(MediaItem mediaItem);

    @Query("SELECT * FROM media_items")
    List<MediaItem> getAllMediaItems();

    @Update
    void update(MediaItem mediaItem);

    @Delete
    void delete(MediaItem mediaItem);
}
