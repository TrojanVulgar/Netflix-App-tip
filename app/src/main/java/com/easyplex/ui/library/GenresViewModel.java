package com.easyplex.ui.library;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.easyplex.data.model.genres.GenresByID;
import com.easyplex.data.model.genres.GenresData;
import com.easyplex.data.repository.MovieRepository;
import javax.inject.Inject;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

/**
 * ViewModel to cache, retrieve data for MoviesFragment & SeriesFragment
 *
 * @author Yobex.
 */
public class GenresViewModel extends ViewModel {

    private MovieRepository movieRepository;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    public final MutableLiveData<GenresByID> movieDetailMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<GenresData> movieGenresDataMutableLiveData = new MutableLiveData<>();


    @Inject
    GenresViewModel(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }




    // Fetch Movies Genres List
    public void getMoviesGenresList() {
        compositeDisposable.add(movieRepository.getMoviesGenres()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(movieDetailMutableLiveData::postValue, this::handleError)
        );
    }



    // Fetch Movie By Genre
    public void getMoviesByGenres(int id) {
        compositeDisposable.add(movieRepository.getMovieByGenre(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(movieGenresDataMutableLiveData::postValue, this::handleError)
        );
    }

    // Fetch Serie By Genre
    public void getSeriesByGenres(int id) {
        compositeDisposable.add(movieRepository.getSerieByGenre(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(movieGenresDataMutableLiveData::postValue, this::handleError)
        );
    }



    // Handle Errors
    private void handleError(Throwable e) {
        Timber.i("In onError()%s", e.getMessage());
    }



    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
