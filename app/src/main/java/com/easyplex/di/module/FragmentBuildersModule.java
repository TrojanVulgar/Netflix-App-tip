package com.easyplex.di.module;

import com.easyplex.ui.home.HomeFragment;
import com.easyplex.ui.library.LibraryFragment;
import com.easyplex.ui.library.MoviesFragment;
import com.easyplex.ui.library.SeriesFragment;
import com.easyplex.ui.mylist.MyListFragment;
import com.easyplex.ui.search.DiscoverFragment;
import com.easyplex.ui.settings.SettingsActivity;
import com.easyplex.ui.streaming.StreamingFragment;
import com.easyplex.ui.upcoming.UpComingFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/*
 * @author Yobex.
 * */
@Module
public abstract class FragmentBuildersModule {


    @ContributesAndroidInjector
    abstract HomeFragment contributeHomeFragment();

    @ContributesAndroidInjector
    abstract UpComingFragment contributeUpcomingFragment();

    @ContributesAndroidInjector
    abstract DiscoverFragment contributeDiscoverFragment();


    @ContributesAndroidInjector
    abstract MoviesFragment contributeMoviesFragment();

    @ContributesAndroidInjector
    abstract SeriesFragment contributeSeriesFragment();

    @ContributesAndroidInjector
    abstract LibraryFragment contributeLibraryFragment();

    @ContributesAndroidInjector
    abstract MyListFragment contributeMyListMoviesFragment();


    @ContributesAndroidInjector
    abstract StreamingFragment contributeLiveFragment();

    @ContributesAndroidInjector
    abstract SettingsActivity contributeSettingsFragment();

}
