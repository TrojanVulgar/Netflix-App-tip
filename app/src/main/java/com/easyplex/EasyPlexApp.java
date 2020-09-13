package com.easyplex;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDexApplication;

import com.easyplex.di.AppInjector;
import com.easyplex.util.Tools;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.MobileAds;

import javax.inject.Inject;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;
import timber.log.Timber;

/**
 * Application level class.
 *
 * @author Yobex.
 */
public class EasyPlexApp extends MultiDexApplication implements HasAndroidInjector {


    @Inject
    DispatchingAndroidInjector<Object> androidInjector;

    private static Context context;


    @Override
    public void onCreate() {
        super.onCreate();


        AppInjector.init(this);

        // Initialize the Audience Network SDK
        AudienceNetworkAds.initialize(this);


        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, initializationStatus -> {});

        // Initialize the Audience Network SDK
        AudienceNetworkAds.initialize(this);



        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        Timber.i("Creating our Application");


        EasyPlexApp.context = getApplicationContext();

    }

    public static Context  getInstance() {

        return EasyPlexApp.context;
    }

    public static boolean hasNetwork() {
        return Tools.checkIfHasNetwork(context);
    }


    public static Context getContext() {
        return context;
    }

    @Override
    public AndroidInjector<Object> androidInjector() {
        AppInjector.init(this);
        return androidInjector;
    }
}

/*
 * Application has activities that is why we implement HasActivityInjector interface.
 * Activities have fragments so we have to implement HasFragmentInjector/HasSupportFragmentInjector
 * in our activities.
 * No child fragment and don’t inject anything in your fragments, no need to implement
 * HasSupportFragmentInjector.
 */
