package com.easyplex.ui.manager;

import android.content.SharedPreferences;
import com.easyplex.data.model.settings.Settings;
import static android.provider.ContactsContract.CommonDataKinds.Website.URL;
import static com.easyplex.util.Constants.ADS_SETTINGS;
import static com.easyplex.util.Constants.AD_BANNER;
import static com.easyplex.util.Constants.AD_BANNER_FACEEBOK_ENABLE;
import static com.easyplex.util.Constants.AD_BANNER_FACEEBOK_UNIT_ID;
import static com.easyplex.util.Constants.AD_BANNER_UNIT;
import static com.easyplex.util.Constants.AD_FACEBOOK_INTERSTITIAL_SHOW;
import static com.easyplex.util.Constants.AD_INTERSTITIAL;
import static com.easyplex.util.Constants.AD_INTERSTITIAL_FACEEBOK_ENABLE;
import static com.easyplex.util.Constants.AD_INTERSTITIAL_FACEEBOK_UNIT_ID;
import static com.easyplex.util.Constants.AD_INTERSTITIAL_SHOW;
import static com.easyplex.util.Constants.AD_INTERSTITIAL_UNIT;
import static com.easyplex.util.Constants.ANIME;
import static com.easyplex.util.Constants.APP_NAME;
import static com.easyplex.util.Constants.APP_URL_ANDROID;
import static com.easyplex.util.Constants.AUTOSUBSTITLES;
import static com.easyplex.util.Constants.FEATURED_HOME_NUMBERS;
import static com.easyplex.util.Constants.IMDB_COVER_PATH;
import static com.easyplex.util.Constants.LATEST_VERSION;
import static com.easyplex.util.Constants.PAYPAL_AMOUNT;
import static com.easyplex.util.Constants.PAYPAL_CLIENT_ID;
import static com.easyplex.util.Constants.PRIVACY_POLICY;
import static com.easyplex.util.Constants.PURCHASE_KEY;
import static com.easyplex.util.Constants.RELEASE_NOTES;
import static com.easyplex.util.Constants.TMDB;
import static com.easyplex.util.Constants.UPDATE_TITLE;


/**
 * EasyPlex - Android Movie Portal App
 * @package     EasyPlex - Android Movie Portal App
 * @author      @Y0bEX
 * @copyright   Copyright (c) 2020 Y0bEX,
 * @license     http://codecanyon.net/wiki/support/legal-terms/licensing-terms/
 * @profile     https://codecanyon.net/user/yobex
 * @link        yobexd@gmail.com
 * @skype       yobexd@gmail.com
 **/


public class SettingsManager {



    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;


    public SettingsManager(SharedPreferences prefs){
        this.prefs = prefs;
        this.editor = prefs.edit();

    }

    public SettingsManager() {


    }


    public void saveSettings(Settings settings){

        editor.putString(APP_NAME, settings.getAppName()).commit();
        editor.putInt(AD_FACEBOOK_INTERSTITIAL_SHOW, settings.getFacebookShowInterstitial()).commit();
        editor.putInt(AD_INTERSTITIAL_SHOW, settings.getAdShowInterstitial()).commit();
        editor.putInt(AD_INTERSTITIAL, settings.getAdInterstitial()).commit();
        editor.putString(AD_INTERSTITIAL_UNIT, settings.getAdUnitIdInterstitial()).commit();
        editor.putInt(AD_BANNER, settings.getAdBanner()).commit();
        editor.putString(AD_BANNER_UNIT, settings.getAdUnitIdBanner()).commit();
        editor.putString(PURCHASE_KEY, settings.getPurchaseKey()).commit();
        editor.putString(TMDB, settings.getTmdbApiKey()).commit();
        editor.putString(PRIVACY_POLICY, settings.getPrivacyPolicy()).commit();
        editor.putInt(AUTOSUBSTITLES, settings.getAutosubstitles()).commit();
        editor.putInt(ANIME, settings.getAnime()).commit();
        editor.putString(LATEST_VERSION, settings.getLatestVersion()).commit();
        editor.putString(UPDATE_TITLE, settings.getUpdateTitle()).commit();
        editor.putString(RELEASE_NOTES, settings.getReleaseNotes()).commit();
        editor.putString(URL, settings.getUrl()).commit();
        editor.putString(PAYPAL_CLIENT_ID, settings.getPaypalClientId()).commit();
        editor.putString(PAYPAL_AMOUNT, settings.getPaypalAmount()).commit();
        editor.putInt(FEATURED_HOME_NUMBERS, settings.getFeaturedHomeNumbers()).commit();
        editor.putString(APP_URL_ANDROID, settings.getAppUrlAndroid()).commit();
        editor.putString(IMDB_COVER_PATH, settings.getImdbCoverPath()).commit();
        editor.putInt(ADS_SETTINGS, settings.getAds()).commit();
        editor.putInt(AD_INTERSTITIAL_FACEEBOK_ENABLE, settings.getAdFaceAudienceInterstitial()).commit();
        editor.putString(AD_INTERSTITIAL_FACEEBOK_UNIT_ID, settings.getAdUnitIdFacebookInterstitialAudience()).commit();
        editor.putInt(AD_BANNER_FACEEBOK_ENABLE, settings.getAdFaceAudienceBanner()).commit();
        editor.putString(AD_BANNER_FACEEBOK_UNIT_ID, settings.getAdUnitIdFacebookBannerAudience()).commit();


    }

    public void deleteSettings(){

        editor.remove(APP_NAME).commit();
        editor.remove(AD_INTERSTITIAL).commit();
        editor.remove(AD_INTERSTITIAL_UNIT).commit();
        editor.remove(AD_BANNER).commit();
        editor.remove(AD_BANNER_UNIT).commit();
        editor.remove(PURCHASE_KEY).commit();
        editor.remove(TMDB).commit();
        editor.remove(PRIVACY_POLICY).commit();
        editor.remove(AUTOSUBSTITLES).commit();
        editor.remove(LATEST_VERSION).commit();
        editor.remove(UPDATE_TITLE).commit();
        editor.remove(RELEASE_NOTES).commit();
        editor.remove(URL).commit();
        editor.remove(PAYPAL_CLIENT_ID).commit();
        editor.remove(PAYPAL_AMOUNT).commit();
        editor.remove(FEATURED_HOME_NUMBERS).commit();
        editor.remove(APP_URL_ANDROID).commit();
        editor.remove(IMDB_COVER_PATH).commit();
        editor.remove(ADS_SETTINGS).commit();
        editor.remove(AD_INTERSTITIAL_FACEEBOK_ENABLE).commit();
        editor.remove(AD_INTERSTITIAL_FACEEBOK_UNIT_ID).commit();
        editor.remove(AD_BANNER_FACEEBOK_ENABLE).commit();
        editor.remove(AD_BANNER_FACEEBOK_UNIT_ID).commit();



    }

    public Settings getSettings(){

        Settings settings = new Settings();
        settings.setAppName(prefs.getString(APP_NAME, null));
        settings.setFacebookShowInterstitial(prefs.getInt(AD_FACEBOOK_INTERSTITIAL_SHOW, 0));
        settings.setAdShowInterstitial(prefs.getInt(AD_INTERSTITIAL_SHOW, 0));
        settings.setAdInterstitial(prefs.getInt(AD_INTERSTITIAL, 0));
        settings.setAdUnitIdInterstitial(prefs.getString(AD_INTERSTITIAL_UNIT, null));
        settings.setAdBanner(prefs.getInt(AD_BANNER, 0));
        settings.setAdUnitIdBanner(prefs.getString(AD_BANNER_UNIT, null));
        settings.setPurchaseKey(prefs.getString(PURCHASE_KEY, null));
        settings.setTmdbApiKey(prefs.getString(TMDB, null));
        settings.setPrivacyPolicy(prefs.getString(PRIVACY_POLICY, null));
        settings.setAutosubstitles(prefs.getInt(AUTOSUBSTITLES, 1));
        settings.setUrl(prefs.getString(URL, null));
        settings.setPaypalClientId(prefs.getString(PAYPAL_CLIENT_ID, null));
        settings.setPaypalAmount(prefs.getString(PAYPAL_AMOUNT, null));
        settings.setAppUrlAndroid(prefs.getString(APP_URL_ANDROID, null));
        settings.setFeaturedHomeNumbers(prefs.getInt(FEATURED_HOME_NUMBERS, 0));
        settings.setUpdateTitle(prefs.getString(UPDATE_TITLE, null));
        settings.setReleaseNotes(prefs.getString(RELEASE_NOTES, null));
        settings.setImdbCoverPath(prefs.getString(IMDB_COVER_PATH, null));
        settings.setAds(prefs.getInt(ADS_SETTINGS, 0));
        settings.setAnime(prefs.getInt(ANIME,0));
        settings.setAdFaceAudienceInterstitial(prefs.getInt(AD_INTERSTITIAL_FACEEBOK_ENABLE, 0));
        settings.setAdFaceAudienceBanner(prefs.getInt(AD_BANNER_FACEEBOK_ENABLE, 0));
        settings.setAdUnitIdFacebookBannerAudience(prefs.getString(AD_BANNER_FACEEBOK_UNIT_ID, null));
        settings.setAdUnitIdFacebookInterstitialAudience(prefs.getString(AD_INTERSTITIAL_FACEEBOK_UNIT_ID, null));



        return settings;


    }





}
