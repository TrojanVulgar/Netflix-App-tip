package com.easyplex.ui.player.interfaces;

import androidx.annotation.Nullable;
import com.easyplex.data.model.media.MediaModel;


/**
 * Created by stoyan on 6/23/17.
 * This is an information callback interface, the relative callback will be triggered when certain user/player action has been made,
 * to update program. all the callback will be called after the matching action has been performed.
 */

public interface PlaybackActionCallback {



    void onProgress(@Nullable MediaModel mediaModel, long milliseconds, long durationMillis);

    void onSeek(@Nullable MediaModel mediaModel, long oldPositionMillis, long newPositionMillis);

    void onPlayToggle(@Nullable MediaModel mediaModel, boolean playing);

    void onSubtitles(@Nullable MediaModel mediaModel, boolean enabled);

    void onSubtitlesSelection();

    void onMediaEnded();

    void onLoadEpisodes();

    void onLoadNextEpisode();

    void onLoadSteaming();

    void onLoadSAds();


    void onLoadQualities();

    void activityActive(boolean active);

    void onCuePointReceived(long[] cuePoints);

    boolean isActive();

    boolean isPremuim();
}
