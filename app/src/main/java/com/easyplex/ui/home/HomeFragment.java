package com.easyplex.ui.home;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import com.easyplex.data.repository.MovieRepository;
import com.easyplex.ui.login.LoginViewModel;
import com.easyplex.ui.manager.AdsManager;
import com.easyplex.ui.manager.AuthManager;
import com.easyplex.R;
import com.easyplex.ui.manager.SettingsManager;
import com.easyplex.ui.manager.StatusManager;
import com.easyplex.ui.manager.TokenManager;
import com.easyplex.databinding.FragmentHomeBinding;
import com.easyplex.di.Injectable;
import com.easyplex.ui.animes.AnimesAdapter;
import com.easyplex.ui.home.adapters.FeaturedAdapter;
import com.easyplex.ui.home.adapters.LatestAdapter;
import com.easyplex.ui.home.adapters.LatestseriesAdapter;
import com.easyplex.ui.home.adapters.MainAdapter;
import com.easyplex.ui.home.adapters.NewThisWeekAdapter;
import com.easyplex.ui.home.adapters.PopularMoviesAdapter;
import com.easyplex.ui.home.adapters.PopularSeriesAdapter;
import com.easyplex.ui.home.adapters.TrendingAdapter;
import com.easyplex.ui.splash.SplashActivity;
import com.easyplex.util.SpacingItemDecoration;
import com.easyplex.util.Tools;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import javax.inject.Inject;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;


public class HomeFragment extends Fragment implements Injectable {


    FragmentHomeBinding binding;

    private FeaturedAdapter mFeaturedAdapter;
    private MainAdapter mMainAdapter;
    private TrendingAdapter mTrendingAdapter;
    private LatestAdapter mLatestAdapter;
    private PopularSeriesAdapter popularSeriesAdapter;
    private LatestseriesAdapter mSeriesRecentsAdapter;
    private AnimesAdapter animesAdapter;
    private NewThisWeekAdapter mNewThisWeekAdapter;
    private PopularMoviesAdapter mPopularAdapter;
    public static final String ARG_MOVIE = "movie";


    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    SharedPreferences preferences;

    @Inject
    MovieRepository movieRepository;

    private HomeViewModel homeViewModel;

    private LoginViewModel loginViewModel;


    @Inject
    SettingsManager settingsManager;

    @Inject
    TokenManager tokenManager;

    @Inject
    AdsManager adsManager;


    @Inject
    AuthManager authManager;

    @Inject
    StatusManager statusManager;


    @Inject
    SharedPreferences.Editor editor;


    @Inject
    SharedPreferences sharedPreferences;


    PublisherAdView adView;


    private boolean mFeaturedLoaded;
    private boolean mRecommendedLoaded;
    private boolean mTrendingLoaded;
    private  boolean mLatestLoaded;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);


        setHasOptionsMenu(true);

        onLoadToolbar();
        onLoadNestedToolbar();

        binding.scrollView.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);

        mMainAdapter = new MainAdapter();
        mFeaturedAdapter = new FeaturedAdapter();
        mTrendingAdapter = new TrendingAdapter();
        mLatestAdapter = new LatestAdapter();
        popularSeriesAdapter = new PopularSeriesAdapter();
        mSeriesRecentsAdapter = new LatestseriesAdapter();
        animesAdapter = new AnimesAdapter();
        mNewThisWeekAdapter = new NewThisWeekAdapter();
        mPopularAdapter = new PopularMoviesAdapter();

        mFeaturedLoaded = false;
        mRecommendedLoaded = false;
        mTrendingLoaded = false;
        mLatestLoaded = false;

        // HomeMovieViewModel to cache, retrieve data for HomeFragment
        homeViewModel = new ViewModelProvider(this, viewModelFactory).get(HomeViewModel.class);

        // LoginViewModel to cache, retrieve data for Authenticated User
        loginViewModel = new ViewModelProvider(this, viewModelFactory).get(LoginViewModel.class);



        onCheckAuthenticatedUser();


        homeViewModel.getHomeItems();


        if(tokenManager.getToken() == null  || tokenManager.getToken().getRefresh() == null || tokenManager.getToken().getAccessToken() == null){

            startActivity(new Intent(getContext(), SplashActivity.class));
            getActivity().finish();

        }


        if (Tools.checkIfHasNetwork(requireContext())) {

            onLoadHomeContent();

        }

        return binding.getRoot();

    }

    private void onCheckAuthenticatedUser() {

        loginViewModel.getAuthDetails();
        loginViewModel.authDetailMutableLiveData.observe(getViewLifecycleOwner(), auth -> {


            if (auth !=null && auth.getName() !=null) {

                authManager.saveSettings(auth);

            } else {

                Toast.makeText(getContext(), "Unable to get Auth Informations", Toast.LENGTH_SHORT).show();

            }

        });
    }


    private void onLoadHomeContent() {



        if (settingsManager.getSettings().getPurchaseKey() ==null) {

            final Dialog dialog = new Dialog(getContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_status_warning);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WRAP_CONTENT;
            lp.height = WRAP_CONTENT;

            dialog.findViewById(R.id.bt_getcode).setOnClickListener(v -> {

                getActivity().finishAffinity();
                dialog.dismiss();

            });

            dialog.findViewById(R.id.bt_close).setOnClickListener(v ->    getActivity().finishAffinity());

            dialog.show();
            dialog.getWindow().setAttributes(lp);

        }else {


            // Return Recommended Movies RecyclerView
            onLoadFeaturedMovies();

            // Return Featured Movies RecyclerView
            onLoadRecommendedMovies();

            // Return Trending Movies RecyclerView
            onLoadTrendingMovies();

            // Return Latest Movies RecyclerView
            onLoadLatestMovies();

            // Return Popular Series RecyclerView
            onLoadPopularSeries();

            // Return Latest Series RecyclerView
            onLoadLatestSeries();


            // Return Latest Animes
            onLoadLatestAnimes();


            // Return New Added Movies This Week
            onLoadNewThisWeek();


            // Return Popular Movies
            onLoadPopularMovies();


        }


    }





    // Load  AppBar
    private void onLoadNestedToolbar() {

        Tools.loadAppBar(binding.scrollView,binding.toolbar);

    }



    // Determine the screen width (less decorations) to use for the ad width.
    private AdSize getAdSize() {


        // Determine the screen width (less decorations) to use for the ad width.
        Display display = requireActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = outMetrics.density;

        float adWidthPixels = binding.adViewContainer.getWidth();

        // If the ad hasn't been laid out, default to the full screen width.
        if (adWidthPixels == 0) {
            adWidthPixels = outMetrics.widthPixels;
        }

        int adWidth = (int) (adWidthPixels / density);

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(getContext(), adWidth);


    }

    private void onLoadToolbar() {

        Tools.loadToolbar(((AppCompatActivity)requireActivity()),binding.toolbar,binding.appbar);
        Tools.loadMiniLogo(requireActivity(),binding.logoImageTop);


    }



    // Display Featured Movies Details
    private void onLoadFeaturedMovies() {

        binding.rvFeatured.setHasFixedSize(true);
        binding.rvFeatured.setNestedScrollingEnabled(false);
        binding.rvFeatured.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvFeatured.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(requireActivity(), 0), true));
        binding.rvFeatured.setItemAnimator(new DefaultItemAnimator());
        binding.rvFeatured.setAdapter(mFeaturedAdapter);
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(binding.rvFeatured);
        binding.indicator.attachToRecyclerView(binding.rvFeatured, pagerSnapHelper);
        binding.indicator.createIndicators(mFeaturedAdapter.getItemCount(),0);
        mFeaturedAdapter.registerAdapterDataObserver(binding.indicator.getAdapterDataObserver());

        homeViewModel.movieFeaturedMutableLiveData.observe(getViewLifecycleOwner(), featured -> {
            mFeaturedAdapter.addFeatured(featured.getFeatured(),requireActivity(),preferences,movieRepository,authManager);

            mFeaturedLoaded = true;
            checkAllDataLoaded();

        });



    }




    // Display Recommended Movies Details
    private void onLoadRecommendedMovies() {

        binding.rvRecommended.setHasFixedSize(true);
        binding.rvRecommended.setNestedScrollingEnabled(false);
        binding.rvRecommended.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvRecommended.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(requireActivity(), 0), true));
        binding.rvRecommended.setAdapter(mMainAdapter);
        homeViewModel.movieRecommendedMutableLiveData.observe(getViewLifecycleOwner(), recommended -> {
            mMainAdapter.addMain(recommended.getRecommended());
            mRecommendedLoaded = true;
            checkAllDataLoaded();

        });

    }


    // Display Trending Movies
    private void onLoadTrendingMovies() {

        binding.rvTrending.setAdapter(mTrendingAdapter);
        binding.rvTrending.setHasFixedSize(true);
        binding.rvTrending.setNestedScrollingEnabled(false);
        binding.rvTrending.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvTrending.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(requireActivity(), 0), true));

        homeViewModel.movieTrendingMutableLiveData.observe(getViewLifecycleOwner(), trending -> {
            mTrendingAdapter.addTrending(trending.getTrending());
            mTrendingLoaded = true;
            checkAllDataLoaded();

        });

    }

    // Display Latest Movies
    private void onLoadLatestMovies() {

        binding.rvLatest.setAdapter(mLatestAdapter);
        binding.rvLatest.setHasFixedSize(true);
        binding.rvLatest.setNestedScrollingEnabled(false);
        binding.rvLatest.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvLatest.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(requireActivity(), 0), true));

        homeViewModel.movieLatestMutableLiveData.observe(getViewLifecycleOwner(), latest -> {
            mLatestAdapter.addLatest(latest.getLatest());

            mLatestLoaded = true;
            checkAllDataLoaded();


        });
    }




    // Display Popular Series
    private void onLoadPopularSeries() {

        binding.rvSeriesPopular.setAdapter(popularSeriesAdapter);
        binding.rvSeriesPopular.setHasFixedSize(true);
        binding.rvSeriesPopular.setNestedScrollingEnabled(false);
        binding.rvSeriesPopular.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvSeriesPopular.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(requireActivity(), 0), true));
        homeViewModel.popularSeriesMutableLiveData.observe(getViewLifecycleOwner(), popularseries -> popularSeriesAdapter.addPopular(popularseries.getPopular()));

    }



    // Display Latest Series
    private void onLoadLatestSeries(){

        binding.rvSeriesRecents.setAdapter(mSeriesRecentsAdapter);
        binding.rvSeriesRecents.setHasFixedSize(true);
        binding.rvSeriesRecents.setNestedScrollingEnabled(false);
        binding.rvSeriesRecents.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvSeriesRecents.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(requireActivity(), 0), true));
        homeViewModel.latestSeriesMutableLiveData.observe(getViewLifecycleOwner(), latestseries -> mSeriesRecentsAdapter.addLatest(latestseries.getLatestSeries()));


    }



    // Display Latest Animes
    private void onLoadLatestAnimes() {

        binding.rvAnimes.setAdapter(animesAdapter);
        binding.rvAnimes.setHasFixedSize(true);
        binding.rvAnimes.setNestedScrollingEnabled(false);
        binding.rvAnimes.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvAnimes.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(requireActivity(), 0), true));
        homeViewModel.latestAnimesMutableLiveData.observe(getViewLifecycleOwner(), animes -> animesAdapter.addToContent(animes.getAnimes()));

        if (settingsManager.getSettings().getAnime() == 0){

            binding.rvAnimes.setVisibility(View.GONE);
            binding.rvAnimesLinear.setVisibility(View.GONE);

        }



    }




    // Display New This Week Movies
    private void onLoadNewThisWeek() {
        binding.rvNewthisweek.setAdapter(mNewThisWeekAdapter);
        binding.rvNewthisweek.setHasFixedSize(true);
        binding.rvNewthisweek.setNestedScrollingEnabled(false);
        binding.rvNewthisweek.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvNewthisweek.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(requireActivity(), 0), true));
        homeViewModel.thisweekMutableLiveData.observe(getViewLifecycleOwner(), thisWeekend -> mNewThisWeekAdapter.addThisWeek(thisWeekend.getThisweek()));

    }





    // Display Popular Movies
    private void onLoadPopularMovies() {

        binding.rvPopular.setAdapter(mPopularAdapter);
        binding.rvPopular.setHasFixedSize(true);
        binding.rvPopular.setNestedScrollingEnabled(false);
        binding.rvPopular.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvPopular.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(requireActivity(), 2), true));
        homeViewModel.popularMoviesMutableLiveData.observe(getViewLifecycleOwner(), popular -> mPopularAdapter.addPopular(popular.getPopularMedia()));
    }




    // On Fragment Detach clear binding views &  adapters to avoid memory leak
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mFeaturedAdapter.unregisterAdapterDataObserver(binding.indicator.getAdapterDataObserver());
        adView = null;
        binding.adViewContainer.removeAllViews();
        binding.adViewContainer.removeAllViewsInLayout();
        binding.rvFeatured.setAdapter(null);
        binding.rvLatest.setAdapter(null);
        binding.rvRecommended.setAdapter(null);
        binding.rvTrending.setAdapter(null);
        binding.rvSeriesPopular.setAdapter(null);
        binding.rvSeriesRecents.setAdapter(null);
        binding.rvNewthisweek.setAdapter(null);
        binding.rvPopular.setAdapter(null);
        binding.rvAnimes.setAdapter(null);
        binding.constraintLayout.removeAllViews();
        binding.scrollView.removeAllViews();
        binding = null;

    }

    // Make sure all calls finished before showing results
    private void checkAllDataLoaded() {
        if (mFeaturedLoaded && mRecommendedLoaded && mTrendingLoaded && mLatestLoaded) {
            binding.scrollView.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);


            // Load Facebook Audience Interstitial ads
            if (settingsManager.getSettings().getAdFaceAudienceInterstitial() == 1) {

                Tools.onLoadFacebookAudience(getContext(),settingsManager.getSettings().getAdFaceAudienceInterstitial()

                        ,settingsManager.getSettings().getFacebookShowInterstitial()

                        ,settingsManager.getSettings().getAdUnitIdFacebookInterstitialAudience());
            }




            // Load  Admob Interstitial Ads
            if (settingsManager.getSettings().getAdInterstitial() == 1) {


                Tools.onLoadAdmobInterstitialAds(getContext(), settingsManager.getSettings().getAdInterstitial(),
                        settingsManager.getSettings().getAdShowInterstitial(),
                        settingsManager.getSettings().getAdUnitIdInterstitial());

            }



            // Load Admob Banner
            if ( settingsManager.getSettings().getAdBanner() == 1 && settingsManager.getSettings().getAdUnitIdBanner() !=null) {

                AdSize adSize = getAdSize();

                // Create an ad request.
                adView = new PublisherAdView(requireActivity());
                adView.setAdUnitId(settingsManager.getSettings().getAdUnitIdBanner());
                binding.adViewContainer.removeAllViews();
                binding.adViewContainer.addView(adView);
                adView.setAdSizes(adSize);

                PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();

                // Start loading the ad in the background.
                adView.loadAd(adRequest);

            } else {

                // Return admob test if no unit it set
                Tools.onLoadAdmobBanner(requireActivity(),binding.adViewContainer,"ca-app-pub-3940256099942544/6300978111");

            }

        }
    }



}
