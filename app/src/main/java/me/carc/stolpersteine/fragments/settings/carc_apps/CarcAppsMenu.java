package me.carc.stolpersteine.fragments.settings.carc_apps;


import me.carc.stolpersteine.R;

/**
 * Set up the front page menu items
 *
 * Created by bamptonm on 18/12/2017.
 */

public enum CarcAppsMenu {

    BTOWN(R.mipmap.ic_launcher_btown, R.string.appTitleBtown, R.string.appDescBtown, "btown"),
    ITIMER(R.mipmap.ic_launcher_timer, R.string.appTitleITimer, R.string.appDescITimer, "carcintervaltimer"),
    FAKER(R.mipmap.ic_launcher_fc, R.string.appTitleFakeCall, R.string.appDescFakeCall, "fakecallandsms_mvp"),
    AGD(R.mipmap.ic_launcher_agd, R.string.appTitleAGD, R.string.appDescAGB, "anygivendate"),
    BBOOKS(R.drawable.app_image_blackbooks, R.string.appTitleBlackBooks, R.string.appDescBlackBooks, "blackbooks");

    final int iconDrawable;
    final int titleResourceId;
    final int subTitleResourceId;
    final String urlExt;

    CarcAppsMenu(int drawable, int titleResourceId, int subTitleResourceId, String urlExt) {
        this.iconDrawable = drawable;
        this.titleResourceId = titleResourceId;
        this.subTitleResourceId = subTitleResourceId;
        this.urlExt = urlExt;
    }

    public int getTitleResourceId() {
        return titleResourceId;
    }

    public int getSubTitleResourceId() {
        return subTitleResourceId;
    }

    public String getUrlExtension() { return urlExt; }

    public int getIconDrawable() {
        return iconDrawable;
    }
}