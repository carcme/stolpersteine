package me.carc.stolpersteine.common.injection.component;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;
import me.carc.stolpersteine.App;
import me.carc.stolpersteine.common.injection.ApplicationContext;
import me.carc.stolpersteine.common.injection.module.ApplicationModule;
import me.carc.stolpersteine.common.location.BTownFusedLocation;
import me.carc.stolpersteine.data.DataManager;
import me.carc.stolpersteine.data.SharedPrefsHelper;

@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {

    void inject(App app);

    @ApplicationContext
    Context context();

    Application application();


    DataManager getDataManager();

    SharedPrefsHelper getPreferenceHelper();

    BTownFusedLocation getFusedLocation();
}
