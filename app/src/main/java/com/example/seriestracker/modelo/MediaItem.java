package com.example.seriestracker.modelo;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "media_items")
public class MediaItem {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String type;
    private boolean watched;

    public MediaItem(String title, String type, boolean watched) {
        this.title = title;
        this.type = type;
        this.watched = watched;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getType() { return type; }
    public boolean isWatched() { return watched; }
    public void setWatched(boolean watched) { this.watched = watched; }
}
