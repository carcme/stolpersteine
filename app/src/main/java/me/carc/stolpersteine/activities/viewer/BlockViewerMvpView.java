package me.carc.stolpersteine.activities.viewer;

import me.carc.stolpersteine.activities.base.MvpView;
import me.carc.stolpersteine.data.remote.bio.Biography;


public interface BlockViewerMvpView extends MvpView {

    void onHtmlParsed(Biography biography);
}
