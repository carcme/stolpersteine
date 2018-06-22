package me.carc.stolpersteine.data;

import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * shared prefs helper
 * Created by janisharali on 25/12/16.
 */

@Singleton
public class SharedPrefsHelper {

    public static String PREF_KEY_TRANSLATE         = "pref_key_translate";

    public static String USER_KEY_PERMISSIONS_REQ   = "request_permissions";
    public static String USER_KEY_ZOOM              = "user_zooom";
    public static String USER_KEY_LOCATION          = "user_location";
    public static String USER_KEY_LANGUAGE          = "user_language";
    public static String USER_KEY_QUOTA_TIME        = "user_quota_used_time";


    private SharedPreferences mSharedPreferences;

    @Inject
    public SharedPrefsHelper(SharedPreferences sharedPreferences) {
        mSharedPreferences = sharedPreferences;
    }


    public void put(String key, String value) {
        mSharedPreferences.edit().putString(key, value).apply();
    }
    public void put(String key, int value) {
        mSharedPreferences.edit().putInt(key, value).apply();
    }
    public void put(String key, float value) {
        mSharedPreferences.edit().putFloat(key, value).apply();
    }
    public void put(String key, boolean value) {
        mSharedPreferences.edit().putBoolean(key, value).apply();
    }


    public String get(String key, String defaultValue) {
        return mSharedPreferences.getString(key, defaultValue);
    }
    public Integer get(String key, int defaultValue) {
        return mSharedPreferences.getInt(key, defaultValue);
    }
    public Float get(String key, float defaultValue) {
        return mSharedPreferences.getFloat(key, defaultValue);
    }
    public Boolean get(String key, boolean defaultValue) {
        return mSharedPreferences.getBoolean(key, defaultValue);
    }

    public void deleteSavedData(String key) {
        mSharedPreferences.edit().remove(key).apply();
    }
}
