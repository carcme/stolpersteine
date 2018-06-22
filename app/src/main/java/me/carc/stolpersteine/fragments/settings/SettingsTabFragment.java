package me.carc.stolpersteine.fragments.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import me.carc.stolpersteine.R;
import me.carc.stolpersteine.data.DataManager;
import me.carc.stolpersteine.data.SharedPrefsHelper;

/**
 * System settings
 * Created by bamptonm on 7/11/17.
 */

public class SettingsTabFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Inject DataManager mDataMngr;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

/*
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
    }
*/

    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

/*        if(key.equals(Preferences.LANGUAGE)) {
            ListPreference langPref = (ListPreference)findPreference(key);
            langPref.setSummary(langPref.getEntry());
            Intent intent = getActivity().getIntent();
            intent.putExtra("lang", true);
        } else */
        if(key.equals(SharedPrefsHelper.PREF_KEY_TRANSLATE)) {
            CheckBoxPreference toursPref = (CheckBoxPreference)findPreference(key);
            sharedPreferences.edit().putBoolean(SharedPrefsHelper.PREF_KEY_TRANSLATE, toursPref.isChecked()).apply();
            toursPref.setSummary(toursPref.isChecked() ?
                    R.string.pref_translate_summaryOn :
                    R.string.pref_translate_summaryOff);

/*
            if(toursPref.isChecked())
                toursPref.setSummary(R.string.pref_translate_summaryOn);
            else
                toursPref.setSummary(R.string.pref_translate_summaryOff);
*/
/*
        } else if (key.equals(Preferences.GPS)) {
            CheckBoxPreference gpsPref = (CheckBoxPreference)findPreference(key);
            if(gpsPref.isChecked())
                gpsPref.setSummary(R.string.pref_location_use_gps_best_accuracy);
            else
                gpsPref.setSummary(R.string.pref_location_use_gps_best_battery);
*/
        }
    }
}
