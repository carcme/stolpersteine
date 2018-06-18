package me.carc.stolpersteine.activities.map;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

import me.carc.stolpersteine.activities.base.MvpView;
import me.carc.stolpersteine.data.remote.model.Stolpersteine;


public interface MapMvpView extends MvpView {

    void addOverlays();
    void addBlocksToDB(final int offset, final ArrayList<Stolpersteine> list);
    void mapMoved(GeoPoint geoPoint);

    void setTrackingMode(boolean mode);
    void enableLocationOverlay(boolean mode);
    void showError();

}
