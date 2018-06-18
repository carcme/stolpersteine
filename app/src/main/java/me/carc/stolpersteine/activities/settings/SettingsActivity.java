package me.carc.stolpersteine.activities.settings;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import javax.inject.Inject;

import me.carc.stolpersteine.R;
import me.carc.stolpersteine.activities.base.MvpBaseActivity;
import me.carc.stolpersteine.data.DataManager;
import me.carc.stolpersteine.data.SharedPrefsHelper;
import me.carc.stolpersteine.fragments.settings.AboutFragment;
import me.carc.stolpersteine.fragments.settings.SettingsPagerFragment;

/**
 * Setting activity
 *
 * Created by bamptonm on 24/10/17.
 */

public class SettingsActivity extends MvpBaseActivity implements SettingsMvpView, AboutFragment.BtnClickListener {
    private static final String TAG = SettingsActivity.class.getName();


    @Inject SettingsPresenter mPresenter;
    @Inject SharedPrefsHelper mSharePrefs;
    @Inject DataManager mDataMngr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String headerTitle = getString(R.string.shared_string_settings);
        int headerTitleTextColor = R.color.md_white_1000;
        int headerColorPrimary = R.color.md_red_700;
        int headerColorSecondary = R.color.md_red_900;

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back);

            String color = "#" + Integer.toHexString(ContextCompat.getColor(this, headerTitleTextColor) & 0x00ffffff);
            assert upArrow != null;
            upArrow.setColorFilter(Color.parseColor(color), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);

            // TODO: 22/12/2017 dont like this - find way to change actionbar text color that is correct/nice
            getSupportActionBar().setTitle(Html.fromHtml("<font color=\"" + color + "\">" + headerTitle + "</font>"));
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, headerColorPrimary)));
            getWindow().setStatusBarColor(ContextCompat.getColor(this, headerColorSecondary));
        }

        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsPagerFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if(getIntent().hasExtra("lang")) {
            setResult(RESULT_OK, getIntent());
        }
        super.onBackPressed();
    }

    //************* CALL BACKS **************//

    @Override
    public void onDonateClick() {
        Toast.makeText(this, "NOT IMPLEMENTED", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onDonateClick: NOT IMPLEMENTED");
    }
}
