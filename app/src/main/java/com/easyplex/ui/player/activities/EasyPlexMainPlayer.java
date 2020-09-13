package com.easyplex.ui.player.activities;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.webkit.WebBackForwardList;
import android.webkit.WebHistoryItem;
import android.webkit.WebView;
import android.widget.AdapterView;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.easyplex.R;
import com.easyplex.data.local.entity.Media;
import com.easyplex.data.model.MovieResponse;
import com.easyplex.data.model.episode.EpisodeStream;
import com.easyplex.data.model.genres.Genre;
import com.easyplex.data.model.serie.Season;
import com.easyplex.data.model.stream.MediaStream;
import com.easyplex.data.model.streaming.Streaming;
import com.easyplex.data.model.substitles.MediaSubstitle;
import com.easyplex.data.repository.AnimeRepository;
import com.easyplex.data.repository.MovieRepository;
import com.easyplex.ui.manager.StatusManager;
import com.easyplex.ui.player.adapters.EpisodesPlayerAdapter;
import com.easyplex.ui.player.adapters.ClickDetectListner;
import com.easyplex.ui.player.adapters.MovieQualitiesAdapter;
import com.easyplex.ui.player.adapters.NextPlayMoviesAdapter;
import com.easyplex.ui.player.adapters.NextPlaySeriesAdapter;
import com.easyplex.ui.player.adapters.SerieQualitiesAdapter;
import com.easyplex.ui.player.adapters.StreamingPlayerAdapter;
import com.easyplex.ui.player.adapters.SubstitlesAdapter;
import com.easyplex.util.DialogHelper;
import com.easyplex.util.SpacingItemDecoration;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.easyplex.ui.manager.SettingsManager;
import com.easyplex.ui.player.bindings.PlayerController;
import com.easyplex.ui.player.controller.PlayerAdLogicController;
import com.easyplex.ui.player.controller.PlayerUIController;
import com.easyplex.ui.player.fsm.Input;
import com.easyplex.ui.player.fsm.callback.AdInterface;
import com.easyplex.ui.player.fsm.concrete.AdPlayingState;
import com.easyplex.ui.player.fsm.concrete.VpaidState;
import com.easyplex.ui.player.fsm.listener.AdPlayingMonitor;
import com.easyplex.ui.player.fsm.listener.CuePointMonitor;
import com.easyplex.ui.player.fsm.state_machine.FsmPlayer;
import com.easyplex.ui.player.interfaces.AutoPlay;
import com.easyplex.ui.player.interfaces.DoublePlayerInterface;
import com.easyplex.data.model.ads.AdMediaModel;
import com.easyplex.data.model.ads.AdRetriever;
import com.easyplex.data.model.ads.CuePointsRetriever;
import com.easyplex.data.model.media.MediaModel;
import com.easyplex.ui.player.interfaces.VpaidClient;
import com.easyplex.ui.player.utilities.ExoPlayerLogger;
import com.easyplex.ui.player.utilities.PlayerDeviceUtils;
import com.easyplex.ui.player.views.UIControllerView;
import com.easyplex.util.Constants;
import com.easyplex.util.Tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.inject.Inject;
import dagger.android.AndroidInjection;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;
import static com.easyplex.util.Constants.AUTO_PLAY;
import static com.easyplex.util.Constants.E;
import static com.easyplex.util.Constants.EP;
import static com.easyplex.util.Constants.S0;
import static com.easyplex.util.Constants.SPECIALS;
import static com.easyplex.util.Constants.UPNEXT;


/**
 * Created by allensun on 7/24/17.
 */
public class EasyPlexMainPlayer extends EasyPlexPlayerActivity implements DoublePlayerInterface, AutoPlay, ClickDetectListner {


    private static final String TAG = "EasyPlexMainPlayer";
    protected SimpleExoPlayer adPlayer;
    SubstitlesAdapter mSubstitleAdapter;
    MovieQualitiesAdapter movieQualitiesAdapter;
    SerieQualitiesAdapter serieQualitiesAdapter;
    private MediaModel mMediaModel;
    private CountDownTimer mCountDownTimer;

    private ImaAdsLoader adsLoader;


    @Inject
    FsmPlayer fsmPlayer;
    @Inject
    PlayerUIController uiController;
    @Inject
    AdPlayingMonitor adPlayingMonitor;
    @Inject
    CuePointMonitor cuePointMonitor;
    @Inject
    AdRetriever adRetriever;
    @Inject
    CuePointsRetriever cuePointsRetriever;
    @Inject
    AdInterface adInterface;
    @Inject
    PlayerAdLogicController playerComponentController;
    @Inject
    VpaidClient vpaidClient;

    @Inject
    SharedPreferences preferences;

    @Inject
    SettingsManager appSettingsManager;

    @Inject
    StatusManager statusManager;

    @Inject
    SharedPreferences.Editor sharedPreferencesEditor;

    @Inject
    PlayerController playerController;

    @Inject
    MovieRepository movieRepository;

    @Inject
    AnimeRepository animeRepository;


    EpisodesPlayerAdapter mEPAdapter;
    StreamingPlayerAdapter mStreamingAdapter;
    NextPlaySeriesAdapter nextPlaySeriesAdapter;
    NextPlayMoviesAdapter nextPlayMoviesAdapter;
    ClickDetectListner clickDetectListner = this;

    private DefaultTrackSelector trackSelectorAd;

    protected AdRetriever getAdRetriever() {
        return adRetriever;
    }

    protected CuePointsRetriever getCuePointsRetriever() {
        return cuePointsRetriever;
    }

    @Override
    protected PlayerUIController getUiController() {
        return uiController;
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);


        Tools.hideSystemPlayerUi(this,true);

    }


    @Override
    public View addUserInteractionView() {

        return new UIControllerView(getBaseContext()).setPlayerController((PlayerController) getPlayerController());

    }


    protected FsmPlayer getFsmPlayer() {
        return fsmPlayer;
    }

    @Override
    protected void initMoviePlayer() {
        super.initMoviePlayer();
        createMediaSource(mediaModel);
        mMoviePlayer.seekTo(uiController.getMovieResumePosition());
        if (!PlayerDeviceUtils.useSinglePlayer()) {
            setupAdPlayer();

        }
    }

    @Override
    protected void onPlayerReady() {
        prepareFSM();

    }

    @Override
    public void releaseMoviePlayer() {
        super.releaseMoviePlayer();
        if (!PlayerDeviceUtils.useSinglePlayer()) {
            releaseAdPlayer();
        }
        updateResumePosition();
    }

    private void setupAdPlayer() {

        trackSelectorAd = new DefaultTrackSelector(this);
        adPlayer = new SimpleExoPlayer.Builder(this)
                .setTrackSelector(trackSelectorAd)
                .build();
    }

    private void releaseAdPlayer() {
        if (adPlayer != null) {
            updateAdResumePosition();
            adPlayer.release();
            adPlayer = null;
            trackSelectorAd = null;

        }

        if (binding.vpaidWebview != null) {
            binding.vpaidWebview.loadUrl(Constants.EMPTY_URL);
            binding.vpaidWebview.clearHistory();
        }
    }

    private void updateAdResumePosition() {
        if (adPlayer != null && uiController != null) {
            int adResumeWindow = adPlayer.getCurrentWindowIndex();
            long adResumePosition = adPlayer.isCurrentWindowSeekable() ? Math.max(0, adPlayer.getCurrentPosition())
                    : C.TIME_UNSET;
            uiController.setAdResumeInfo(adResumeWindow, adResumePosition);
        }
    }

    /**
     * update the movie and ad playing position when players are released
     */


    @Override
    public void updateResumePosition() {

        //keep track of movie player's position when activity resume back
        if (mMoviePlayer != null && uiController != null
                && mMoviePlayer.getPlaybackState() != Player.STATE_IDLE) {
            int resumeWindow = mMoviePlayer.getCurrentWindowIndex();
            long resumePosition = mMoviePlayer.isCurrentWindowSeekable() ?
                    Math.max(0, mMoviePlayer.getCurrentPosition())
                    :
                    C.TIME_UNSET;
            uiController.setMovieResumeInfo(resumeWindow, resumePosition);
            ExoPlayerLogger.i(Constants.FSMPLAYER_TESTING, resumePosition + "");
        }

        //keep track of ad player's position when activity resume back, only keep track when current state is in AdPlayingState.
        if (fsmPlayer.getCurrentState() instanceof AdPlayingState && adPlayer != null && uiController != null
                && adPlayer.getPlaybackState() != Player.STATE_IDLE) {
            int adResumeWindow = adPlayer.getCurrentWindowIndex();
            long adResumePosition = adPlayer.isCurrentWindowSeekable() ? Math.max(0, adPlayer.getCurrentPosition())
                    : C.TIME_UNSET;
            uiController.setAdResumeInfo(adResumeWindow, adResumePosition);
        }

    }


    @Override
    protected boolean isCaptionPreferenceEnable() {
        return true;
    }

    /**
     * prepare / set up FSM and inject all the elements into the FSM
     */
    @Override
    public void prepareFSM() {

        //update the playerUIController view, need to update the view everything when two ExoPlayer being recreated in activity lifecycle.
        uiController.setContentPlayer(mMoviePlayer);

        if (!PlayerDeviceUtils.useSinglePlayer()) {
            uiController.setAdPlayer(adPlayer);
        }

        uiController.setExoPlayerView(binding.tubitvPlayer);
        uiController.setVpaidWebView(binding.vpaidWebview);

        //update the MediaModel
        fsmPlayer.setController(uiController);
        fsmPlayer.setMovieMedia(mediaModel);
        fsmPlayer.setAdRetriever(adRetriever);
        fsmPlayer.setCuePointsRetriever(cuePointsRetriever);
        fsmPlayer.setAdServerInterface(adInterface);

        //set the PlayerComponentController.
        playerComponentController.setAdPlayingMonitor(adPlayingMonitor);
        playerComponentController.setTubiPlaybackInterface(this);
        playerComponentController.setDoublePlayerInterface(this);
        playerComponentController.setCuePointMonitor(cuePointMonitor);
        playerComponentController.setVpaidClient(vpaidClient);
        fsmPlayer.setPlayerComponentController(playerComponentController);
        fsmPlayer.setLifecycle(getLifecycle());

        if (fsmPlayer.isInitialized()) {
            fsmPlayer.updateSelf();
            Tools.hideSystemPlayerUi(this, true);
        } else {
            fsmPlayer.transit(Input.INITIALIZE);
        }
    }

    @Override
    public void onBackPressed() {

        playerComponentController.setDoublePlayerInterface(null);
        playerComponentController.setTubiPlaybackInterface(null);
        adPlayingMonitor = null;
        uiController.setExoPlayerView(null);
        uiController.setContentPlayer(null);
        uiController.setVpaidWebView(null);
        uiController.setAdPlayer(null);


        if (adsLoader !=null) {

            adsLoader.stop();
            adsLoader.release();
            adsLoader = null;

        }


        mediaSource = null;
        mediaModel.setMediaSource(null);


        if(mCountDownTimer!=null){

        mCountDownTimer.cancel();

        }

        if (fsmPlayer != null && fsmPlayer.getCurrentState() instanceof VpaidState && binding.vpaidWebview != null
                && binding.vpaidWebview.canGoBack()) {

            //if the last page is empty url, then, it should
            if (ingoreWebViewBackNavigation(binding.vpaidWebview)) {
                super.onBackPressed();
                return;
            }

            binding.vpaidWebview.goBack();

        } else {
            super.onBackPressed();
        }
    }

    //when the last item is "about:blank", ingore the back navigation for webview.
    private boolean ingoreWebViewBackNavigation(WebView vpaidWebView) {

        if (vpaidWebView != null) {
            WebBackForwardList mWebBackForwardList = vpaidWebView.copyBackForwardList();

            if (mWebBackForwardList == null) {
                return false;
            }

            WebHistoryItem historyItem = mWebBackForwardList.getItemAtIndex(mWebBackForwardList.getCurrentIndex() - 1);

            if (historyItem == null) {
                return false;
            }

            String historyUrl = historyItem.getUrl();

            return historyUrl != null && historyUrl.equalsIgnoreCase(Constants.EMPTY_URL);
        }

        return false;
    }

    /**
     * creating the {@link MediaSource} for the Exoplayer, recreate it everytime when new {@link SimpleExoPlayer} has been initialized
     *
     * @return
     */
    protected void createMediaSource(MediaModel videoMediaModel) {

        videoMediaModel.setMediaSource(buildMediaSource(videoMediaModel));

    }

    @Override
    public void onPrepareAds(@Nullable AdMediaModel adMediaModel) {

        if (adMediaModel !=null) {


            for (MediaModel singleMedia : adMediaModel.getListOfAds()) {
                MediaSource adMediaSource = buildMediaSource(singleMedia);
                singleMedia.setMediaSource(adMediaSource);
            }
        }

        }

    @Override
    public void onProgress(MediaModel mediaModel, long milliseconds, long durationMillis) {
        ExoPlayerLogger.v(TAG, mediaModel.getMediaName() + ": " + mediaModel.toString() + " onProgress: " + "milliseconds: " + milliseconds + " durationMillis: " + durationMillis);

        // monitor the movie progress.
        cuePointMonitor.onMovieProgress(milliseconds);

        if (milliseconds == 300) {


            getPlayerController().onCuePointReached(true);
        }


    }

    @Override
    public void onSeek(MediaModel mediaModel, long oldPositionMillis, long newPositionMillis) {
        ExoPlayerLogger.v(TAG, mediaModel.getMediaName() + ": " + mediaModel.toString() + " onSeek : " + "oldPositionMillis: " + oldPositionMillis + " newPositionMillis: " + newPositionMillis);
    }

    @Override
    public void onPlayToggle(MediaModel mediaModel, boolean playing) {
        ExoPlayerLogger.v(TAG, mediaModel.getMediaName() + ": " + mediaModel.toString() + " onPlayToggle :");
    }


    @Override
    public void onSubtitles(@Nullable MediaModel mediaModel, boolean enabled) {

        //
    }





    // Load Qualities for Movies and Series
    @Override
    public void onLoadQualities() {


        binding.frameQualities.setVisibility(View.VISIBLE);
        binding.closeQualities.setOnClickListener(v -> binding.frameQualities.setVisibility(View.GONE));



        // For Movie
        if (getPlayerController().getMediaType().equals("0")){

            movieRepository.getMovie(getPlayerController().getVideoID())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Media>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                            //

                        }

                        @Override
                        public void onNext(Media media) {


                            List<MediaStream> movieStreams = media.getVideos();


                            // Episodes RecycleView
                            binding.rvQualites.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            binding.rvQualites.setHasFixedSize(true);
                            movieQualitiesAdapter = new MovieQualitiesAdapter(EasyPlexMainPlayer.this);
                            movieQualitiesAdapter.addSeasons(movieStreams,clickDetectListner);
                            binding.rvQualites.setAdapter(movieQualitiesAdapter);


                        }

                        @Override
                        public void onError(Throwable e) {

                            //

                        }

                        @Override
                        public void onComplete() {

                            //

                        }
                    });



        } else {



            // For Series
            movieRepository.getSerieStream(getPlayerController().getCurrentEpTmdbNumber())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache()
                    .subscribe(new Observer<MediaStream>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                            //

                        }

                        @Override
                        public void onNext(MediaStream episodeStream) {

                            List<MediaStream> streamInfo = episodeStream.getMediaStreams();


                            // Episodes RecycleView
                            binding.rvQualites.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            binding.rvQualites.setHasFixedSize(true);
                            serieQualitiesAdapter = new SerieQualitiesAdapter(EasyPlexMainPlayer.this);
                            serieQualitiesAdapter.addQuality(streamInfo,  clickDetectListner);
                            binding.rvQualites.setAdapter(serieQualitiesAdapter);


                        }

                        @Override
                        public void onError(Throwable e) {

                            //

                        }

                        @Override
                        public void onComplete() {

                            //

                        }
                    });



        }


    }



    // Load Substitles for Movies & Series
    @Override
    public void onSubtitlesSelection() {

        binding.frameSubstitles.setVisibility(View.VISIBLE);
        binding.closeSubstitle.setOnClickListener(v -> binding.frameSubstitles.setVisibility(View.GONE));

        if (getPlayerController().getMediaType().equals("0")){


            movieRepository.getMovie(getPlayerController().getVideoID())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Media>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                            //

                        }

                        @Override
                        public void onNext(Media media) {

                            List<MediaSubstitle> movieSubtitles = media.getSubstitles();

                            // Episodes RecycleView
                            binding.rvSubstitles.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            binding.rvSubstitles.setHasFixedSize(true);
                            mSubstitleAdapter = new SubstitlesAdapter(EasyPlexMainPlayer.this);
                            mSubstitleAdapter.addSubtitle(movieSubtitles, clickDetectListner);
                            binding.rvSubstitles.setAdapter(mSubstitleAdapter);


                        }

                        @Override
                        public void onError(Throwable e) {

                            //

                        }

                        @Override
                        public void onComplete() {

                            //

                        }
                    });



        }else {


            movieRepository.getEpisodeSubstitle(getPlayerController().getCurrentEpTmdbNumber())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache()
                    .subscribe(new Observer<EpisodeStream>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                            //


                        }

                        @Override
                        public void onNext(EpisodeStream episodeStream) {


                            List<MediaSubstitle> movieSubtitles = episodeStream.getStreamepisode();


                            // Episodes RecycleView
                            binding.rvSubstitles.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            binding.rvSubstitles.setHasFixedSize(true);
                            mSubstitleAdapter = new SubstitlesAdapter(EasyPlexMainPlayer.this);
                            mSubstitleAdapter.addSubtitle(movieSubtitles, clickDetectListner);
                            binding.rvSubstitles.setAdapter(mSubstitleAdapter);



                        }

                        @Override
                        public void onError(Throwable e) {

                            //

                        }

                        @Override
                        public void onComplete() {

                            //

                        }
                    });




        }


    }

    @Override
    public void onMediaEnded() {

        if (playerController.playerPlaybackState.get() != Player.STATE_ENDED) {


            if (preferences.getBoolean(AUTO_PLAY,true) && !getPlayerController().getVideoID().isEmpty() && !getPlayerController().getMediaType().isEmpty()) {


                mediaModel.setMediaSource(null);

                if (binding.frameLayoutSeasons.getVisibility() == View.VISIBLE) {

                    binding.frameLayoutSeasons.setVisibility(View.GONE);

                }


                if (getPlayerController().getMediaType().equals("1")) {


                    onLoadNextSerieEpisodes();


                } else {


                    onLoadNextMovies();


                }


            }else {

                onBackPressed();

            }

        }

    }




    // Load Next Movies RecycleViews
    private void onLoadNextMovies() {


        binding.filterBtnEnded.setOnClickListener(v -> binding.spinnerMediaEnded.performClick());
        binding.framlayoutMediaEnded.setVisibility(View.VISIBLE);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(500);
        binding.framlayoutMediaEnded.startAnimation(alphaAnimation);



        binding.closeMediaEnded.setOnClickListener(v -> {


            binding.framlayoutMediaEnded.setVisibility(View.GONE);

            if(mCountDownTimer!=null) {

                mCountDownTimer.cancel();

            }

        });

        movieRepository.getMoviRandom()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(new Observer<MovieResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                        //

                    }

                    @Override
                    public void onNext(MovieResponse movieResponse) {


                        // Episodes RecycleView
                        binding.rvEpisodesEnded.setHasFixedSize(true);
                        binding.rvEpisodesEnded.setNestedScrollingEnabled(false);
                        binding.rvEpisodesEnded.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
                        binding.rvEpisodesEnded.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(getApplicationContext(), 0), true));
                        binding.rvEpisodesEnded.setItemViewCacheSize(20);
                        nextPlayMoviesAdapter = new NextPlayMoviesAdapter(clickDetectListner, EasyPlexMainPlayer.this);
                        nextPlayMoviesAdapter.addSeasons(movieResponse.getRandom());
                        binding.rvEpisodesEnded.setAdapter(nextPlayMoviesAdapter);
                        Collections.shuffle(movieResponse.getRandom());
                        binding.filterBtnEnded.setVisibility(View.GONE);

                       onLoadRandomMovie();

                    }

                    @Override
                    public void onError(Throwable e) {

                        Timber.tag(String.format("%s : %s", TAG, e.getCause()));

                    }

                    @Override
                    public void onComplete() {

                        //

                    }
                });

    }

    private void onLoadRandomMovie() {

        movieRepository.getMovie(String.valueOf(nextPlayMoviesAdapter.getFirstItem()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Media>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                        //

                    }

                    @Override
                    public void onNext(Media media) {


                        mCountDownTimer = new CountDownTimer(10000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {

                                binding.textViewVideoTimeRemaining.setText(UPNEXT + millisUntilFinished / 1000 + " s");
                                binding.ratingBar.setRating(Float.parseFloat(media.getVoteAverage()) / 2);


                                if (media.getReleaseDate() != null ) {
                                    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                                    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy");
                                    try {
                                        Date releaseDate = sdf1.parse(media.getReleaseDate());
                                        binding.textViewVideoRelease.setText(" - "+sdf2.format(releaseDate));
                                    } catch (ParseException e) {

                                        Timber.d("%s", Arrays.toString(e.getStackTrace()));

                                    }
                                } else {
                                    binding.textViewVideoRelease.setText("");
                                }


                                Glide.with(EasyPlexMainPlayer.this).load(media.getBackdropPath())
                                        .centerCrop()
                                        .placeholder(R.drawable.placehoder_episodes)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into(binding.imageViewMovieNext);


                                binding.textViewVideoNextName.setText(media.getTitle());



                                for (Genre genre : media.getGenres()) {
                                    binding.textViewVideoNextReleaseDate.setText(genre.getName());
                                }



                            }

                            @Override
                            public void onFinish() {


                                if (media.getVideos().isEmpty()) {

                                    DialogHelper.showNoStreamAvailable(EasyPlexMainPlayer.this);

                                }else {


                                    String mediaUrl = media.getVideos().get(0).getLink();
                                    String currentQuality = media.getVideos().get(0).getServer();
                                    mMediaModel = MediaModel.media(String.valueOf(media.getId()), String.valueOf(media.getTmdbId()),currentQuality,
                                            "0", media.getTitle(), mediaUrl, media.getBackdropPath(), null,
                                            null, null, null, null, null,
                                            null,null,null,null,null);
                                    playNext(mMediaModel);

                                    binding.framlayoutMediaEnded.setVisibility(View.GONE);



                                }

                            }
                        }.start();


                    }

                    @Override
                    public void onError(Throwable e) {

                        onBackPressed();


                    }

                    @Override
                    public void onComplete() {


                        //

                    }
                });





    }


    // Load Next Episode for A Serie
    private void onLoadNextSerieEpisodes() {

        binding.filterBtnEnded.setOnClickListener(v -> binding.spinnerMediaEnded.performClick());

        binding.framlayoutMediaEnded.setVisibility(View.VISIBLE);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(500);
        binding.framlayoutMediaEnded.startAnimation(alphaAnimation);

        binding.closeMediaEnded.setOnClickListener(v -> {

            binding.framlayoutMediaEnded.setVisibility(View.GONE);

            if(mCountDownTimer!=null){

                mCountDownTimer.cancel();

            }

        });


       onLoadSerieEpisodes();
       onLoadNextEpisodeWithTimer();


    }



    // Load Next Episode Info  for A Serie With A CountDownTimer
    private void onLoadNextEpisodeWithTimer() {

        movieRepository.getSerieSeasons(getPlayerController().nextSeaonsID())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(new Observer<MovieResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                        //

                    }

                    @Override
                    public void onNext(MovieResponse movieResponse) {

                        if (getPlayerController().getCurrentEpisodePosition() != movieResponse.getEpisodes().size() - 1) {

                            for (int i=0; i<movieResponse.getEpisodes().size(); i++) {

                                if (getPlayerController().getEpName().equals(movieResponse.getEpisodes().get(i).getName())) {

                                    int position = i+1;

                                    mCountDownTimer = new CountDownTimer(10000, 1000) {
                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void onTick(long millisUntilFinished) {

                                            binding.textViewVideoTimeRemaining.setText(UPNEXT + millisUntilFinished / 1000 + " s");

                                            binding.ratingBar.setRating(Float.parseFloat(movieResponse.getEpisodes().get(position).getVoteAverage()) / 2);

                                            Glide.with(EasyPlexMainPlayer.this).load(movieResponse.getEpisodes().get(position).getStillPath())
                                                    .centerCrop()
                                                    .placeholder(R.drawable.placehoder_episodes)
                                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                    .into(binding.imageViewMovieNext);

                                            binding.textViewVideoNextName.setText(EP + movieResponse.getEpisodes().get(position).getEpisodeNumber() + " : " + movieResponse.getEpisodes().get(position).getName());
                                            binding.textViewVideoNextReleaseDate.setText(getPlayerController().getCurrentSeason());

                                        }

                                        @Override
                                        public void onFinish() {


                                            onCheckEpisodeHasStream(movieResponse,position);


                                        }


                                    }.start();

                                }


                            }


                        }



                    }

                    @Override
                    public void onError(Throwable e) {

                        onBackPressed();

                    }

                    @Override
                    public void onComplete() {

                        //

                    }
                });


    }



    // Check if a link is exit for the next Episode before playing
    private void onCheckEpisodeHasStream(MovieResponse movieResponse,int position) {

        if (movieResponse.getEpisodes().get(position).getVideos() != null && !movieResponse.getEpisodes().get(position).getVideos().isEmpty()) {

            String mediaCover = movieResponse.getEpisodes().get(position).getStillPath();
            String type = "1";
            String currentquality = movieResponse.getEpisodes().get(position).getVideos().get(0).getServer();
            String name = S0 + getPlayerController().getSeaonNumber() + E + movieResponse.getEpisodes().get(position).getEpisodeNumber()

                    + " : " + movieResponse.getEpisodes().get(position).getName();

            String videourl = movieResponse.getEpisodes().get(position).getVideos().get(0).getLink();

            mMediaModel = MediaModel.media(getPlayerController().getVideoID(), null, currentquality, type, name, videourl, mediaCover, null,
                    null, null, null, null, movieResponse.getEpisodes().get(position).getName(),
                    null, null, null,null,getPlayerController().getCurrentTvShowName());
            playNext(mMediaModel);

            binding.framlayoutMediaEnded.setVisibility(View.GONE);

        } else {

            DialogHelper.showNoStreamAvailable(EasyPlexMainPlayer.this);


        }

    }


    // Return Seasons With Episodes for A Serie
    private void onLoadSerieEpisodes() {

        movieRepository.getSerie(getPlayerController().getVideoID())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(new Observer<Media>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                        //

                    }

                    @Override
                    public void onNext(Media series) {

                        List<Season> seasons = series.getSeasons() ;

                        for(Iterator<Season> iterator = seasons.iterator(); iterator.hasNext(); ) {
                            if(iterator.next().getName().equals(SPECIALS))
                                iterator.remove();
                        }


                        binding.spinnerMediaEnded.setItem(seasons);
                        binding.spinnerMediaEnded.setSelection(0);
                        binding.spinnerMediaEnded.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {


                                Tools.hideSystemPlayerUi(EasyPlexMainPlayer.this, true);

                                Season season = (Season) adapterView.getItemAtPosition(position);
                                String episodeId = String.valueOf(season.getId());
                                String currentSeason = season.getName();
                                String serieId = String.valueOf(series.getTmdbId());
                                String seasonNumber = season.getSeasonNumber();


                                binding.selectedSeasonEnded.setText(season.getName());

                                // Episodes RecycleView
                                binding.rvEpisodesEnded.setHasFixedSize(true);
                                binding.rvEpisodesEnded.setNestedScrollingEnabled(false);
                                binding.rvEpisodesEnded.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
                                binding.rvEpisodesEnded.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(getApplicationContext(), 0), true));
                                binding.rvEpisodesEnded.setItemViewCacheSize(20);
                                nextPlaySeriesAdapter = new NextPlaySeriesAdapter(serieId,seasonNumber,episodeId,currentSeason, clickDetectListner, EasyPlexMainPlayer.this);
                                nextPlaySeriesAdapter.addNextPlay(season.getEpisodes());
                                binding.rvEpisodesEnded.setAdapter(nextPlaySeriesAdapter);



                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                                Tools.hideSystemPlayerUi(EasyPlexMainPlayer.this, true);

                            }
                        });


                    }

                    @Override
                    public void onError(Throwable e) {

                        Timber.tag(String.format("%s : %s", TAG, e.getCause()));

                    }

                    @Override
                    public void onComplete() {

                        //

                    }
                });

    }

    @Override
    public void onLoadEpisodes() {

        binding.filterBtn.setOnClickListener(v -> binding.planetsSpinner.performClick());

        binding.frameLayoutSeasons.setVisibility(View.VISIBLE);
        binding.closeEpisode.setOnClickListener(v -> binding.frameLayoutSeasons.setVisibility(View.GONE));


        binding.viewSerieControllerTitle.setText(getPlayerController().getCurrentTvShowName());


        if (getPlayerController().getMediaType().equals("1")) {


            movieRepository.getSerie(getPlayerController().getVideoID())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache()
                    .subscribe(new Observer<Media>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                            //

                        }

                        @Override
                        public void onNext(Media series) {

                            List<Season> seasons = series.getSeasons() ;

                            for(Iterator<Season> iterator = seasons.iterator(); iterator.hasNext(); ) {
                                if(iterator.next().getName().equals(SPECIALS))
                                    iterator.remove();
                            }


                            binding.planetsSpinner.setItem(seasons);
                            binding.planetsSpinner.setSelection(0);
                            binding.planetsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {


                                    Tools.hideSystemPlayerUi(EasyPlexMainPlayer.this, true);

                                    Season season = (Season) adapterView.getItemAtPosition(position);
                                    String episodeId = String.valueOf(season.getId());
                                    String currentSeason = season.getName();
                                    String serieId = String.valueOf(series.getTmdbId());
                                    String seasonNumber = season.getSeasonNumber();


                                    binding.currentSelectedSeasons.setText(season.getName());

                                    // Episodes RecycleView
                                    binding.recyclerViewEpisodes.setHasFixedSize(true);
                                    binding.recyclerViewEpisodes.setNestedScrollingEnabled(false);
                                    binding.recyclerViewEpisodes.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
                                    binding.recyclerViewEpisodes.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(getApplicationContext(), 0), true));
                                    binding.recyclerViewEpisodes.setItemViewCacheSize(20);
                                    mEPAdapter = new EpisodesPlayerAdapter(serieId,seasonNumber,episodeId,currentSeason, clickDetectListner, EasyPlexMainPlayer.this);
                                    mEPAdapter.addSeasons(season.getEpisodes());
                                    binding.recyclerViewEpisodes.setAdapter(mEPAdapter);



                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {


                                    //

                                }
                            });


                        }

                        @Override
                        public void onError(Throwable e) {

                            Timber.tag(""+e.getCause());

                        }

                        @Override
                        public void onComplete() {

                            //

                        }
                    });

        }else {


           onLoadAnimeEpisodes();


        }

    }

    @Override
    public void onLoadNextEpisode() {

        movieRepository.getSerieSeasons(getPlayerController().nextSeaonsID())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(new Observer<MovieResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                        //

                    }

                    @Override
                    public void onNext(MovieResponse movieResponse) {

                        if (getPlayerController().getCurrentEpisodePosition() == movieResponse.getEpisodes().size() - 1) {

                            onBackPressed();

                        }


                        for (int i = 0; i < movieResponse.getEpisodes().size();

                             i++) {

                            if (getPlayerController().getEpName().equals(movieResponse.getEpisodes().get(i).getName())) {

                                int position = i+1;

                                if (movieResponse.getEpisodes().get(position).getVideos() != null && !movieResponse.getEpisodes().get(position).getVideos().isEmpty()) {

                                    String mediaCover = movieResponse.getEpisodes().get(position).getStillPath();
                                    String type = "1";
                                    String currentquality = movieResponse.getEpisodes().get(position).getVideos().get(0).getServer();
                                    String name = S0 + getPlayerController().getSeaonNumber() + E + movieResponse.getEpisodes().get(position).getEpisodeNumber()

                                            + " : " + movieResponse.getEpisodes().get(position).getName();

                                    String videourl = movieResponse.getEpisodes().get(position).getVideos().get(0).getLink();

                                    mMediaModel = MediaModel.media(getPlayerController().getVideoID(), null, currentquality, type, name, videourl, mediaCover, null,
                                            null, null, null, null, movieResponse.getEpisodes().get(position).getName(),
                                            null, null, null,null,getPlayerController().getCurrentTvShowName());
                                    playNext(mMediaModel);

                                    binding.framlayoutMediaEnded.setVisibility(View.GONE);

                                } else {

                                    DialogHelper.showNoStreamAvailable(EasyPlexMainPlayer.this);


                                }
                            }
                        }


                    }

                    @Override
                    public void onError(Throwable e) {

                        //

                    }

                    @Override
                    public void onComplete() {

                        //

                    }
                });


    }


    // Load Episodes for Anime
    private void onLoadAnimeEpisodes() {

        animeRepository.getAnimeDetails(getPlayerController().getVideoID())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(new Observer<Media>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                        //

                    }

                    @Override
                    public void onNext(Media anime) {

                        List<Season> seasons = anime.getSeasons() ;

                        for(Iterator<Season> iterator = seasons.iterator(); iterator.hasNext(); ) {
                            if(iterator.next().getName().equals(SPECIALS))
                                iterator.remove();
                        }


                        binding.planetsSpinner.setItem(seasons);
                        binding.planetsSpinner.setSelection(0);
                        binding.planetsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {


                                Tools.hideSystemPlayerUi(EasyPlexMainPlayer.this, true);

                                Season season = (Season) adapterView.getItemAtPosition(position);
                                String episodeId = String.valueOf(season.getId());
                                String currentSeason = season.getName();
                                String serieId = String.valueOf(anime.getTmdbId());
                                String seasonNumber = season.getSeasonNumber();


                                binding.currentSelectedSeasons.setText(season.getName());

                                // Episodes RecycleView
                                binding.recyclerViewEpisodes.setHasFixedSize(true);
                                binding.recyclerViewEpisodes.setNestedScrollingEnabled(false);
                                binding.recyclerViewEpisodes.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
                                binding.recyclerViewEpisodes.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(getApplicationContext(), 0), true));
                                binding.recyclerViewEpisodes.setItemViewCacheSize(20);
                                mEPAdapter = new EpisodesPlayerAdapter(serieId,seasonNumber,episodeId,currentSeason, clickDetectListner, EasyPlexMainPlayer.this);
                                mEPAdapter.addSeasons(season.getEpisodes());
                                binding.recyclerViewEpisodes.setAdapter(mEPAdapter);



                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                                Tools.hideSystemPlayerUi(EasyPlexMainPlayer.this, true);

                            }
                        });


                    }

                    @Override
                    public void onError(Throwable e) {

                        Timber.tag(""+e.getCause());

                    }

                    @Override
                    public void onComplete() {

                        //

                    }
                });
    }





    // Return List of Streaming in RecycleViews
    @Override
    public void onLoadSteaming() {
        binding.frameLayoutStreaming.setVisibility(View.VISIBLE);
        binding.closeStreaming.setOnClickListener(v -> binding.frameLayoutStreaming.setVisibility(View.GONE));


        movieRepository.getLatestStreaming()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MovieResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                        //

                    }

                    @Override
                    public void onNext(MovieResponse movieResponse) {

                        List<Streaming> streaming = movieResponse.getStreaming();

                        binding.recyclerViewStreaming.setHasFixedSize(true);
                        binding.recyclerViewStreaming.setNestedScrollingEnabled(false);
                        binding.recyclerViewStreaming.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
                        binding.recyclerViewStreaming.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(getApplicationContext(), 0), true));
                        binding.recyclerViewStreaming.setItemViewCacheSize(20);
                        mStreamingAdapter = new StreamingPlayerAdapter(EasyPlexMainPlayer.this);
                        mStreamingAdapter.addStreaming(streaming,clickDetectListner);
                        binding.recyclerViewStreaming.setAdapter(mStreamingAdapter);


                    }

                    @Override
                    public void onError(Throwable e) {

                        //

                    }

                    @Override
                    public void onComplete() {

                        //

                    }
                });

    }


    @Override
    public void onLoadSAds() {

        //

    }





    // Update CueIndicator when the CuePoint Received
    @Override
    public void onCuePointReceived(long[] cuePoints) {

        binding.cuepointIndictor.setText(printCuePoints(cuePoints));
    }

    @Override
    public boolean isPremuim() {
        return false;
    }

    private String printCuePoints(long[] cuePoints) {
        if (cuePoints == null) {
            return null;
        }

        return "";
    }



    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

        // On CuePoint Change

    }



    // Play Next Media (Reset and update State for the Player)
    @Override
    public void playNext(MediaModel nextVideo) {
        createMediaSource(nextVideo);
        fsmPlayer.setMovieMedia(nextVideo);
        fsmPlayer.restart();

    }


    // Update Media (Without Resetting the position )
    @Override
    public void update(MediaModel update) {
        createMediaSource(update);
        fsmPlayer.setMovieMedia(update);
        fsmPlayer.update();

    }

    @Override
    public void backState(MediaModel backstate) {
        createMediaSource(backstate);
        fsmPlayer.setMovieMedia(backstate);
        fsmPlayer.backfromApp();

    }


    @Override
    public void onStop() {
        super.onStop();
        super.onDestroy();
    }



    // Detect if a user has Selected a substitle
    @Override
    public void onSubstitleClicked(boolean clicked) {

        if (clicked) {
            binding.frameSubstitles.setVisibility(View.GONE);

        }

    }


    // Detect if a user has Selected a Quality
    @Override
    public void onQualityClicked(boolean clicked) {


        if (clicked) {
            binding.frameQualities.setVisibility(View.GONE);

        }


    }



    // Detect if a user has Selected a Stream
    @Override
    public void onStreamingclicked(boolean clicked) {

        if (clicked) {
            binding.frameLayoutStreaming.setVisibility(View.GONE);
        }

    }





    // Detect if a user has Clicked an episode or movie when the media is ended
    @Override
    public void onNextMediaClicked(boolean clicked) {


        if (clicked) {
            binding.framlayoutMediaEnded.setVisibility(View.GONE);

            if(mCountDownTimer!=null){

                mCountDownTimer.cancel();

            }

        }


    }

    @Override
    public void onEpisodeClicked(boolean clicked) {

        if (clicked) {
            binding.frameLayoutSeasons.setVisibility(View.GONE);
        }

    }

}
