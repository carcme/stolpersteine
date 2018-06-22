package me.carc.stolpersteine;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;

import javax.inject.Inject;

import io.fabric.sdk.android.Fabric;
import me.carc.stolpersteine.common.NetworkChangeReceiver;
import me.carc.stolpersteine.common.injection.component.ApplicationComponent;
import me.carc.stolpersteine.common.injection.component.DaggerApplicationComponent;
import me.carc.stolpersteine.common.injection.module.ApplicationModule;
import me.carc.stolpersteine.data.DataManager;
import me.carc.stolpersteine.data.db.AppDatabase;

/**
 * Application class for BTown
 * Created by bamptonm on 19/09/2017.
 */

public class App extends Application {


    @Inject DataManager dataManager;

    protected ApplicationComponent mApplicationComponent;
    public static final String DATABASE_NAME = "stolpersteine.db";
    private static Context applicationContext;

    private AppDatabase database;
    private NetworkChangeReceiver networkChangeReceiver;
    private AppCompatActivity mCurrentActivity = null;
    private Location mLatestLocation;


    public static App get(Context context) {
        return (App) context.getApplicationContext();
    }

    public AppCompatActivity getCurrentActivity() {
        return mCurrentActivity;
    }
    public void setCurrentActivity(AppCompatActivity mCurrentActivity) {
        this.mCurrentActivity = mCurrentActivity;
    }

    public ApplicationComponent getComponent() {
        if (mApplicationComponent == null) {
            mApplicationComponent = DaggerApplicationComponent.builder()
                    .applicationModule(new ApplicationModule(this))
                    .build();
        }

        mApplicationComponent.inject(this);
        return mApplicationComponent;
    }

    /**
     * Init global values
     */
    public void onCreate() {
        super.onCreate();

        if(BuildConfig.USE_CRASHLYTICS)
            Fabric.with(this, new Crashlytics());

        applicationContext = getApplicationContext();
        registerConnectivityRecver();
    }


    private void registerConnectivityRecver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(new NetworkChangeReceiver(), intentFilter);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && (networkInfo.isConnected());
    }


    @Override
    public void onTerminate() {
        try {
            unregisterReceiver(networkChangeReceiver);
        } catch (IllegalArgumentException e) { /* EMPTY CATCH */ }

        super.onTerminate();
    }
}