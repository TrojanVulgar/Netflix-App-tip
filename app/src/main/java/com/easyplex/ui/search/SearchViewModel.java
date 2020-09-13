package com.easyplex.ui.search;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.easyplex.data.model.MovieResponse;
import com.easyplex.data.model.search.SearchResponse;
import com.easyplex.data.repository.MovieRepository;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

/**
 * ViewModel for SearchActivity.
 *
 * @author Yobex.
 */
public class SearchViewModel extends ViewModel {




    private MovieRepository movieRepository;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    MutableLiveData<MovieResponse> movieDetailMutableLiveData;



    @Inject
    SearchViewModel(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
        movieDetailMutableLiveData = new MutableLiveData<>();
    }

    public Observable<SearchResponse> search(final String query) {
        return movieRepository.getSearch(query);
    }




    // Load Suggested Movies
    public void getSuggestedMovies() {
        compositeDisposable.add(movieRepository.getSuggested()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(movieDetailMutableLiveData::postValue, this::handleError)
        );
    }


    // Handle Error
    private void handleError(Throwable e) {

        Timber.i("In onError()%s", e.getMessage());
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        Timber.i("SearchViewModel Cleared");
    }
}
