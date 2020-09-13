package com.easyplex.ui.moviedetails.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.easyplex.R;
import com.easyplex.data.model.genres.Genre;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter for Movie Genres.
 *
 * @author Yobex.
 */
public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.MyViewHolder> {

    private List<Genre> genreList;

    public GenreAdapter() {

        //
    }

    public void addGenres(List<Genre> genreList) {
        this.genreList = genreList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.list_item_genre, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        if (genreList != null) {
            return genreList.size();
        } else {
            return 0;
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_genre)
        TextView mMovieGenres;

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void onBind(final int position) {

            final Genre genre = genreList.get(position);
            if (genre.getName() != null) {
                mMovieGenres.setText(genre.getName());
            }
        }
    }
}
