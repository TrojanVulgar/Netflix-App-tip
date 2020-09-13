package com.easyplex.ui.player.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.easyplex.R;
import com.easyplex.data.model.media.MediaModel;
import com.easyplex.data.model.streaming.Streaming;
import com.easyplex.databinding.RowPlayerLivetvBinding;
import com.easyplex.ui.player.activities.EasyPlexMainPlayer;
import com.easyplex.util.DialogHelper;
import java.util.List;


/**
 * Adapter for Streaming.
 *
 * @author Yobex.
 */
public class StreamingPlayerAdapter extends RecyclerView.Adapter<StreamingPlayerAdapter.StreamingViewHolder> {

    private List<Streaming> streamingList;
    MediaModel mMediaModel;
    private EasyPlexMainPlayer doubleViewTubiPlayerActivity;
    ClickDetectListner clickDetectListner;



    public StreamingPlayerAdapter(EasyPlexMainPlayer doubleViewTubiPlayerActivity){

        this.doubleViewTubiPlayerActivity = doubleViewTubiPlayerActivity;
    }

    public void addStreaming(List<Streaming> castList, ClickDetectListner clickDetectListner) {
        this.streamingList = castList;
        notifyDataSetChanged();
        this.clickDetectListner = clickDetectListner;

    }

    @NonNull
    @Override
    public StreamingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {



        RowPlayerLivetvBinding binding = RowPlayerLivetvBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new StreamingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull StreamingViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        if (streamingList != null) {
            return streamingList.size();
        } else {
            return 0;
        }
    }


    class StreamingViewHolder extends RecyclerView.ViewHolder {


        private final RowPlayerLivetvBinding binding;

        StreamingViewHolder (@NonNull RowPlayerLivetvBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }

        void onBind(final int position) {

            final Streaming streaming = streamingList.get(position);


            Context context = binding.epcover.getContext();


            Glide.with(context).load(streaming.getPosterPath())
                    .centerCrop()
                    .placeholder(R.drawable.placehoder_episodes)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(1280,720)
                    .into(binding.epcover);



            binding.eptitle.setText(streaming.getName());

            binding.epoverview.setText(streaming.getOverview());

            binding.epLayout.setOnClickListener(v -> {


                if (streaming.getLink().isEmpty()) {

                    DialogHelper.showNoStreamAvailable(context);

                }else {




                    String artwork = streaming.getPosterPath();
                    String type = "streaming";
                    String name = streaming.getName();
                    String videourl = streaming.getLink();

                    mMediaModel = MediaModel.media(null, null, null, type, name, videourl, artwork, null,
                            null, null, null, null, null,
                            null,null,null,null,null);
                    doubleViewTubiPlayerActivity.playNext(mMediaModel);



                    clickDetectListner.onStreamingclicked(true);


                }

            });

        }
    }


}
