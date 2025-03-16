package com.example.seriestracker.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.seriestracker.MovieDetailsFragment;
import com.example.seriestracker.R;
import com.example.seriestracker.adapters.MovieAdapter;
import com.example.seriestracker.modelo.Movie;
import com.example.seriestracker.utils.FileHelper;
import java.util.List;

public class MovieListFragment extends Fragment {
    private RecyclerView recyclerView;
    private MovieAdapter adapter;
    private List<Movie> movieList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewMovies);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // ✅ Cargar la lista de películas desde el archivo
        movieList = FileHelper.loadMovies(getContext());

        if (movieList != null && !movieList.isEmpty()) {
            adapter = new MovieAdapter(movieList, movie -> {
                MovieDetailsFragment fragment = MovieDetailsFragment.newInstance(movie);
                fragment.show(getParentFragmentManager(), "MovieDetailsFragment");
            });
            recyclerView.setAdapter(adapter);
        }

        return view;
    }
}
