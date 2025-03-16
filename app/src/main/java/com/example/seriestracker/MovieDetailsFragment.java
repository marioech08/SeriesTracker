package com.example.seriestracker;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.seriestracker.R;
import com.example.seriestracker.modelo.Movie;

public class MovieDetailsFragment extends DialogFragment {
    private static final String ARG_TITLE = "title";
    private static final String ARG_SYNOPSIS = "synopsis";
    private static final String ARG_RATING = "rating";

    public static MovieDetailsFragment newInstance(Movie movie) {
        MovieDetailsFragment fragment = new MovieDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, movie.getTitle());
        args.putString(ARG_SYNOPSIS, movie.getSynopsis());
        args.putFloat(ARG_RATING, movie.getRating()); // Asegúrate de que `getRating()` devuelve un `float`
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_details, container, false);

        TextView titleTextView = view.findViewById(R.id.textViewTitle);
        TextView synopsisTextView = view.findViewById(R.id.textViewSynopsis);
        TextView ratingTextView = view.findViewById(R.id.textViewRating);

        if (getArguments() != null) {
            titleTextView.setText(getArguments().getString(ARG_TITLE));
            synopsisTextView.setText(getArguments().getString(ARG_SYNOPSIS));
            ratingTextView.setText(getString(R.string.rating_format, getArguments().getFloat(ARG_RATING)));
        }

        return view;
    }
}
