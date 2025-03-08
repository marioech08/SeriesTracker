package com.example.seriestracker.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.seriestracker.modelo.MediaItem;
import com.example.seriestracker.modelo.Episode;

@Database(entities = {MediaItem.class, Episode.class}, version = 2)
public abstract class MediaDatabase extends RoomDatabase {
    private static MediaDatabase instance;

    public abstract MediaDao mediaDao();
    public abstract EpisodeDao episodeDao();

    public static synchronized MediaDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            MediaDatabase.class, "media_database")
                    .fallbackToDestructiveMigration()
                    .build();

        }
        return instance;
    }
}
