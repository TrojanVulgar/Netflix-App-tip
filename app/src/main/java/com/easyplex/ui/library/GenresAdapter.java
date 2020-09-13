package com.easyplex.ui.library;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.easyplex.data.local.entity.Media;
import com.easyplex.databinding.ItemGenreBinding;
import com.easyplex.ui.moviedetails.MovieDetailsActivity;
import com.easyplex.ui.seriedetails.SerieDetailsActivity;
import com.easyplex.util.Tools;
import java.util.List;

import static com.easyplex.util.Constants.ARG_MOVIE;

/**
 * Adapter for Genre Movies.
 *
 * @author Yobex.
 */
public class GenresAdapter extends RecyclerView.Adapter<GenresAdapter.MainViewHolder> {

    private List<Media> genresList;

    public void addToContent(List<Media> mediaList) {
        this.genresList = mediaList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GenresAdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        ItemGenreBinding binding = ItemGenreBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new GenresAdapter.MainViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull GenresAdapter.MainViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        if (genresList != null) {
            return genresList.size();
        } else {
            return 0;
        }
    }

    class MainViewHolder extends RecyclerView.ViewHolder {


        private final ItemGenreBinding binding;


        MainViewHolder(@NonNull ItemGenreBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }


        void onBind(final int position) {


            final Media media = genresList.get(position);
            Context context = binding.itemMovieImage.getContext();


            if (media.getTitle() !=null) {


                binding.rootLayout.setOnClickListener(view -> {

                    Intent intent = new Intent(context, MovieDetailsActivity.class);
                    intent.putExtra(ARG_MOVIE, media);
                    context.startActivity(intent);


                });
            }else  {

                binding.rootLayout.setOnClickListener(view -> {

                Intent intent = new Intent(context, SerieDetailsActivity.class);
                intent.putExtra(ARG_MOVIE, media);
                context.startActivity(intent);


            });

            }


            Tools.onLoadMediaCover(context,binding.itemMovieImage, media.getPosterPath());
        }
    }
}
