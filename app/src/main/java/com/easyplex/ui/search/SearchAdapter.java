package com.easyplex.ui.search;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.easyplex.data.local.entity.Media;
import com.easyplex.databinding.ItemSearchBinding;
import com.easyplex.ui.moviedetails.MovieDetailsActivity;
import com.easyplex.ui.seriedetails.SerieDetailsActivity;
import com.easyplex.util.Tools;
import java.util.List;
import static com.easyplex.util.Constants.ARG_MOVIE;


/**
 * Adapter for Search Results (Movies,Series,Animes).
 *
 * @author Yobex.
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    private List<Media> castList;

    public void setSearch(List<Media> castList) {
        this.castList = castList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        ItemSearchBinding binding = ItemSearchBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new SearchViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
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

    class SearchViewHolder extends RecyclerView.ViewHolder {

        private final ItemSearchBinding binding;


        SearchViewHolder (@NonNull ItemSearchBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }

        void onBind(final int position) {

            final Media cast = castList.get(position);
            Context context = binding.itemMovieImage.getContext();

            if (cast.getTitle() !=null) {

                binding.rootLayout.setOnClickListener(view -> {

                    Intent intent = new Intent(context, MovieDetailsActivity.class);
                    intent.putExtra(ARG_MOVIE, cast);
                    context.startActivity(intent);


                });
            }else {


                binding.rootLayout.setOnClickListener(view -> {

                    Intent intent = new Intent(context, SerieDetailsActivity.class);
                    intent.putExtra(ARG_MOVIE, cast);
                    context.startActivity(intent);


                });

            }


            Tools.onLoadMediaCover(context,binding.itemMovieImage,cast.getPosterPath());

        }
    }
}
