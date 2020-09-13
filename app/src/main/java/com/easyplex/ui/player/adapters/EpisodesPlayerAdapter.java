package com.easyplex.ui.player.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.easyplex.data.model.episode.Episode;
import com.easyplex.data.model.media.MediaModel;
import com.easyplex.databinding.RowPlayerEpisodesBinding;
import com.easyplex.ui.player.activities.EasyPlexMainPlayer;
import com.easyplex.util.DialogHelper;
import com.easyplex.util.Tools;
import java.util.List;


/**
 * Adapter for Serie Episodes.
 *
 * @author Yobex.
 */
public class EpisodesPlayerAdapter extends RecyclerView.Adapter<EpisodesPlayerAdapter.EpisodesPlayerViewHolder> {

    private List<Episode> castList;
    String currenserieid;
    String currentSeasons;
    String seasonsid;
    String currentSeasonsNumber;
    MediaModel mMediaModel;
    private EasyPlexMainPlayer player;
    ClickDetectListner clickDetectListner;


    public EpisodesPlayerAdapter(String serieid , String seasonsid, String seasonsidpostion, String currentseason,
                                 ClickDetectListner clickDetectListner,
                                 EasyPlexMainPlayer player) {

        this.currenserieid = serieid;
        this.currentSeasons = seasonsid;
        this.seasonsid = seasonsidpostion;
        this.currentSeasonsNumber = currentseason;
        this.clickDetectListner = clickDetectListner;
        this.player = player;

    }

    public void addSeasons(List<Episode> castList) {
        this.castList = castList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EpisodesPlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        RowPlayerEpisodesBinding binding = RowPlayerEpisodesBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new EpisodesPlayerAdapter.EpisodesPlayerViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodesPlayerViewHolder holder, int position) {
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


    class EpisodesPlayerViewHolder extends RecyclerView.ViewHolder {


        private final RowPlayerEpisodesBinding binding;

        EpisodesPlayerViewHolder (@NonNull RowPlayerEpisodesBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }

        void onBind(final int position) {

            final Episode episode = castList.get(position);


            Context context = binding.epcover.getContext();



            Tools.onLoadMediaCoverEpisode(context,binding.epcover,episode.getStillPath());




            binding.eptitle.setText(episode.getName());
            binding.epnumber.setText(episode.getEpisodeNumber()+" -");
            binding.epoverview.setText(episode.getOverview());

            binding.epLayout.setOnClickListener(v -> {



                if (episode.getVideos().isEmpty()) {

                    DialogHelper.showNoStreamAvailable(context);

                }else {


                    clickDetectListner.onEpisodeClicked(true);

                    String artwork = episode.getStillPath();
                    String type = "1";
                    String currentquality = episode.getVideos().get(0).getServer();
                    String name = "S0" + currentSeasons + "E" + episode.getEpisodeNumber() + " : " + episode.getName();
                    String videourl = episode.getVideos().get(0).getLink();

                    mMediaModel = MediaModel.media(null, null, currentquality, type, name, videourl, artwork, null,
                            null, null, null, null, null,
                            null,null,null,null, player.getPlayerController().getCurrentTvShowName());
                    player.playNext(mMediaModel);

                }

            });

        }
    }


}
