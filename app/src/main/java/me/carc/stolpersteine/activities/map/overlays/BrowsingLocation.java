package me.carc.stolpersteine.activities.map.overlays;

import org.osmdroid.util.GeoPoint;

/**
 * store map location when pressing goto my locaiton
 * Created by bamptonm on 14/06/2018.
 */

public class BrowsingLocation {

    private float zoomLvl;
    private GeoPoint browsingLocation;

    public BrowsingLocation(GeoPoint browsingLocation, float zoom) {
        this.browsingLocation = browsingLocation;
        this.zoomLvl = zoom;
    }

    public float getZoomLvl() { return zoomLvl; }

    public GeoPoint getBrowsingLocation() { return browsingLocation; }
}