package me.carc.stolpersteine.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;

import me.carc.stolpersteine.App;
import me.carc.stolpersteine.common.C;

public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = BaseActivity.class.getName();

    @VisibleForTesting
    private static final Object lock = new Object();

    public App getApp() {
        return (App) getApplication();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getBaseFunctions();
        super.onCreate(savedInstanceState);
    }/**/

    @Override
    protected void onResume() {
        super.onResume();
        getApp().setCurrentActivity(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        clearReferences();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearReferences();
    }

    private void clearReferences() {
        AppCompatActivity currActivity = getApp().getCurrentActivity();
        if (this.equals(currActivity))
            getApp().setCurrentActivity(null);
    }

    public void getBaseFunctions() {
        calculateImageHeight();
        getResources().updateConfiguration(Resources.getSystem().getConfiguration(), getResources().getDisplayMetrics());
        C.USER_LANGUAGE = Resources.getSystem().getConfiguration().locale.getLanguage();

    }

    /**
     * Display image on 2/3 of screen
     */
    protected void calculateImageHeight() {
        synchronized (lock) {
            if (C.IMAGE_WIDTH == null || C.IMAGE_HEIGHT == null) {
                final DisplayMetrics metrics = new DisplayMetrics();
                Display display = getWindowManager().getDefaultDisplay();

                display.getRealMetrics(metrics);

                // SCREEN DIMENSIONS
                C.DENSITY = metrics.density;
                C.SCREEN_WIDTH = metrics.widthPixels;
                C.SCREEN_HEIGHT = metrics.heightPixels;

                // IMAGE DIMENSIONS
                C.IMAGE_WIDTH = C.SCREEN_WIDTH;
                C.IMAGE_HEIGHT = (int) (C.SCREEN_HEIGHT * 2 / 3.0f);
            }
        }
    }
}