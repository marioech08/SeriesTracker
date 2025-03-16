package com.example.seriestracker.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.seriestracker.ui.MovieListFragment;
import com.example.seriestracker.ui.MediaListFragment;

public class MainPagerAdapter extends FragmentStateAdapter {

    public MainPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new MediaListFragment(); // Lista de Series/Medios
        } else {
            return new MovieListFragment(); // Lista de Películas del TXT
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Dos pestañas
    }
}
