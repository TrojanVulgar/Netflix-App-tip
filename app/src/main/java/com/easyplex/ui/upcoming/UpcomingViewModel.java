package com.easyplex.ui.upcoming;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.easyplex.data.model.MovieResponse;
import com.easyplex.data.model.upcoming.Upcoming;
import com.easyplex.data.repository.MovieRepository;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

public class UpcomingViewModel extends ViewModel {


    private MovieRepository movieRepository;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    MutableLiveData<Upcoming> upcomingMutableLiveData;
    MutableLiveData<MovieResponse> upcomingResponseMutableLive;

    @Inject
    UpcomingViewModel(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
        upcomingMutableLiveData = new MutableLiveData<>();
        upcomingResponseMutableLive = new MutableLiveData<>();


    }



    // Fetch Upcoming Movies
    public void getUpcomingMovie() {
        compositeDisposable.add(movieRepository.getUpcoming()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(upcoming -> upcomingResponseMutableLive.postValue(upcoming), this::handleError)
        );
    }



    // Fetch Upcoming Movie Detail
    public void getUpcomingMovieDetail(int movieId) {
        compositeDisposable.add(movieRepository.getUpcomingById(movieId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(upcoming -> upcomingMutableLiveData.postValue(upcoming), this::handleError)
        );
    }


    // Handle Errors
    private void handleError(Throwable e) {
        e.printStackTrace();
        Timber.i("In onError()%s", e.getMessage());
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }


}
