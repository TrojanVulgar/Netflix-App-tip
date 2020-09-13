package com.easyplex.ui.seriedetails;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import com.easyplex.data.model.episode.Episode;
import com.easyplex.data.model.media.MediaModel;
import com.easyplex.databinding.RowSeasonsBinding;
import com.easyplex.ui.manager.AuthManager;
import com.easyplex.ui.player.activities.EasyPlexMainPlayer;
import com.easyplex.ui.player.activities.EasyPlexPlayerActivity;
import com.easyplex.util.DialogHelper;
import com.easyplex.util.NetworkUtils;
import com.easyplex.util.Tools;

import java.util.List;

import static com.easyplex.util.Constants.WIFI_CHECK;

/**
 * Adapter for Series Episodes.
 *
 * @author Yobex.
 */
public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.EpisodeViewHolder> {

    private List<Episode> episodeList;
    String currentSerieId;
    String currentSeasons;
    String seasonId;
    String currentSeasonsNumber;
    String currentTvShowName;
    int premuim;
    private SharedPreferences preferences;
    private AuthManager authManager;


    public EpisodeAdapter(String serieid , String seasonId, String seasonsidpostion, String currentseason , int premuim,SharedPreferences preferences,AuthManager authManager,String currentTvShowName) {

        this.currentSerieId = serieid;
        this.currentSeasons = seasonId;
        this.seasonId = seasonsidpostion;
        this.currentSeasonsNumber = currentseason;
        this.premuim = premuim;
        this.authManager = authManager;
        this.currentTvShowName = currentTvShowName;
        this.preferences = preferences;
    }

    public void addSeasons(List<Episode> episodeList) {
        this.episodeList = episodeList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EpisodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RowSeasonsBinding binding = RowSeasonsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new EpisodeViewHolder(binding);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull EpisodeViewHolder holder, int position) {
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

    class EpisodeViewHolder extends RecyclerView.ViewHolder {

        private final RowSeasonsBinding binding;

        EpisodeViewHolder(@NonNull RowSeasonsBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        void onBind(final int position) {

            final Episode episode = EpisodeAdapter.this.episodeList.get(position);

            Context context = binding.epcover.getContext();


            Tools.onLoadMediaCoverEpisode(context,binding.epcover,episode.getStillPath());

            binding.eptitle.setText(episode.getName());
            binding.epnumber.setText(episode.getEpisodeNumber());
            binding.epoverview.setText(episode.getOverview());

            binding.epLayout.setOnClickListener(v -> {

                if (preferences.getBoolean(WIFI_CHECK, false) && NetworkUtils.isWifiConnected(context)) {

                    DialogHelper.showWifiWarning(context);

                }else if (premuim == 1 && authManager.getUserInfo().getPremuim() != 1) {


                    DialogHelper.showPremuimWarning(context);

                }else if ( episode.getVideos().isEmpty()) {

                    DialogHelper.showNoStreamAvailable(context);

                } else {


                    if (episode.getSubstitles() !=null && !episode.getSubstitles().isEmpty() ){


                        String mediaSubstitle = episode.getSubstitles().get(position).getLink();
                        String tvseasonid = seasonId;
                        Integer currentep = Integer.parseInt(episode.getEpisodeNumber());
                        String currentepname = episode.getName();
                        String currenteptmdbnumber = String.valueOf(episode.getTmdbId());
                        String currentseasons = currentSeasons;
                        String currentseasonsNumber = currentSeasonsNumber;
                        String currentepimdb = String.valueOf(episode.getTmdbId());
                        String artwork = episode.getStillPath();
                        String type = "1";
                        String currentquality = episode.getVideos().get(0).getServer();
                        String name = "S0" + currentSeasons + "E" + episode.getEpisodeNumber() + " : " + episode.getName();
                        String videourl = episode.getVideos().get(0).getLink();
                        Intent intent = new Intent(context, EasyPlexMainPlayer.class);

                        intent.putExtra(EasyPlexPlayerActivity.TUBI_MEDIA_KEY, MediaModel.media(currentSerieId, null, currentquality, type, name, videourl, artwork, mediaSubstitle, currentep
                                , currentseasons, currentepimdb, tvseasonid, currentepname, currentseasonsNumber, position, currenteptmdbnumber, String.valueOf(premuim),currentTvShowName));
                        context.startActivity(intent);

                    }else {

                        String tvseasonid = seasonId;
                        Integer currentep = Integer.parseInt(episode.getEpisodeNumber());
                        String currentepname = episode.getName();
                        String currenteptmdbnumber = String.valueOf(episode.getTmdbId());
                        String currentseasons = currentSeasons;
                        String currentseasonsNumber = currentSeasonsNumber;
                        String currentepimdb = String.valueOf(episode.getTmdbId());
                        String artwork = episode.getStillPath();
                        String type = "1";
                        String currentquality = episode.getVideos().get(0).getServer();
                        String name = "S0" + currentSeasons + "E" + episode.getEpisodeNumber() + " : " + episode.getName();
                        String videourl = episode.getVideos().get(0).getLink();
                        Intent intent = new Intent(context, EasyPlexMainPlayer.class);

                        intent.putExtra(EasyPlexPlayerActivity.TUBI_MEDIA_KEY, MediaModel.media(currentSerieId, null, currentquality, type, name, videourl, artwork, null, currentep
                                , currentseasons, currentepimdb, tvseasonid, currentepname, currentseasonsNumber, position, currenteptmdbnumber, String.valueOf(premuim),currentTvShowName));
                        context.startActivity(intent);

                    }





                }


            });

        }
    }


}
