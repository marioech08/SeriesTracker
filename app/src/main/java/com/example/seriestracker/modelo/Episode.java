package com.example.seriestracker.modelo;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "episodes")
public class Episode {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int seriesId;
    private String title;
    private boolean watched;

    public Episode(int seriesId, String title, boolean watched) {
        this.seriesId = seriesId;
        this.title = title;
        this.watched = watched;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSeriesId() { return seriesId; }
    public void setSeriesId(int seriesId) { this.seriesId = seriesId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public boolean isWatched() { return watched; }
    public void setWatched(boolean watched) { this.watched = watched; }
}
