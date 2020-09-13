package com.easyplex.ui.settings;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.easyplex.data.model.ads.Ads;
import com.easyplex.data.model.settings.Settings;
import com.easyplex.data.model.status.Status;
import com.easyplex.data.repository.SettingsRepository;
import javax.inject.Inject;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

/**
 * ViewModel to cache, retrieve App Settings
 *
 * @author Yobex.
 */
public class SettingsViewModel extends ViewModel {


    private SettingsRepository settingsRepository;
    public final MutableLiveData<Settings> settingsMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<Ads> adsMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<Status> statusMutableLiveData = new MutableLiveData<>();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();




    @Inject
    SettingsViewModel(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;

    }


    public void getSettingsDetails() {

        // Fetch Settings Details
        compositeDisposable.add(settingsRepository.getSettings()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(settingsMutableLiveData::postValue, this::handleError));



        // Fetch Ads Details
       compositeDisposable.add(settingsRepository.getAdsSettings()
               .subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread())
               .cache()
               .subscribe(adsMutableLiveData::postValue, this::handleError));




        // Fetch Status Details
      compositeDisposable.add(settingsRepository.getStatus()
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .cache()
              .subscribe(statusMutableLiveData::postValue, this::handleError))
        ;

    }




    // Handle Errors
    private void handleError(Throwable e) {
        Timber.i("In onError()%s", e.getMessage());
    }


    @Override
    public void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
