package me.carc.stolpersteine.data;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.osmdroid.util.GeoPoint;

import javax.inject.Inject;
import javax.inject.Singleton;

import me.carc.stolpersteine.common.Commons;
import me.carc.stolpersteine.common.injection.ApplicationContext;

/**
 * Created by janisharali on 25/12/16.
 */

@Singleton
public class DataManager {


    private SharedPrefsHelper mSharedPrefsHelper;

    @Inject
    public DataManager(@ApplicationContext Context context, SharedPrefsHelper sharedPrefsHelper) {
        mSharedPrefsHelper = sharedPrefsHelper;
    }

    public void setZoom(float zoom) {
        mSharedPrefsHelper.put(SharedPrefsHelper.PREF_KEY_ZOOM, zoom);
    }
    public float getZoom(){
        return mSharedPrefsHelper.get(SharedPrefsHelper.PREF_KEY_ZOOM, 16f);
    }

    public void setLastPosition(GeoPoint location) {
        String gson = new Gson().toJson(location);
        mSharedPrefsHelper.put(SharedPrefsHelper.PREF_KEY_LOCATION, gson);
    }
    public GeoPoint getLastPosition(){
        String location = mSharedPrefsHelper.get(SharedPrefsHelper.PREF_KEY_LOCATION, null);
        GeoPoint point = new Gson().fromJson(location, new TypeToken<GeoPoint>() {}.getType());
        if(Commons.isNull(point)) {
            point = new GeoPoint(52.514489, 13.350240);
        }
        return point;
    }

    public void setUserLanguage(String lang) {
        mSharedPrefsHelper.put(SharedPrefsHelper.PREF_KEY_LANGUAGE, lang);
    }
    public String getUserLanguage(){
        return mSharedPrefsHelper.get(SharedPrefsHelper.PREF_KEY_LANGUAGE, null);
    }


    public boolean hasPermissions(){
        return mSharedPrefsHelper.get(SharedPrefsHelper.PREF_KEY_PERMISSIONS_REQ, false);
    }
    public void permissionGranted(boolean granted) {
        mSharedPrefsHelper.put(SharedPrefsHelper.PREF_KEY_PERMISSIONS_REQ, granted);
    }
}
