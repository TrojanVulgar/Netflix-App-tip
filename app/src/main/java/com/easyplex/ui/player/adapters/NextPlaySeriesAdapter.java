package com.easyplex.ui.player.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.easyplex.R;
import com.easyplex.data.model.episode.Episode;
import com.easyplex.data.model.media.MediaModel;
import com.easyplex.databinding.ViewNextMediaItemBinding;
import com.easyplex.ui.player.activities.EasyPlexMainPlayer;
import com.easyplex.util.DialogHelper;
import java.util.List;

/**
 * Adapter for Next Play Episode Adapter.
 *
 * @author Yobex.
 */
public class NextPlaySeriesAdapter extends RecyclerView.Adapter<NextPlaySeriesAdapter.NextPlaySeriesViewHolder> {

    private List<Episode> castList;
    String currentSerieId;
    String currentSeasons;
    String seasonsid;
    String currentSeasonsNumber;
    private EasyPlexMainPlayer player;
    private MediaModel mMediaModel;
    ClickDetectListner clickDetectListner;



    public NextPlaySeriesAdapter(String serieid , String seasonsid,
                                 String seasonsidpostion, String currentseason, ClickDetectListner
                                         clickDetectListner, EasyPlexMainPlayer player) {

        this.currentSerieId = serieid;
        this.currentSeasons = seasonsid;
        this.seasonsid = seasonsidpostion;
        this.currentSeasonsNumber = currentseason;
        this.clickDetectListner = clickDetectListner;
        this.player = player;

    }

    public void addNextPlay(List<Episode> castList) {
        this.castList = castList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NextPlaySeriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ViewNextMediaItemBinding binding = ViewNextMediaItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new NextPlaySeriesAdapter.NextPlaySeriesViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NextPlaySeriesViewHolder holder, int position) {
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


    class NextPlaySeriesViewHolder extends RecyclerView.ViewHolder {

        private final ViewNextMediaItemBinding binding;

        NextPlaySeriesViewHolder (@NonNull ViewNextMediaItemBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }

        void onBind(final int position) {

            final Episode episode = castList.get(position);

            Context context = binding.epcover.getContext();


            Glide.with(context).load(episode.getStillPath())
                    .centerCrop()
                    .placeholder(R.drawable.placehoder_episodes)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(1280,720)
                    .into(binding.epcover);


            binding.eptitle.setText(episode.getName());
            binding.epnumber.setText(episode.getEpisodeNumber()+" -");
            binding.epoverview.setText(episode.getOverview());

            binding.epLayout.setOnClickListener(v -> {

                if (episode.getVideos().isEmpty()) {

                    DialogHelper.showNoStreamAvailable(context);

                }else {

                    clickDetectListner.onNextMediaClicked(true);

                    String tvSeasonsId = seasonsid;
                    Integer currentEpisode = Integer.parseInt(episode.getEpisodeNumber());
                    String currentEpisodeName = episode.getName();
                    String currentEpisodeNumber = String.valueOf(episode.getTmdbId());
                    String currentSeason = currentSeasons;
                    String currentTvSeasonsNumber = NextPlaySeriesAdapter.this.currentSeasonsNumber;
                    String currentEpisodeTmdb = String.valueOf(episode.getTmdbId());
                    String artwork = episode.getStillPath();
                    String type = "1";
                    String currentQuality = episode.getVideos().get(0).getServer();
                    String name = "S0" + currentSeasons + "E" + episode.getEpisodeNumber() + " : " + episode.getName();
                    String episodeStreamLink = episode.getVideos().get(0).getLink();

                    mMediaModel = MediaModel.media(currentSerieId, null, currentQuality, type, name, episodeStreamLink, artwork, null, currentEpisode
                            , currentSeason, currentEpisodeTmdb,
                            tvSeasonsId, currentEpisodeName, currentTvSeasonsNumber,position,
                            currentEpisodeNumber,null, player.getPlayerController().getCurrentTvShowName());

                    player.playNext(mMediaModel);


                }

            });

        }
    }


}
