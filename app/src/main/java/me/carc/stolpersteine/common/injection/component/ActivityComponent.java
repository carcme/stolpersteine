package me.carc.stolpersteine.common.injection.component;

import dagger.Subcomponent;
import me.carc.stolpersteine.activities.PermissionActivity;
import me.carc.stolpersteine.activities.map.MapActivity;
import me.carc.stolpersteine.activities.settings.SettingsActivity;
import me.carc.stolpersteine.activities.viewer.BlockViewerActivity;
import me.carc.stolpersteine.common.injection.PerActivity;
import me.carc.stolpersteine.common.injection.module.ActivityModule;

/**
 * This component inject dependencies to all Activities across the application
 */
@PerActivity
@Subcomponent(modules = {ActivityModule.class})
public interface ActivityComponent {

    void inject(MapActivity mapActivity);
    void inject(PermissionActivity permissionActivity);
    void inject(BlockViewerActivity blockViewerActivity);
    void inject(SettingsActivity settingsActivity);

}
