package me.carc.stolpersteine.activities.map.overlays;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.views.overlay.simplefastpoint.StyledLabelledGeoPoint;

import java.util.Iterator;
import java.util.List;

/**
 * This class is just a simple wrapper for a List of {@link IGeoPoint}s to be used in
 * {@link org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlay}. Can be used for unlabelled or labelled GeoPoints.
 * Use the simple constructor, or otherwise be sure to set the labelled and styled parameters of the
 * constructor to match the kind of points.
 * More complex cases should implement {@link org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlay.PointAdapter}, not extend this
 * one. This is a simple example on how to implement an adapter for any case.
 * Created by Miguel Porto on 26-10-2016.
 */

public final class PointTheme implements PointOverlay.PointAdapter {
    private final List<IGeoPoint> mPoints;
    private boolean mLabelled, mStyled;

    public PointTheme(List<IGeoPoint> pPoints) {
        this(pPoints, pPoints.size() != 0 && pPoints.get(0) instanceof LabelledGeoPoint, pPoints.size() != 0 && pPoints.get(0) instanceof StyledLabelledGeoPoint);
    }

    public PointTheme(List<IGeoPoint> pPoints, boolean labelled) {
        this(pPoints, labelled, false);
    }

    public PointTheme(List<IGeoPoint> pPoints, boolean labelled, boolean styled) {
        mPoints = pPoints;
        mLabelled = labelled;
        mStyled = styled;
    }

    @Override
    public int size() {
        return mPoints.size();
    }

    @Override
    public IGeoPoint get(int i) {
        return mPoints.get(i);
    }

    @Override
    public boolean isLabelled() {
        return mLabelled;
    }

    @Override
    public boolean isStyled() {
        return mStyled;
    }

    /**
     * NOTE: this iterator will be called very frequently, avoid complicated code.
     * @return
     */
    @Override
    public Iterator<IGeoPoint> iterator() {
        return mPoints.iterator();
    }

}
