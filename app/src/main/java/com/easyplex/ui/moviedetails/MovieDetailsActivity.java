package com.easyplex.ui.moviedetails;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.easyplex.R;
import com.easyplex.data.local.entity.Media;
import com.easyplex.data.model.genres.Genre;
import com.easyplex.data.model.media.MediaModel;
import com.easyplex.data.model.stream.MediaStream;
import com.easyplex.databinding.ItemMovieDetailBinding;
import com.easyplex.ui.manager.AuthManager;
import com.easyplex.ui.manager.SettingsManager;
import com.easyplex.ui.moviedetails.adapters.CastAdapter;
import com.easyplex.ui.moviedetails.adapters.RelatedsAdapter;
import com.easyplex.ui.player.activities.EasyPlexMainPlayer;
import com.easyplex.ui.player.activities.EasyPlexPlayerActivity;
import com.easyplex.util.Constants;
import com.easyplex.util.DialogHelper;
import com.easyplex.util.NetworkUtils;
import com.easyplex.util.SpacingItemDecoration;
import com.easyplex.util.Tools;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import dagger.android.AndroidInjection;
import timber.log.Timber;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.easyplex.util.Constants.ARG_MOVIE;


/**
 * EasyPlex - Movies - Live Streaming - TV Series, Anime
 *
 * @author @Y0bEX
 * @package  EasyPlex - Movies - Live Streaming - TV Series, Anime
 * @copyright Copyright (c) 2020 Y0bEX,
 * @license http://codecanyon.net/wiki/support/legal-terms/licensing-terms/
 * @profile https://codecanyon.net/user/yobex
 * @link yobexd@gmail.com
 * @skype yobexd@gmail.com
 **/


public class MovieDetailsActivity extends AppCompatActivity {



    ItemMovieDetailBinding binding;

    AdView mAdView;

    @Inject ViewModelProvider.Factory viewModelFactory;
    private MovieDetailViewModel movieDetailViewModel;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    SettingsManager settingsManager;

    @Inject
    AuthManager authManager;

    CastAdapter mCastAdapter;
    RelatedsAdapter mRelatedsAdapter;
    private boolean mMovie;
    private boolean mRelatedMovie;
    private boolean mCast;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AndroidInjection.inject(this);

        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.item_movie_detail);

        mMovie = false;
        mCast = false;
        mRelatedMovie = false;
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.itemDetailContainer.setVisibility(View.GONE);
        binding.mPlay.setVisibility(View.GONE);

        Intent intent = getIntent();
        Media media = intent.getParcelableExtra(ARG_MOVIE);

        Tools.hideSystemPlayerUi(this,true,0);
        Tools.setSystemBarTransparent(this);


        // ViewModel to cache, retrieve data for MovieDetailsActivity

        movieDetailViewModel = new ViewModelProvider(this, viewModelFactory).get(MovieDetailViewModel.class);

        if (media != null){

            movieDetailViewModel.getMovieDetails(media.getTmdbId());
            movieDetailViewModel.movieDetailMutableLiveData.observe(this, movieDetail -> {
                onLoadImage(movieDetail.getPosterPath());
                onLoadTitle(movieDetail.getTitle());
                onLoadDate(movieDetail.getReleaseDate());
                onLoadAppBar(movieDetail.getTitle());
                onLoadSynopsis(movieDetail.getOverview());
                onLoadGenres(movieDetail.getGenres());
                onLoadBackButton();
                onLoadRelatedsMovies(Integer.parseInt(movieDetail.getId()));
                onLoadCast(Integer.parseInt(movieDetail.getTmdbId()));
                onLoadRating(movieDetail.getVoteAverage());
                onLoadBanner();

                binding.report.setOnClickListener(v -> onLoadReport(movieDetail.getTitle(),movieDetail.getPosterPath()));
                binding.ButtonPlayTrailer.setOnClickListener(v -> onLoadTrailer(movieDetail.getPreviewPath(),movieDetail.getTitle(),movieDetail.getBackdropPath()));
                binding.shareIcon.setOnClickListener(v -> onLoadShare(movieDetail.getTitle(),Integer.parseInt(movieDetail.getTmdbId())));

                binding.favoriteIcon.setOnClickListener(view -> {
                    binding.favoriteIcon.toggleWishlisted();
                    onFavoriteClick(movieDetail);
                });

                movieDetailViewModel.isFavorite(Integer.parseInt(movieDetail.getTmdbId())).observe(this, favMovieDetail1 -> {
                    binding.favoriteIcon.setActivated(favMovieDetail1 != null);
                    binding.favoriteIcon.setVisibility(View.VISIBLE);
                });



                binding.PlayButtonIcon.setOnClickListener(v -> {

                    if (sharedPreferences.getBoolean(Constants.WIFI_CHECK, false) &&
                            NetworkUtils.isWifiConnected(this))
                        DialogHelper.showWifiWarning(MovieDetailsActivity.this);
                    else if (media.getPremuim() == 1 && authManager.getUserInfo().getPremuim() != 1) {


                        DialogHelper.showPremuimWarning(this);

                    } else {

                        onLoadStream(movieDetail.getVideos(), movieDetail.getTitle(), movieDetail.getBackdropPath(), Integer.parseInt(movieDetail.getTmdbId()));

                    }

                });



                if (media.getSubstitles() !=null) {

                    binding.subsEnabled.setVisibility(View.VISIBLE);

                }else {

                    binding.subsEnabled.setVisibility(View.GONE);
                }



                mMovie = true;
                checkAllDataLoaded();


            });


        }else {


            DialogHelper.showNoStreamAvailable(this);

        }

    }

    private void onLoadBanner() {

        if (settingsManager.getSettings().getAdUnitIdBanner() !=null) {

            AdSize adSize = getAdSize();
            // Create an ad request.
            mAdView = new AdView(this);
            mAdView.setAdUnitId(settingsManager.getSettings().getAdUnitIdBanner());
            binding.adViewContainer.removeAllViews();
            binding.adViewContainer.addView(mAdView);
            mAdView.setAdSize(adSize);

            AdRequest adRequest = new AdRequest.Builder().build();

            // Start loading the ad in the background.
            mAdView.loadAd(adRequest);



        }else {

            AdSize adSize = getAdSize();
            // Create an ad request.
            mAdView = new AdView(this);
            mAdView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
            binding.adViewContainer.removeAllViews();
            binding.adViewContainer.addView(mAdView);
            mAdView.setAdSize(adSize);

            AdRequest adRequest = new AdRequest.Builder().build();
            // Start loading the ad in the background.
            mAdView.loadAd(adRequest);

        }
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("B3EEABB8EE11C2BE770B684D95219ECB")).build();
        MobileAds.setRequestConfiguration(configuration);


    }


    // Determine the screen width (less decorations) to use for the ad width.
    private AdSize getAdSize() {

        Display display = this.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getRealMetrics(outMetrics);

        float density = outMetrics.density;

        float adWidthPixels = binding.adViewContainer.getWidth();

        // If the ad hasn't been laid out, default to the full screen width.
        if (adWidthPixels == 0) {
            adWidthPixels = outMetrics.widthPixels;
        }

        int adWidth = (int) (adWidthPixels / density);

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);


    }


    // Load the Movie Rating
    private void onLoadRating(String rating) {

        binding.ratingBar.setRating(Float.parseFloat(rating) / 2);

    }



    // Animate the AppBar
    private void onLoadAppBar(final String title) {



        binding.appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (appBarLayout.getTotalScrollRange() + verticalOffset == 0) {
                if (title != null){


                    binding.collapsingToolbar.setTitle(title);
                    binding.collapsingToolbar.setCollapsedTitleTextColor(Color.parseColor("#FFFFFF"));
                    binding.collapsingToolbar.setContentScrimColor(Color.parseColor("#E6070707"));

                }

                else
                    binding.collapsingToolbar.setTitle("");
                binding.toolbar.setVisibility(View.VISIBLE);


            } else {
                binding.collapsingToolbar.setTitle("");
                binding.toolbar.setVisibility(View.INVISIBLE);
            }
        });



    }



    // Send report for this Movie
    private void onLoadReport(String title,String posterpath) {


        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_report);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WRAP_CONTENT;
        lp.height = WRAP_CONTENT;

        EditText editTextMessage = dialog.findViewById(R.id.et_post);
        TextView reportMovieName = dialog.findViewById(R.id.movietitle);
        ImageView imageView = dialog.findViewById(R.id.item_movie_image);


        reportMovieName.setText(title);


        Tools.onLoadMediaCover(this,imageView,posterpath);


        dialog.findViewById(R.id.bt_cancel).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.bt_submit).setOnClickListener(v -> {


            editTextMessage.getText().toString().trim();


            if (editTextMessage.getText() !=null) {

                movieDetailViewModel.sendReport(title,editTextMessage.getText().toString());
                movieDetailViewModel.reportMutableLiveData.observe(MovieDetailsActivity.this, report -> {


                    if (report !=null) {


                        dialog.dismiss();


                        Toast.makeText(this, "Your report has been submitted successfully", Toast.LENGTH_SHORT).show();

                    }


                });

            }


        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);






    }


    // Handle Favorite Button Click to add or remove the from MyList
    public void onFavoriteClick(Media mediaDetail) {
        if (binding.favoriteIcon.isActivated()) {
            Toast.makeText(getApplicationContext(), "Added: " + mediaDetail.getTitle(),
                    Toast.LENGTH_SHORT).show();
            movieDetailViewModel.addFavorite(mediaDetail);
        } else {
            Toast.makeText(getApplicationContext(), "Removed: " + mediaDetail.getTitle(),
                    Toast.LENGTH_SHORT).show();
            movieDetailViewModel.removeFavorite(mediaDetail);
        }
    }



    // Get Movie Cast
    private void onLoadCast(int imdb) {

        movieDetailViewModel.getMovieCast(imdb);
        movieDetailViewModel.movieCreditsMutableLiveData.observe(this, credits -> {
            mCastAdapter = new CastAdapter(settingsManager);
            mCastAdapter.addCasts(credits.getCasts());

            // Starring RecycleView
            binding.recyclerViewCastMovieDetail.setAdapter(mCastAdapter);
            binding.recyclerViewCastMovieDetail.setHasFixedSize(true);
            binding.recyclerViewCastMovieDetail.setNestedScrollingEnabled(false);
            binding.recyclerViewCastMovieDetail.setLayoutManager(new LinearLayoutManager(MovieDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
            binding.recyclerViewCastMovieDetail.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(this, 0), true));

            mCast = true;
            checkAllDataLoaded();


        });
    }


    // Load Relateds Movies
    private void onLoadRelatedsMovies(int id) {

        movieDetailViewModel.getRelatedsMovies(id);
        movieDetailViewModel.movieRelatedsMutableLiveData.observe(this, relateds -> {
            mRelatedsAdapter = new RelatedsAdapter();
            mRelatedsAdapter.addToContent(relateds.getRelateds());

            // Relateds Movies RecycleView

            binding.rvMylike.setAdapter(mRelatedsAdapter);
            binding.rvMylike.setHasFixedSize(true);
            binding.rvMylike.setNestedScrollingEnabled(false);
            binding.rvMylike.setLayoutManager(new LinearLayoutManager(MovieDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
            binding.rvMylike.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(this, 0), true));


            mRelatedMovie = true;
            checkAllDataLoaded();


        });
    }






    // Load Stream if Added
    private void onLoadStream(List<MediaStream> videos, String movieTitle, String backdropPath, int moveId) {



        if (videos !=null &&  !videos.isEmpty() ) {


            Intent intent = new Intent(this, EasyPlexMainPlayer.class);
            intent.putExtra(EasyPlexPlayerActivity.TUBI_MEDIA_KEY, MediaModel.media(String.valueOf(moveId),null,videos.get(0).getServer(),"0", movieTitle,
                    videos.get(0).getLink(), backdropPath, null, null
                    , null,null,null,null,null,null,null,null,null));
            startActivity(intent);
            Animatoo.animateFade(this);


        } else {


            DialogHelper.showNoStreamAvailable(this);




        }


    }


    // Share the Movie
    private void onLoadShare(String title, int imdb) {

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_msg)+ " " + title + " For more information please check"+("https://www.imdb.com/title/tt" + imdb));
        sendIntent.setType("text/plain");
        startActivity(sendIntent);

    }

    private void onLoadBackButton() {

        binding.backbutton.setOnClickListener(v -> {
            onBackPressed();
            Animatoo.animateSplit(MovieDetailsActivity.this);

        });
    }


    // Load The Trailer
    private void onLoadTrailer(String previewPath,String title,String backdrop) {


        if (sharedPreferences.getBoolean(Constants.WIFI_CHECK, false) &&
                NetworkUtils.isWifiConnected(this)) {

            DialogHelper.showWifiWarning(MovieDetailsActivity.this);

        }else {

            Tools.startTrailer(this,previewPath,title,backdrop);

        }


    }


    // Display Movie Poster
    private void onLoadImage(String imageURL){

        Tools.onLoadMediaCover(getApplicationContext(),binding.imageMoviePoster,imageURL);

    }

    // Display Movie Title
    private void onLoadTitle(String title){

        binding.textMovieTitle.setText(title);
    }


    // Display Movie Release Date
    private void onLoadDate(String date){

        if (date != null && !date.trim().isEmpty()) {
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy");
            try {
                Date releaseDate = sdf1.parse(date);
                binding.textMovieRelease.setText(sdf2.format(releaseDate));
            } catch (ParseException e) {

                Timber.d("%s", Arrays.toString(e.getStackTrace()));

            }
        } else {
            binding.textMovieRelease.setText("");
        }
    }


    // Display Movie Synopsis or Overview
    private void onLoadSynopsis(String synopsis){
        binding.textOverviewLabel.setText(synopsis);
    }



    // Movie Genres
    private void onLoadGenres(List<Genre> genresList) {
        String genres = "";
        if (genresList != null) {
            for (int i = 0; i < genresList.size(); i++) {
                if (genresList.get(i) == null) continue;
                if (i == genresList.size() - 1) {
                    genres = genres.concat(genresList.get(i).getName());
                } else {
                    genres = genres.concat(genresList.get(i).getName() + ", ");
                }
            }
        }
        binding.mgenres.setText(genres);
    }


    private void checkAllDataLoaded() {
        if (mMovie && mCast && mRelatedMovie) {
            binding.progressBar.setVisibility(View.GONE);
            binding.itemDetailContainer.setVisibility(View.VISIBLE);
            binding.mPlay.setVisibility(View.VISIBLE);

        }
    }



    // Destroy the views on exit
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.get(this).clearMemory();
        binding.adViewContainer.removeAllViews();

        if (mAdView !=null) {

            mAdView.removeAllViews();
            mAdView.destroy();

        }
        binding = null;
    }

    @Override
    public void onBackPressed() {
        finishAfterTransition();
    }



    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            Tools.hideSystemPlayerUi(this,true,0);
        }
    }



}