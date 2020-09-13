package com.easyplex.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.easyplex.data.model.auth.Login;
import com.easyplex.data.model.auth.UserAuthInfo;
import com.easyplex.data.remote.ErrorHandling;
import com.easyplex.data.repository.AuthRepository;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;


/**
 * ViewModel to cache, retrieve data for LoginActivity
 *
 * @author Yobex.
 */
public class LoginViewModel extends ViewModel {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private AuthRepository authRepository;


    public final MutableLiveData<UserAuthInfo> authDetailMutableLiveData = new MutableLiveData<>();

    public final MutableLiveData<UserAuthInfo> authUpgradeMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<UserAuthInfo> authEditeMutableLiveData = new MutableLiveData<>();




    @Inject
    LoginViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;

    }







    // Update User Profile
    public LiveData<ErrorHandling<UserAuthInfo>> updateUser(String name,String email , String password) {
        return authRepository.editUserProfile(name,email, password);
    }



    // Update User to Premuim
    public void getUpgrade() {
        compositeDisposable.add(authRepository.getUpgrade()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(authUpgradeMutableLiveData::postValue, this::handleError)
        );
    }




    // Return Login Route
    public LiveData<ErrorHandling<Login>> getLogin(String username , String password) {
        return authRepository.getDetail(username, password);
    }



    // Return the Authenticated User Details
    public void getAuthDetails() {
        compositeDisposable.add(authRepository.getAuth()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(authDetailMutableLiveData::postValue, this::handleError)
        );
    }




    private void handleError(Throwable e) {
        Timber.i("In onError()%s", e.getMessage());
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
