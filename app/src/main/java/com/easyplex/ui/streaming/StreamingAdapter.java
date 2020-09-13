package com.easyplex.ui.streaming;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.easyplex.data.model.media.MediaModel;
import com.easyplex.data.model.streaming.Streaming;
import com.easyplex.databinding.ItemShowStreamingBinding;
import com.easyplex.ui.manager.AuthManager;
import com.easyplex.ui.player.activities.EasyPlexMainPlayer;
import com.easyplex.ui.player.activities.EasyPlexPlayerActivity;
import com.easyplex.util.DialogHelper;
import java.util.List;
import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;

/**
 * Adapter for  Streaming Channels
 *
 * @author Yobex.
 */
public class StreamingAdapter extends RecyclerView.Adapter<StreamingAdapter.StreamingViewHolder> {

    private List<Streaming> streamingList;

    private AuthManager authManager;

    public void addStreaming(List<Streaming> castList , AuthManager authManager) {
        this.streamingList = castList;
        this.authManager = authManager;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StreamingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        ItemShowStreamingBinding binding = ItemShowStreamingBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new StreamingAdapter.StreamingViewHolder(binding);
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

        private final ItemShowStreamingBinding binding;


        StreamingViewHolder (@NonNull ItemShowStreamingBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }

        void onBind(final int position) {

            final Streaming streaming = streamingList.get(position);
            Context context = binding.imageViewShowCard.getContext();


            Glide.with(context).asBitmap().load(streaming.getPosterPath())
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transition(withCrossFade())
                    .into(binding.imageViewShowCard);



            if (streaming.getVip() == 1) binding.viewIsVip.setVisibility(View.VISIBLE);


            binding.streamTitle.setText(streaming.getName());

            binding.streamOverview.setText(streaming.getOverview());


            binding.upcomingRelative.setOnClickListener(v -> {


                if (streaming.getVip() == 1 && authManager.getUserInfo().getPremuim() == 0) {

                    DialogHelper.showPremuimWarning(context);



                }else {


                    String artwork = streaming.getPosterPath();
                    String name = streaming.getName();
                    String videourl = streaming.getLink();
                    String type = "streaming";
                    Intent intent = new Intent(context, EasyPlexMainPlayer.class);
                    intent.putExtra(EasyPlexPlayerActivity.TUBI_MEDIA_KEY, MediaModel.media(null,null,null,type, name, videourl, artwork, null
                            , null, null,null,
                            null,null,
                            null,
                            null,null,null,null));
                    context.startActivity(intent);

                }




            });


        }
    }


}
