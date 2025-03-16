package com.example.seriestracker.modelo;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "media_items")
public class MediaItem {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String type;
    private boolean watched;
    @Ignore
    public MediaItem(String title, String type, boolean watched) {
        this.title = title;
        this.type = type;
        this.watched = watched;
    }
    public MediaItem() {}
    public void setType(String type) {
        this.type = type;
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getType() { return type; }
    public boolean isWatched() { return watched; }
    public void setWatched(boolean watched) { this.watched = watched; }


}
