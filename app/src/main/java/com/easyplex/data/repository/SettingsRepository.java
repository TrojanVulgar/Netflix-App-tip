package com.easyplex.data.repository;

import com.easyplex.data.model.status.Status;
import com.easyplex.data.remote.ApiInterface;
import com.easyplex.data.model.ads.Ads;
import com.easyplex.data.model.settings.Settings;
import javax.inject.Inject;
import io.reactivex.rxjava3.core.Observable;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class SettingsRepository {



    @Inject
    ApiInterface apiInterface;


    @Inject
    SettingsRepository(ApiInterface apiInterface) {
        this.apiInterface = apiInterface;

    }




    // Return List of Added Ads for the Player
    public Observable<Ads> getAdsSettings() {
        return apiInterface.getAdsSettings();
    }



    // Return App Settings
    public Observable<Settings> getSettings() {
        return apiInterface.getSettings();
    }





    // Return Status
    public Observable<Status> getStatus() {
        return apiInterface.getStatus();
    }



}

