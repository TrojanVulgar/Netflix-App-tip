package com.easyplex.ui.animes;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.easyplex.data.model.episode.Episode;
import com.easyplex.data.model.media.MediaModel;
import com.easyplex.databinding.RowSeasonsBinding;
import com.easyplex.ui.player.activities.EasyPlexMainPlayer;
import com.easyplex.ui.player.activities.EasyPlexPlayerActivity;
import com.easyplex.util.DialogHelper;
import com.easyplex.util.Tools;
import java.util.List;

/**
 * Adapter for Anime Episodes.
 *
 * @author Yobex.
 */
public class AnimesEpisodeAdapter extends RecyclerView.Adapter<AnimesEpisodeAdapter.AnimeEpisodeViewHolder> {

    private List<Episode> episodeList;

    String currenserieid;
    String currentSeasons;
    String seasonsid;
    String currentSeasonsNumber;


    public AnimesEpisodeAdapter (String serieid , String seasonsid, String seasonsidpostion, String currentseason) {

        this.currenserieid = serieid;
        this.currentSeasons = seasonsid;
        this.seasonsid = seasonsidpostion;
        this.currentSeasonsNumber = currentseason;
    }


    public void addSeasons(List<Episode> castList) {
        this.episodeList = castList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AnimeEpisodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        RowSeasonsBinding binding = RowSeasonsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new AnimesEpisodeAdapter.AnimeEpisodeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimeEpisodeViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        if (episodeList != null) {
            return episodeList.size();
        } else {
            return 0;
        }
    }

    class AnimeEpisodeViewHolder extends RecyclerView.ViewHolder {


        private final RowSeasonsBinding binding;


        AnimeEpisodeViewHolder (@NonNull RowSeasonsBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }

        void onBind(final int position) {

            final Episode episode = episodeList.get(position);
            int epPosition = position;

            Context context = binding.epcover.getContext();



            Tools.onLoadMediaCoverEpisode(context,binding.epcover,episode.getStillPath());



            binding.eptitle.setText(episode.getName());
            binding.epnumber.setText(episode.getEpisodeNumber());
            binding.epoverview.setText(episode.getOverview());

            binding.epLayout.setOnClickListener(v -> {

                if (episode.getVideos().isEmpty()) {

                    DialogHelper.showNoStreamAvailable(context);

                }else {

                    Integer episodepostionnumber = epPosition;
                    String tvseasonid = seasonsid;
                    Integer currentep = Integer.parseInt(episode.getEpisodeNumber());
                    String currentepname = episode.getName();
                    String currenteptmdbnumber = String.valueOf(episode.getTmdbId());
                    String currentseasons = currentSeasons;
                    String currentseasonsNumber = currentSeasonsNumber;
                    String currentepimdb = String.valueOf(episode.getTmdbId());
                    String artwork = episode.getStillPath();
                    String type = "anime";
                    String currentquality = episode.getVideos().get(0).getServer();
                    String name = "S0" + currentSeasons + "E" + episode.getEpisodeNumber() + " : " + episode.getName();
                    String videourl = episode.getVideos().get(0).getLink();
                    Intent intent = new Intent(context, EasyPlexMainPlayer.class);
                    intent.putExtra(EasyPlexPlayerActivity.TUBI_MEDIA_KEY, MediaModel.media(currenserieid, null,currentquality, type, name, videourl, artwork, null, currentep
                            , currentseasons, currentepimdb,tvseasonid,
                            currentepname,currentseasonsNumber,episodepostionnumber,
                            currenteptmdbnumber,null,null));
                    context.startActivity(intent);
                }

            });

        }
    }


}
