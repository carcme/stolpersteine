package me.carc.stolpersteine.activities.settings;

import javax.inject.Inject;

import me.carc.stolpersteine.activities.base.MvpBasePresenter;
import me.carc.stolpersteine.activities.viewer.BlockViewerMvpView;
import me.carc.stolpersteine.data.DataManager;
import me.carc.stolpersteine.data.SharedPrefsHelper;


/**
 *
 */
public class SettingsPresenter extends MvpBasePresenter<BlockViewerMvpView> {
    private static final String TAG = SettingsPresenter.class.getName();

    @Inject
    public SettingsPresenter() {
    }

    @Inject
    SharedPrefsHelper mSharePrefs;
    @Inject
    DataManager mDataManager;


    @Override
    public void attachView(BlockViewerMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
    }


}
