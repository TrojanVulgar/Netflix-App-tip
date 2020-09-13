package com.easyplex.ui.home;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.easyplex.data.model.MovieResponse;
import com.easyplex.data.repository.MovieRepository;
import javax.inject.Inject;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;



/**
 * ViewModel to cache, retrieve data for HomeFragment
 *
 * @author Yobex.
 */

public class HomeViewModel extends ViewModel {


    private MovieRepository movieRepository;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    public final MutableLiveData<MovieResponse> movieFeaturedMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<MovieResponse>  movieRecommendedMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<MovieResponse>  movieTrendingMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<MovieResponse>  movieLatestMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<MovieResponse>  popularSeriesMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<MovieResponse> latestSeriesMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<MovieResponse> latestAnimesMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<MovieResponse> thisweekMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<MovieResponse> popularMoviesMutableLiveData = new MutableLiveData<>();

    @Inject
    HomeViewModel(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }



    public void getHomeItems() {



        // Fetch Popular Movies
        compositeDisposable.add(movieRepository.getPopularMovies()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(popularMoviesMutableLiveData::postValue, this::handleError)
        );


        // Fetch This Week Movies
        compositeDisposable.add(movieRepository.getThisWeek()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(thisweekMutableLiveData::postValue, this::handleError)
        );



        // Fetch Animes
        compositeDisposable.add(movieRepository.getAnimes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(latestAnimesMutableLiveData::postValue, this::handleError)
        );

        // Fetch Popular Series
        compositeDisposable.add(movieRepository.getPopularSeries()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(popularSeriesMutableLiveData::postValue, this::handleError)
        );


        // Fetch Latest Series
        compositeDisposable.add(movieRepository.getLatestSeries()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(latestSeriesMutableLiveData::postValue, this::handleError)
        );


        // Fetch Features Movies & Series
        compositeDisposable.add(movieRepository.getFeatured()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(movieFeaturedMutableLiveData::postValue, this::handleError)
        );

        // Fetch Recommended Movies
        compositeDisposable.add(movieRepository.getRecommended()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(movieRecommendedMutableLiveData::postValue, this::handleError)
        );



        // Fetch Trending Movies
        compositeDisposable.add(movieRepository.getTrending()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(movieTrendingMutableLiveData::postValue, this::handleError)
        );



        // Fetch Latest Movies
        compositeDisposable.add(movieRepository.getLatestMovies()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(movieLatestMutableLiveData::postValue, this::handleError)
        );
    }



    // HandleError
    private void handleError(Throwable e) {

        Timber.i("In onError()%s", e.getMessage());
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }


}
