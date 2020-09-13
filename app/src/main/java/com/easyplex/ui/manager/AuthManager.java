package com.easyplex.ui.manager;

import android.content.SharedPreferences;
import com.easyplex.data.model.auth.UserAuthInfo;
import static com.easyplex.util.Constants.PREMUIM;


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



public class AuthManager {


    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;


    public AuthManager(SharedPreferences prefs){
        this.prefs = prefs;
        this.editor = prefs.edit();
    }

    public void saveSettings(UserAuthInfo userAuthInfo){
        editor.putInt(PREMUIM, userAuthInfo.getPremuim()).commit();
        editor.apply();
    }

    public void deleteAuth(){
        editor.remove(PREMUIM).commit();
    }

    public UserAuthInfo getUserInfo(){
        UserAuthInfo userAuthInfo = new UserAuthInfo();
        userAuthInfo.setPremuim(prefs.getInt(PREMUIM, 0));
        return userAuthInfo;
    }




}
