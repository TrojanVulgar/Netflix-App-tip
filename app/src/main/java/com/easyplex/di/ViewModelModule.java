package com.easyplex.di;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.easyplex.ui.animes.AnimeViewModel;
import com.easyplex.ui.home.HomeViewModel;
import com.easyplex.ui.library.GenresViewModel;
import com.easyplex.ui.login.LoginViewModel;
import com.easyplex.ui.moviedetails.MovieDetailViewModel;
import com.easyplex.ui.mylist.MoviesListViewModel;
import com.easyplex.ui.register.RegisterViewModel;
import com.easyplex.ui.search.SearchViewModel;
import com.easyplex.ui.seriedetails.SerieDetailViewModel;
import com.easyplex.ui.settings.SettingsViewModel;
import com.easyplex.ui.streaming.StreamingDetailViewModel;
import com.easyplex.ui.upcoming.UpcomingViewModel;
import com.easyplex.viewmodel.MoviesViewModelFactory;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

/*
 * @author Yobex.
 * */
@Module
public abstract class ViewModelModule {


    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel.class)
    abstract ViewModel bindHomeViewModel(HomeViewModel homeViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(UpcomingViewModel.class)
    abstract ViewModel bindUpcomingViewModel(UpcomingViewModel upcomingViewModel);


    @Binds
    @IntoMap
    @ViewModelKey(MovieDetailViewModel.class)
    abstract ViewModel bindMovieDetailViewModel(MovieDetailViewModel movieDetailViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(SerieDetailViewModel.class)
    abstract ViewModel bindSerieDetailViewModel(SerieDetailViewModel serieDetailViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel.class)
    abstract ViewModel bindSearchViewModel(SearchViewModel searchViewModel);


    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel.class)
    abstract ViewModel bindLoginViewModel(LoginViewModel loginViewModel);


    @Binds
    @IntoMap
    @ViewModelKey(RegisterViewModel.class)
    abstract ViewModel bindRegisterViewModel(RegisterViewModel registerViewModel);


    @Binds
    @IntoMap
    @ViewModelKey(GenresViewModel.class)
    abstract ViewModel bindGenresViewModel(GenresViewModel genresViewModel);


    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel.class)
    abstract ViewModel bindSettingsViewModel(SettingsViewModel settingsViewModel);


    @Binds
    @IntoMap
    @ViewModelKey(MoviesListViewModel.class)
    abstract ViewModel bindMoviesListViewModel(MoviesListViewModel moviesListViewModel);


    @Binds
    @IntoMap
    @ViewModelKey(AnimeViewModel.class)
    abstract ViewModel bindAnimeViewModel(AnimeViewModel animeViewModel);


    @Binds
    @IntoMap
    @ViewModelKey(StreamingDetailViewModel.class)
    abstract ViewModel bindStreamingDetailViewModel(StreamingDetailViewModel streamingDetailViewModel);


    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(MoviesViewModelFactory factory);


}
