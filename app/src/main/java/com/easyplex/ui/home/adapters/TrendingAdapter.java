package com.easyplex.ui.home.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.easyplex.data.local.entity.Media;
import com.easyplex.data.model.genres.Genre;
import com.easyplex.databinding.ItemMovieBinding;
import com.easyplex.ui.moviedetails.MovieDetailsActivity;
import com.easyplex.util.Constants;
import com.easyplex.util.Tools;
import java.util.List;




/**
 * Adapter for Trending Movies.
 *
 * @author Yobex.
 */
public class TrendingAdapter extends RecyclerView.Adapter<TrendingAdapter.MainViewHolder> {

    private List<Media> castList;


    public void addTrending(List<Media> castList) {
        this.castList = castList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TrendingAdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemMovieBinding binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new TrendingAdapter.MainViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TrendingAdapter.MainViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        if (castList != null) {
            return castList.size();
        } else {
            return 0;
        }
    }

    class MainViewHolder extends RecyclerView.ViewHolder {

        private final ItemMovieBinding binding;

        MainViewHolder(@NonNull ItemMovieBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }



        void onBind(final int position) {


            final Media media = castList.get(position);


            Context context = binding.itemMovieImage.getContext();

            binding.movietitle.setText(media.getTitle());


            for (Genre genre : media.getGenres()) {

                binding.mgenres.setText(genre.getName());


            }

            binding.rootLayout.setOnClickListener(view -> {


                Intent intent = new Intent(context, MovieDetailsActivity.class);
                intent.putExtra(Constants.ARG_MOVIE, media);
                context.startActivity(intent);


            });

            if (media.getPremuim() == 1) {

                binding.moviePremuim.setVisibility(View.VISIBLE);


            }else {

                binding.moviePremuim.setVisibility(View.GONE);
            }


            Tools.onLoadMediaCoverAdapters(context,binding.itemMovieImage, media.getPosterPath());

        }
    }
}
