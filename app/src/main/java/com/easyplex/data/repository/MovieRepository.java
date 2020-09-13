package com.easyplex.data.repository;

import androidx.lifecycle.LiveData;

import com.easyplex.data.local.entity.Media;
import com.easyplex.data.model.episode.EpisodeStream;
import com.easyplex.data.model.stream.MediaStream;
import com.easyplex.data.remote.ApiInterface;
import com.easyplex.ui.manager.SettingsManager;
import com.easyplex.data.local.dao.FavoriteDao;
import com.easyplex.data.model.MovieResponse;
import com.easyplex.data.model.report.Report;
import com.easyplex.data.model.search.SearchResponse;
import com.easyplex.data.model.credits.MovieCreditsResponse;
import com.easyplex.data.model.genres.GenresByID;
import com.easyplex.data.model.genres.GenresData;
import com.easyplex.data.model.upcoming.Upcoming;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import hu.akarnokd.rxjava3.bridge.RxJavaBridge;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import timber.log.Timber;


/**
 * Repository that acts as a mediators between different data sources; API network and ROOM database.
 * It abstracts the data sources from the rest of the app
 *
 * @author Yobex.
 */
@Singleton
public class MovieRepository  {

    private final FavoriteDao favoriteDao;
    ApiInterface requestMainApi;

    // Return Imdb Api from Api Interfae ( https://api.themoviedb.org/3/ )
    @Inject
    @Named("imdb")
    ApiInterface requestImdbApi;


    @Inject
    SettingsManager settingsManager;


    @Inject
    MovieRepository(FavoriteDao favoriteDao, ApiInterface requestMainApi, ApiInterface requestImdbApi) {
        this.favoriteDao = favoriteDao;
        this.requestMainApi = requestMainApi;
        this.requestImdbApi = requestImdbApi;

    }




    // Return Serie Seasons
    public Observable<MovieResponse> getSerieSeasons(String seasonsId) {
        return requestMainApi.getSerieSeasons(seasonsId);
    }




    // Return Random Movie
    public Observable<MovieResponse> getMoviRandom() {
        return requestMainApi.getMoviRandom(settingsManager.getSettings().getPurchaseKey());
    }





    // Return Substitle Episode
    public Observable<EpisodeStream> getEpisodeSubstitle(String tmdb) {
        return requestMainApi.getEpisodeSubstitle(tmdb);
    }





    // Return Serie Stream
    public Observable<MediaStream> getSerieStream(String tmdb) {
        return requestMainApi.getSerieStream(tmdb);
    }



    // Return Serie By Id
    public Observable<Media> getSerie(String serieTmdb) {
        return requestMainApi.getSerieById(serieTmdb, settingsManager.getSettings().getPurchaseKey());
    }




    // Return Anime By Id
    public Observable<MovieResponse> getAnimes() {
        return requestMainApi.getAnimes(settingsManager.getSettings().getPurchaseKey());
    }



    // Return Upcoming Movie By Id
    public Observable<Upcoming> getUpcomingById(int movieID) {
        return requestMainApi.getUpcomingMovieDetail(movieID);

    }



    // Return Upcoming Movies Lists
    public Observable<MovieResponse> getUpcoming() {
        return requestMainApi.getUpcomingMovies();

    }



    // Return Relateds Movies for a movie
    public Observable<MovieResponse> getRelateds(int movieID) {
        return requestMainApi.getRelatedsMovies(movieID);

    }



    // Return Casts Lists for  Movie
    public Observable<MovieCreditsResponse> getMovieCredits(int movieID) {
        return requestImdbApi.getMovieCredits(movieID,settingsManager.getSettings().getTmdbApiKey());

    }


    // Return Casts Lists for a Serie
    public Observable<MovieCreditsResponse> getSerieCredits(int movieID) {
        return requestImdbApi.getSerieCredits(movieID,settingsManager.getSettings().getTmdbApiKey());

    }



    // Return Movie By Genre
    public Observable<GenresData> getMovieByGenre(int id) {
        return requestMainApi.getGenreByID(id,settingsManager.getSettings().getPurchaseKey());
    }



    // Return Serie By Genre
    public Observable<GenresData> getSerieByGenre(int id) {
        return requestMainApi.getSerieById(id,settingsManager.getSettings().getPurchaseKey());
    }



    // Return Movies Genres
    public Observable<GenresByID> getMoviesGenres() {
        return requestMainApi.getGenreName(settingsManager.getSettings().getPurchaseKey());
    }



    // Return Report
    public Observable<Report> getReport(String title,String message) {
        return requestMainApi.report(title,message);
    }





    // Return Movie Detail by Id
    public Observable<Media> getMovie(String tmdb) {
        return requestMainApi.getMovieByTmdb(tmdb);
    }



    // Return Popular Series for HomeFragment
    public Observable<MovieResponse> getPopularSeries() {
        return requestMainApi.getSeriesPopular(settingsManager.getSettings().getPurchaseKey());
    }



    // Return ThisWeek Movies & Series for HomeFragment
    public Observable<MovieResponse> getThisWeek() {
        return requestMainApi.getThisWeekMovies(settingsManager.getSettings().getPurchaseKey());
    }



    // Return Popular Movies for HomeFragment
    public Observable<MovieResponse> getPopularMovies() {
        return requestMainApi.getPopularMovies(settingsManager.getSettings().getPurchaseKey());
    }



    // Return Latest Series for HomeFragment
    public Observable<MovieResponse> getLatestSeries() {
        return requestMainApi.getSeriesRecents(settingsManager.getSettings().getPurchaseKey());
    }



    // Return Featured Movies for HomeFragment
    public Observable<MovieResponse> getFeatured() {
        return requestMainApi.getMovieFeatured(settingsManager.getSettings().getPurchaseKey());
    }




    // Return Recommended Series for HomeFragment
    public Observable<MovieResponse> getRecommended() {
        return requestMainApi.getRecommended(settingsManager.getSettings().getPurchaseKey());
    }



    // Return Tranding Movies for HomeFragment
    public Observable<MovieResponse> getTrending() {
        return requestMainApi.getTrending(settingsManager.getSettings().getPurchaseKey());
    }




    // Return Latest Movies for HomeFragment
    public Observable<MovieResponse> getLatestMovies() {
        return requestMainApi.getMovieLatest(settingsManager.getSettings().getPurchaseKey());
    }




    // Return Suggested Movies for HomeFragment
    public Observable<MovieResponse> getSuggested() {
        return requestMainApi.getMovieSuggested(settingsManager.getSettings().getPurchaseKey());
    }



    // Handle Search
    public Observable<SearchResponse> getSearch(String query) {
        return requestMainApi.getSearch(query);
    }



    // Return Latest Streaming Channels for HomeFragment
    public Observable<MovieResponse> getLatestStreaming() {
        return requestMainApi.getLatestStreaming(settingsManager.getSettings().getPurchaseKey());
    }




    // Add Movie or Serie in favorite
    public void addFavorite(Media mediaDetail) {
        favoriteDao.saveFavoriteMovie(mediaDetail);
    }


    // Remove Movie or Serie from favorite
    public void removeFavorite(Media mediaDetail) {
        Timber.i("Removing %s to database", mediaDetail.getTitle());
        favoriteDao.deleteFavoriteMovie(mediaDetail);
    }



    // Return Favorite Lists of Movies or Series
    public Flowable<List<Media>> getFavorites() {
        return favoriteDao.getFavoriteMovies().as(RxJavaBridge.toV3Flowable());
    }



    // Delete All Movies & Series from Favorite Table
    public void deleteAllFromFavorites() {
        favoriteDao.deleteFavoriteMovie();
    }




    // Return if the movie or serie is in favorite table
    public LiveData<Media> isFavorite(int movieid) {
        return favoriteDao.isFavoriteMovie(movieid);
    }


    // Return True if Movie or Serie in Featured List is in Favorite Table
    public boolean isFeaturedFavorite(int movieid) {
        return favoriteDao.isFeaturedFavoriteMovie(movieid);
    }

}
