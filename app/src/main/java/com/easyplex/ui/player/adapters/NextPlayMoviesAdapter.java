package com.easyplex.ui.player.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.easyplex.R;
import com.easyplex.data.local.entity.Media;
import com.easyplex.data.model.genres.Genre;
import com.easyplex.data.model.media.MediaModel;
import com.easyplex.databinding.RowPlayerMoviesEndedBinding;
import com.easyplex.ui.player.activities.EasyPlexMainPlayer;
import com.easyplex.util.DialogHelper;
import java.io.Serializable;
import java.util.List;



/**
 * Adapter for Next Movie.
 *
 * @author Yobex.
 */
public class NextPlayMoviesAdapter extends RecyclerView.Adapter<NextPlayMoviesAdapter.NextPlayMoviesViewHolder> {

    private List<Media> castList;
    MediaModel mMediaModel;
    ClickDetectListner clickDetectListner;
    private EasyPlexMainPlayer player;



    public NextPlayMoviesAdapter(ClickDetectListner clickDetectListner, EasyPlexMainPlayer player) {

        this.clickDetectListner = clickDetectListner;
        this.player = player;

    }

    public void addSeasons(List<Media> castList) {
        this.castList = castList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NextPlayMoviesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RowPlayerMoviesEndedBinding binding = RowPlayerMoviesEndedBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new NextPlayMoviesAdapter.NextPlayMoviesViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NextPlayMoviesViewHolder holder, int position) {
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



    public Serializable getFirstItem() {
        if (castList != null) {
            return castList.get(0).getTmdbId();
        } else {
            return 0;
        }
    }


    class NextPlayMoviesViewHolder extends RecyclerView.ViewHolder {


        private final RowPlayerMoviesEndedBinding binding;

        NextPlayMoviesViewHolder (@NonNull RowPlayerMoviesEndedBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }

        void onBind(final int position) {

            final Media media = castList.get(position);
            Context context = binding.epcover.getContext();


            Glide.with(context).load(media.getBackdropPath())
                    .centerCrop()
                    .placeholder(R.drawable.placehoder_episodes)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(1280,720)
                    .into(binding.epcover);


            binding.movieName.setText(media.getTitle());

            binding.epoverview.setText(media.getOverview());



            for (Genre genre : media.getGenres()) {
                binding.movieGenre.setText(" - "+genre.getName());
            }


            binding.epLayout.setOnClickListener(v -> {



                if (media.getVideos().isEmpty()) {

                    DialogHelper.showNoStreamAvailable(context);

                }else {

                    clickDetectListner.onNextMediaClicked(true);

                    String artwork = media.getBackdropPath();
                    String movieId = media.getTmdbId();
                    String type = "0";
                    String currentQuality = media.getVideos().get(0).getServer();
                    String name = media.getTitle();
                    String streamLink = media.getVideos().get(0).getLink();

                    mMediaModel = MediaModel.media(movieId, null, currentQuality, type, name, streamLink, artwork, null,
                            null, null, null, null,
                            null,null,null,
                            null,null, player.getPlayerController().getCurrentTvShowName());
                    player.playNext(mMediaModel);

                }

            });

        }
    }


}
