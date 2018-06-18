package me.carc.stolpersteine.common.injection.module;

import android.content.Context;
import android.content.SharedPreferences;

import dagger.Module;
import dagger.Provides;
import me.carc.stolpersteine.common.injection.ActivityContext;

/**
 * Created by bamptonm on 04/03/2018.
 */

@Module
public class SharedPreferencesModule {

    private Context context;

    public SharedPreferencesModule(Context context) {
        this.context = context;
    }

    @Provides
    @ActivityContext
    SharedPreferences provideSharedPreferences() {
        return context.getSharedPreferences("PrefName", Context.MODE_PRIVATE);
    }
}