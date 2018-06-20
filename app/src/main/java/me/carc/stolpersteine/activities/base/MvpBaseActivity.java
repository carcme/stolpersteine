package me.carc.stolpersteine.activities.base;

import android.os.Bundle;
import android.support.v4.util.LongSparseArray;
import android.support.v7.app.AppCompatActivity;

import java.util.concurrent.atomic.AtomicLong;

import me.carc.stolpersteine.App;
import me.carc.stolpersteine.activities.BaseActivity;
import me.carc.stolpersteine.common.injection.component.ActivityComponent;
import me.carc.stolpersteine.common.injection.component.ConfigPersistentComponent;
import me.carc.stolpersteine.common.injection.component.DaggerConfigPersistentComponent;
import me.carc.stolpersteine.common.injection.module.ActivityModule;


/**
 * Abstract activity that every other Activity in this application must implement. It handles
 * creation of Dagger components and makes sure that instances of ConfigPersistentComponent survive
 * across configuration changes.
 */
public class MvpBaseActivity extends BaseActivity {

    private static final String KEY_ACTIVITY_ID = "KEY_ACTIVITY_ID";
    private static final AtomicLong NEXT_ID = new AtomicLong(0);
    private static final LongSparseArray<ConfigPersistentComponent> sComponentsMap = new LongSparseArray<>();

    private ActivityComponent mActivityComponent;
    private long mActivityId;


    public App getApp() {
        return (App) getApplication();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create the ActivityComponent and reuses cached ConfigPersistentComponent if this is
        // being called after a configuration change.
        mActivityId = savedInstanceState != null ? savedInstanceState.getLong(KEY_ACTIVITY_ID) : NEXT_ID.getAndIncrement();

        ConfigPersistentComponent configPersistentComponent = sComponentsMap.get(mActivityId, null);

        if (configPersistentComponent == null) {
            configPersistentComponent = DaggerConfigPersistentComponent.builder()
                    .applicationComponent(App.get(this).getComponent())
                    .build();

            sComponentsMap.put(mActivityId, configPersistentComponent);
        }
        mActivityComponent = configPersistentComponent.activityComponent(new ActivityModule(this));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(KEY_ACTIVITY_ID, mActivityId);
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

    private void clearReferences() {
        AppCompatActivity currActivity = getApp().getCurrentActivity();
        if (this.equals(currActivity))
            getApp().setCurrentActivity(null);
    }

    @Override
    protected void onDestroy() {
        if (!isChangingConfigurations()) {
            sComponentsMap.remove(mActivityId);
        }
        super.onDestroy();
    }

    public ActivityComponent activityComponent() {
        return mActivityComponent;
    }

}
