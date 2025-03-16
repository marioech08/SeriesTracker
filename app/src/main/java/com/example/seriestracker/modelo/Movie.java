package com.example.seriestracker.modelo;

public class Movie {
    private String title;
    private String synopsis;
    private float rating;

    public Movie(String title, String synopsis, float rating) {
        this.title = title;
        this.synopsis = synopsis;
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public String getSynopsis() {  // ✅ Método que falta
        return synopsis;
    }

    public float getRating() {
        return rating;
    }
}
