package com.easyplex.ui.mylist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.ViewModel;

import com.easyplex.data.local.entity.Media;
import com.easyplex.data.repository.MovieRepository;
import java.util.List;
import javax.inject.Inject;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

/**
 * ViewModel to cache, retrieve data for MyList
 *
 */
public class MoviesListViewModel extends ViewModel {


    private MovieRepository movieRepository;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private LiveData<List<Media>> favoriteMoviesLiveData;


    @Inject
    MoviesListViewModel(MovieRepository movieRepository) {

        // Get a list of Favorite Movies from the Database
        favoriteMoviesLiveData = LiveDataReactiveStreams.fromPublisher(movieRepository.getFavorites()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()));

        this.movieRepository = movieRepository;

    }



    // Return Movies & Series & Animes in MyList
    LiveData<List<Media>> getMoviesFavorites() {
        return favoriteMoviesLiveData;
    }



    // Delete All Movies from MyList
    public void deleteAllMovies() {
        Timber.i("MyList has been cleared...");

        compositeDisposable.add(Completable.fromAction(() -> movieRepository.deleteAllFromFavorites())
                .subscribeOn(Schedulers.io())
                .subscribe());
    }



    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
