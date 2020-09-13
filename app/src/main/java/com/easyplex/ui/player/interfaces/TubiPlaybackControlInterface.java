package com.easyplex.ui.player.interfaces;

import android.net.Uri;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;


public interface TubiPlaybackControlInterface {



    // on Ads Play (Hide some part of the player)
    void onAdsPlay(boolean playing,boolean isSkippable);


    // Return if media is an Ad or Movie

    boolean isCurrentVideoAd();

    String getCurrentMediaPremuim();

    // Player Control
    void triggerSubtitlesToggle(boolean enabled);

    void seekBy(long millisecond);


    void onCuePointReached(boolean reached);

    void seekTo(long millisecond);

    void closePlayer();

    Boolean isSubtitleEnabled(boolean enabled);

    boolean hasSubsActive();

    void loadPreview(long millisecond, long max);

    void triggerPlayOrPause(boolean setPlay);

    void scale();

    void clickQualitySettings();

    void clickOnSubs();



    // Series


    void onLoadEpisodes();

    void onLoadStreaming();

    void nextEpisode();




    // Display Media Info
    String getCurrentVideoName();

    Integer getCurrentEpisodePosition();

    String getVideoID();

    String getMediaSubstitleName();

    Uri getVideoUrl();

    Uri getMediaSubstitleUrl();

    Uri getMediaPoster();

    String getMediaType();

    String getCurrentEpTmdbNumber();

    String getVideoCurrentQuality();

    String nextSeaonsID();

    String getCurrentSeason();

    String getEpName();

    String getSeaonNumber();

    String getCurrentTvShowName();


    void setVideoAspectRatio(float widthHeightRatio);

    float getInitVideoAspectRatio();

    void setResizeMode(@AspectRatioFrameLayout.ResizeMode int resizeMode);

    void setPremuim(boolean premuim);



}
