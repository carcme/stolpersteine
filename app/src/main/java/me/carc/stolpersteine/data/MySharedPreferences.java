package me.carc.stolpersteine.data;

import android.content.SharedPreferences;

import javax.inject.Inject;

/**
 * shared prefs
 * Created by bamptonm on 04/03/2018.
 */
public class MySharedPreferences {

    private SharedPreferences mSharedPreferences;

    @Inject
    public MySharedPreferences(SharedPreferences mSharedPreferences) {
        this.mSharedPreferences = mSharedPreferences;
    }

    public void putData(String key, int data) {
        mSharedPreferences.edit().putInt(key,data).apply();
    }

    public int getData(String key) {
        return mSharedPreferences.getInt(key,0);
    }
}