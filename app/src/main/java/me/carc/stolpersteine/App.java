package me.carc.stolpersteine;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;

import javax.inject.Inject;

import io.fabric.sdk.android.Fabric;
import me.carc.stolpersteine.common.Commons;
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

    protected ApplicationComponent mApplicationComponent;

    @Inject DataManager dataManager;

    public static final String DATABASE_NAME = "stolpersteine.db";
    public static final String SHARED_PREFS_NAME = App.class.getName();
    private static Context applicationContext;

    private AppDatabase database;
    private NetworkChangeReceiver networkChangeReceiver;
    private AppCompatActivity mCurrentActivity = null;
    private Location mLatestLocation;
    public SharedPreferences mSharedPreferences;
    private boolean isUpdatingFirebase;


    public Location getLatestLocation() {
        return mLatestLocation;
    }
    public void setLatestLocation(Location location) {
        mLatestLocation = location;
    }


    private Intent imagesServiceIntent;

    // Dont like this!! Used in CacheDir and TinyDB, both of which should be removed/reworked
    public static Context getAC() {
        return applicationContext;
    }

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

    // Needed to replace the component with a test specific one
    public void setComponent(ApplicationComponent applicationComponent) {
        mApplicationComponent = applicationComponent;
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

    public synchronized AppDatabase getDB() {
        if (Commons.isNull(database))
            database = initDB();
        return database;
    }


    /**
     * Init database
     */
    private AppDatabase initDB() {

        return Room.databaseBuilder(getApplicationContext(), AppDatabase.class, DATABASE_NAME)
                .fallbackToDestructiveMigration()
//                .addMigrations(Migrations.MIGRATION_1_2)
                .build();
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