package com.example.seriestracker.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import com.example.seriestracker.modelo.Episode;
import java.util.List;

@Dao
public interface EpisodeDao {
    @Insert
    void insert(Episode episode);

    @Update
    void update(Episode episode);

    @Delete
    void delete(Episode episode);

    @Query("SELECT COUNT(*) FROM episodes WHERE watched = 0")
    int countUnwatchedEpisodes();




    @Query("SELECT * FROM episodes WHERE seriesId = :seriesId")
    List<Episode> getEpisodesForSeries(int seriesId);
}
