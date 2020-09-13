package com.easyplex.ui.player.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.easyplex.data.model.media.MediaModel;
import com.easyplex.data.model.stream.MediaStream;
import com.easyplex.databinding.RowSubstitleBinding;
import com.easyplex.ui.player.activities.EasyPlexMainPlayer;

import java.util.List;
import javax.inject.Inject;

/**
 * Adapter for Movie Qualities.
 *
 * @author Yobex.
 */
public class MovieQualitiesAdapter extends RecyclerView.Adapter<MovieQualitiesAdapter.CastViewHolder> {

    private List<MediaStream> mediaStreams;
    private MediaModel mMediaModel;
    private EasyPlexMainPlayer doubleViewTubiPlayerActivity;

    ClickDetectListner clickDetectListner;



    @Inject
    public MovieQualitiesAdapter(EasyPlexMainPlayer doubleViewTubiPlayerActivity){

        this.doubleViewTubiPlayerActivity = doubleViewTubiPlayerActivity;
    }


    public void addSeasons(List<MediaStream> castList, ClickDetectListner clickDetectListner) {
        this.mediaStreams = castList;
        notifyDataSetChanged();
        this.clickDetectListner = clickDetectListner;

    }

    @NonNull
    @Override
    public MovieQualitiesAdapter.CastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        RowSubstitleBinding binding = RowSubstitleBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new MovieQualitiesAdapter.CastViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieQualitiesAdapter.CastViewHolder holder, int position) {
        holder.onBind(position);
    }



    @Override
    public int getItemCount() {
        if (mediaStreams != null) {
            return mediaStreams.size();
        } else {
            return 0;
        }
    }

    class CastViewHolder extends RecyclerView.ViewHolder {

        private final RowSubstitleBinding binding;

        CastViewHolder (@NonNull RowSubstitleBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }

        void onBind(final int position) {

            final MediaStream qualitySelected = mediaStreams.get(position);

            binding.eptitle.setText(qualitySelected.getServer());

            binding.eptitle.setOnClickListener(v -> {


                String id = doubleViewTubiPlayerActivity.getPlayerController().getVideoID();
                String externalId = doubleViewTubiPlayerActivity.getPlayerController().getMediaSubstitleName();
                String type = doubleViewTubiPlayerActivity.getPlayerController().getMediaType();
                String currentQuality = doubleViewTubiPlayerActivity.getPlayerController().getVideoCurrentQuality();
                String artwork = (String.valueOf(doubleViewTubiPlayerActivity.getPlayerController().getMediaPoster())) ;
                String name = doubleViewTubiPlayerActivity.getPlayerController().getCurrentVideoName();
                mMediaModel = MediaModel.media(id,externalId,currentQuality,type,name, qualitySelected.getLink(), artwork, null,null,null
                        ,null,null,null,
                        null,
                        null,null,null,doubleViewTubiPlayerActivity.getPlayerController().getCurrentTvShowName());
                doubleViewTubiPlayerActivity.update(mMediaModel);
                doubleViewTubiPlayerActivity.getPlayerController().isSubtitleEnabled(true);
                clickDetectListner.onQualityClicked(true);


            });

        }
    }


}
