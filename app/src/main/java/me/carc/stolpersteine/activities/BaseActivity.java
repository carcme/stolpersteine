package me.carc.stolpersteine.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

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
    }

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
//                if (C.SCREEN_HEIGHT > 1200)
//                    C.IMAGE_HEIGHT = (int) (C.SCREEN_HEIGHT * 3 / 4.0f);
//                else
                C.IMAGE_HEIGHT = (int) (C.SCREEN_HEIGHT * 2 / 3.0f);
            }
        }
    }

    /**
     * Set the background color of the status bar
     *
     * @param color status bar background color
     */
    protected void setStatusBarColor(boolean palette, int color) {
        if (C.HAS_K)
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        final View decorView = getWindow().getDecorView();
        View view = new View(this);
        final int statusBarHeight = getStatusBarHeight();
        view.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight));
        if (palette)
            view.setBackgroundColor(color);
        else
            view.setBackgroundColor(ContextCompat.getColor(this, color));
        ((ViewGroup) decorView).addView(view);
    }

    /**
     * find height of the status bar
     *
     * @return the height
     */
    protected int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressWarnings("unused")
    public void setTranslucentStatusBarLollipop(Window window) {
        setStatusBarColor(false, android.R.color.transparent);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @SuppressWarnings("unused")
    private void setTranslucentStatusBarKiKat(Window window) {
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }


    protected void removeToolBarFlags(Toolbar toolbar) {
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(0);
    }


    protected void scrollHider(RecyclerView rv, final FloatingActionButton fab) {
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0)
                    fab.hide();
                else
                    fab.show();
            }
        });
    }



    public void showAlertDialog(@StringRes int title, @StringRes int message, @StringRes int btnText) {
        showAlertDialog(title, message, btnText, -1);
    }

    public void showAlertDialog(@StringRes int title, @StringRes int message, @StringRes int btnText, @DrawableRes int icon) {
        AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        dlg.setTitle(title);
        dlg.setMessage(message);

        if (btnText != -1)
            dlg.setPositiveButton(btnText, null);

        if (icon != -1)
            dlg.setIcon(icon);

        dlg.show();
    }
}