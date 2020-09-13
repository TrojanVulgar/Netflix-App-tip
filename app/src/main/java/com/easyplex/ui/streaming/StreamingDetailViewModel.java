package com.easyplex.ui.streaming;

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
 * ViewModel to cache, retrieve data for StreamingFragment
 *
 * @author Yobex.
 */
public class StreamingDetailViewModel extends ViewModel {

    private MovieRepository movieRepository;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    public final MutableLiveData<MovieResponse> latestStreamingMutableLiveData = new MutableLiveData<>();


    @Inject
    StreamingDetailViewModel (MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }



    // Fetch Streaming List
    public void getStreaming() {
        compositeDisposable.add(movieRepository.getLatestStreaming()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(latestStreamingMutableLiveData::postValue, this::handleError)
        );
    }



    // Handle Error for getStreaming
    private void handleError(Throwable e) {
        Timber.i("In onError()%s", e.getMessage());
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
