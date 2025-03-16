package com.example.seriestracker.utils;

import android.content.Context;
import com.example.seriestracker.modelo.Movie;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FileHelper {

    public static List<Movie> loadMovies(Context context) {
        List<Movie> movies = new ArrayList<>();
        try {
            InputStream inputStream = context.getAssets().open("movies.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 3) {
                    movies.add(new Movie(parts[0], parts[1], (float) Double.parseDouble(parts[2])));
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return movies;
    }
}
