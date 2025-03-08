package com.example.seriestracker.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.seriestracker.modelo.MediaItem;

@Database(entities = {MediaItem.class}, version = 1)
public abstract class MediaDatabase extends RoomDatabase {
    private static MediaDatabase instance;
    public abstract MediaDao mediaDao();

    public static synchronized MediaDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    MediaDatabase.class, "media_database").build();
        }
        return instance;
    }
}
