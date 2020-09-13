package com.easyplex.ui.player.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import androidx.databinding.DataBindingUtil;
import com.easyplex.data.model.status.Status;
import com.easyplex.data.repository.SettingsRepository;
import com.easyplex.databinding.ActivityEasyplexPlayerBinding;
import com.easyplex.ui.manager.AdsManager;
import com.easyplex.ui.manager.AuthManager;
import com.easyplex.ui.manager.SettingsManager;
import com.easyplex.util.Tools;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.source.ads.AdsLoader;
import com.google.android.exoplayer2.source.ads.AdsMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import com.easyplex.R;
import com.easyplex.ui.player.controller.PlayerUIController;
import com.easyplex.ui.player.helpers.MediaHelper;
import com.easyplex.ui.player.interfaces.PlaybackActionCallback;
import com.easyplex.ui.player.interfaces.TubiPlaybackControlInterface;
import com.easyplex.data.model.media.MediaModel;
import com.easyplex.ui.player.utilities.EventLogger;
import org.jetbrains.annotations.NotNull;
import javax.inject.Inject;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;



/**
 * This is the base activity that prepare one instance of {@link SimpleExoPlayer} mMoviePlayer, this player is mean to serve as the main player to player content.
 * Along with some abstract methods to be implemented by subclass for extra functions.
 * You can use this class as it is and implement the abstract methods to be a standalone player to player video with customized UI controls and different forms of adaptive streaming.
 */
public abstract class EasyPlexPlayerActivity extends LifeCycleActivity implements  PlaybackActionCallback, AdsLoader.EventListener{


    ActivityEasyplexPlayerBinding binding;
    DefaultBandwidthMeter.Builder bandwidthMeter = new  DefaultBandwidthMeter.Builder(getBaseContext());
    public static final String TUBI_MEDIA_KEY = "tubi_media_key";
    protected SimpleExoPlayer mMoviePlayer;
    protected DefaultTrackSelector mTrackSelector;
    protected boolean isActive = false;
    private boolean activityRuning = false;

    MediaSource mediaSource;

    /**
     * ideally, only one instance of {@link MediaModel} and its arrtibute {@link MediaSource} for movie should be created throughout the whole movie playing experiences.
     */

    protected MediaModel mediaModel;
    private DataSource.Factory mMediaDataSourceFactory;


    @Inject
    PlayerUIController playerUIController;

    @Inject
    AdsManager adsManager;

    @Inject
    SettingsManager settingsManager;


    @Inject
    AuthManager authManager;


    @Inject
    SharedPreferences sharedPreferences;


    @Inject
    SettingsRepository settingsRepository;


    @Inject
    SettingsRepository authRepository;


    private ImaAdsLoader adsLoader;



    public abstract View addUserInteractionView();


    protected abstract void onPlayerReady();

    protected abstract void updateResumePosition();

    protected PlayerUIController getUiController() {
        return playerUIController;


    }

    protected abstract boolean isCaptionPreferenceEnable();




    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Tools.hideSystemPlayerUi(this, true);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        parseIntent();
        mMediaDataSourceFactory = buildDataSourceFactory();
        initLayout();
        mMoviePlayer = new SimpleExoPlayer.Builder(this).build();

        authRepository.getStatus()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Status>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                        //

                    }

                    @Override
                    public void onNext(Status status) {

                        //

                    }

                    @SuppressLint("ClickableViewAccessibility")
                    @Override
                    public void onError(Throwable e) {

                        binding.viewStatus.setVisibility(View.VISIBLE);
                        binding.restartApp.setOnClickListener(view -> finishAffinity());
                        binding.closeStatus.setOnClickListener(view -> finishAffinity());
                        binding.viewStatus.setOnTouchListener((view, motionEvent) -> {
                            finishAffinity();
                            return false;
                        });


                    }

                    @Override
                    public void onComplete() {

                        //

                    }
                });


        // if the user has a premuim plan ( enable or disable ads on the main player )
        if (authManager.getUserInfo().getPremuim() == 0 && adsManager.getAds().getLink() != null && settingsManager.getSettings().getAds() != 0) {

            adsLoader = new ImaAdsLoader(this, Uri.parse(adsManager.getAds().getLink()));

            adsLoader.getAdsLoader().addAdsLoadedListener(adsManagerLoadedEvent -> adsManagerLoadedEvent.getAdsManager().addAdEventListener(adEvent -> {

                // These are the suggested event types to handle. For full list of all ad
                // event types, see the documentation for AdEvent.AdEventType.
                switch (adEvent.getType()) {
                    case LOADED:

                        binding.tubitvPlayer.getPlayerController().onAdsPlay(true, adEvent.getAd().isSkippable());


                        break;
                    case CONTENT_PAUSE_REQUESTED:

                        break;
                    case CONTENT_RESUME_REQUESTED:

                        break;
                    case PAUSED:

                        break;
                    case RESUMED:

                        break;
                    case ALL_ADS_COMPLETED:

                        if (activityRuning) {

                            binding.tubitvPlayer.getPlayerController().onAdsPlay(false, false);
                            adsLoader.release();
                            adsLoader.stop();

                        }

                        break;
                    default:
                        break;
                }

            }));

        }


    }

    @Override
    public void activityActive( boolean isActive){

        activityRuning = isActive;

    }



    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        releaseMoviePlayer();
        setIntent(intent);

    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            setupExo();
        }

       activityRuning = true;

    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || mMoviePlayer == null)) {
            setupExo();


            activityRuning = true;
        }


        if (getPlayerController().hasSubsActive()) {

            String subs = String.valueOf(getPlayerController().getMediaSubstitleUrl());
            String id = getPlayerController().getVideoID();
            String externalId = getPlayerController().getMediaSubstitleName();
            String currentQuality = getPlayerController().getVideoCurrentQuality();
            String type = getPlayerController().getMediaType();
            String artwork = (String.valueOf(getPlayerController().getMediaPoster())) ;
            String name = getPlayerController().getCurrentVideoName();
            String videoUrl = (String.valueOf(getPlayerController().getVideoUrl())) ;
            MediaModel mMediaModel = MediaModel.media(id, externalId, currentQuality, type, name, videoUrl, artwork, subs,
                    null, null, null, null,
                    null, null, null, null,null,getPlayerController().getCurrentTvShowName());
            ((EasyPlexMainPlayer)(this)).backState(mMediaModel);

        }

    }


    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releaseMoviePlayer();
        }

        activityRuning = false;


    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releaseMoviePlayer();
        }

        activityRuning = false;


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (adsLoader !=null) {

            adsLoader.release();
            adsLoader.stop();

        }

        binding =null;


    }



    @Override
    public boolean isActive() {
        return isActive;
    }

    @SuppressWarnings("ConstantConditions")
    protected void parseIntent() {
        String errorNoMediaMessage = getResources().getString(R.string.no_media_error_message);
        Assertions.checkState(getIntent() != null && getIntent().getExtras() != null,
                errorNoMediaMessage);
        mediaModel = (MediaModel) getIntent().getExtras().getSerializable(TUBI_MEDIA_KEY);
        Assertions.checkState(mediaModel != null,
                errorNoMediaMessage);
    }

    protected void initLayout() {

        binding = DataBindingUtil.setContentView(this,R.layout.activity_easyplex_player);
        binding.tubitvPlayer.requestFocus();
        binding.vpaidWebview.setBackgroundColor(Color.BLACK);
        binding.tubitvPlayer.addUserInteractionView(addUserInteractionView());

    }

    private void setCaption(boolean isOn) {
        if (binding.tubitvPlayer.getControlView() != null) {
            binding.tubitvPlayer.getPlayerController().triggerSubtitlesToggle(isOn);
        }
    }

    private void setupExo() {

        initMoviePlayer();
        setCaption(isCaptionPreferenceEnable());
        isActive = true;
        onPlayerReady();
        binding.tubitvPlayer.getPlayerController().triggerSubtitlesToggle(true);

    }

    protected void initMoviePlayer() {

        // 3. Create the mMoviePlayer
        mMoviePlayer = new SimpleExoPlayer.Builder(this).build();
        EventLogger mEventLogger = new EventLogger(mTrackSelector);
        mMoviePlayer.addAnalyticsListener(mEventLogger);
        mMoviePlayer.addMetadataOutput(mEventLogger);

        if (adsLoader !=null ) {

            adsLoader.setPlayer(mMoviePlayer);

        }

            binding.tubitvPlayer.setPlayer(mMoviePlayer, this);
            binding.tubitvPlayer.setMediaModel(mediaModel);

    }




    public void releaseMoviePlayer() {
        if (mMoviePlayer != null) {
            mMoviePlayer.release();
            mMoviePlayer = null;
            mTrackSelector = null;

            if (adsLoader !=null ) {

                adsLoader.setPlayer(null);

            }



        }
        isActive = false;

    }

    protected MediaSource buildMediaSource(MediaModel model) {

        int type = TextUtils.isEmpty(model.getMediaExtension()) ? Util.inferContentType(model.getMediaUrl())
                : Util.inferContentType("." + model.getMediaExtension());

        switch (type) {
            case C.TYPE_SS:
            case C.TYPE_OTHER:
                mediaSource = new ProgressiveMediaSource.Factory(mMediaDataSourceFactory).createMediaSource(model.getMediaUrl());


                if (adsLoader !=null ) {


                    mediaSource = new AdsMediaSource(mediaSource,mMediaDataSourceFactory,adsLoader, binding.tubitvPlayer);

                }


                break;
            case C.TYPE_DASH:
                mediaSource = new DashMediaSource.Factory(mMediaDataSourceFactory).createMediaSource(model.getMediaUrl());
                break;

            case C.TYPE_HLS:

                mediaSource = new HlsMediaSource.Factory(mMediaDataSourceFactory).createMediaSource(model.getMediaUrl());


                break;
            default:
                throw new IllegalStateException("Unsupported type: " + type);
        }

        if (model.getMediaSubstitleUrl() != null) {

            MediaSource subtitleSource = new SingleSampleMediaSource.Factory(mMediaDataSourceFactory)
                    .createMediaSource(model.getMediaSubstitleUrl(), Format.createTextSampleFormat(null, MimeTypes.APPLICATION_SUBRIP, null, Format.NO_VALUE,
                            C.SELECTION_FLAG_DEFAULT, "en", null, 0), C.TIME_UNSET);


            // Plays the video with the sideloaded subtitle.
            mediaSource = new MergingMediaSource(mediaSource, subtitleSource);
        }


        return mediaSource;
    }

    /**
     * Returns a new DataSource factory.MainActivity
     *
     * @return A new DataSource factory.
     */
    protected DataSource.Factory buildDataSourceFactory() {

        return MediaHelper.buildDataSourceFactory(this, bandwidthMeter);
    }


    public TubiPlaybackControlInterface getPlayerController() {
        if (binding.tubitvPlayer.getPlayerController() != null) {

            return binding.tubitvPlayer.getPlayerController();
        }
        return null;
    }



    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            Tools.hideSystemPlayerUi(this,true,0);
        }
    }




}
