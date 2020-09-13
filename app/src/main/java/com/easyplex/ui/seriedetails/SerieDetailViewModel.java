package com.easyplex.ui.seriedetails;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.easyplex.data.local.entity.Media;
import com.easyplex.data.model.credits.MovieCreditsResponse;
import com.easyplex.data.model.report.Report;
import com.easyplex.data.repository.MovieRepository;
import javax.inject.Inject;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

/**
 * ViewModel to cache, retrieve data for SeriesDetailActivity
 *
 * @author Yobex.
 */
public class SerieDetailViewModel extends ViewModel {

    private MovieRepository movieRepository;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    public final MutableLiveData<Media> movieDetailMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<MovieCreditsResponse> serieCreditsMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<Report> reportMutableLiveData = new MutableLiveData<>();


    @Inject
    SerieDetailViewModel(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;

    }





    // Send Report
    public void sendReport (String title,String message) {
        compositeDisposable.add(movieRepository.getReport(title,message)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(reportMutableLiveData::postValue, this::handleError)
        );
    }



    // Return Serie Details
    public void getSerieDetails(String tmdb) {
        compositeDisposable.add(movieRepository.getSerie(tmdb)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(movieDetailMutableLiveData::postValue, this::handleError)
        );
    }

    // Return Serie Cast
    public void getSerieCast(int id) {
        compositeDisposable.add(movieRepository.getSerieCredits(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(serieCreditsMutableLiveData::postValue, this::handleError)
        );
    }




    // Add Serie To Favorite
    public void addtvFavorite(Media series) {
        Timber.i("Serie Added To Watchlist");
        compositeDisposable.add(Completable.fromAction(() -> movieRepository.addFavorite(series))
                .subscribeOn(Schedulers.io())
                .subscribe());
    }


    // Remove Serie from Favorite
    public void removeTvFromFavorite(Media series) {
        Timber.i("Serie Removed From Watchlist");

        compositeDisposable.add(Completable.fromAction(() -> movieRepository.removeFavorite(series))
                .subscribeOn(Schedulers.io())
                .subscribe());
    }


    // Handle Errors
    private void handleError(Throwable e) {

        Timber.i("In onError()%s", e.getMessage());
    }



    // Check if  Serie is in the  Favorite Table
    public LiveData<Media> isFavorite(int movieId) {
        Timber.i("Checking if Serie is in the Favorites");
        return movieRepository.isFavorite(movieId);
    }



    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
