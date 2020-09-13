package com.easyplex.ui.search;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.easyplex.data.local.entity.Media;
import com.easyplex.databinding.ItemSuggestedBinding;
import com.easyplex.ui.moviedetails.MovieDetailsActivity;
import com.easyplex.util.Tools;
import java.util.List;

import static com.easyplex.util.Constants.ARG_MOVIE;

/**
 * Adapter for Suggested Movies.
 *
 * @author Yobex.
 */
public class SuggestedAdapter extends RecyclerView.Adapter<SuggestedAdapter.SuggestedViewHolder> {

    private List<Media> suggestedList;


    public void setSearch(List<Media> mediaList) {
        this.suggestedList = mediaList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SuggestedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        ItemSuggestedBinding binding = ItemSuggestedBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new SuggestedAdapter.SuggestedViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestedViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        if (suggestedList != null) {
            return suggestedList.size();
        } else {
            return 0;
        }
    }

    class SuggestedViewHolder extends RecyclerView.ViewHolder {

        private final ItemSuggestedBinding binding;

        SuggestedViewHolder (@NonNull ItemSuggestedBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }

        void onBind(final int position) {

            final Media media = suggestedList.get(position);

            Context context = binding.itemMovieImage.getContext();

            Tools.onLoadMediaCover(context,binding.itemMovieImage, media.getPosterPath());

            binding.rootLayout.setOnClickListener(view -> {

                Intent intent = new Intent(context, MovieDetailsActivity.class);
                intent.putExtra(ARG_MOVIE, media);
                context.startActivity(intent);


            });

        }
    }
}
