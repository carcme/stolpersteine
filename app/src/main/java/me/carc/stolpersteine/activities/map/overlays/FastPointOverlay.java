package me.carc.stolpersteine.activities.map.overlays;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.List;

import me.carc.stolpersteine.activities.map.MapActivity;
import me.carc.stolpersteine.common.C;
import me.carc.stolpersteine.data.remote.model.Coordinates;
import me.carc.stolpersteine.data.remote.model.Stolpersteine;

/**
 * Example of SimpleFastPointOverlay
 * Created by Miguel Porto on 12-11-2016.
 */

public class FastPointOverlay {
    private static final String TAG = MapActivity.class.getName();

    private PointOverlayOptions mOptions;
    private PointOverlay mPointOverlay;
    private List<Stolpersteine> mStolpersteines;

    public interface OnPointClicked {
      void onPointClicked(LabelledGeoPoint labelledGeoPoint, Stolpersteine element);
    };

    OnPointClicked mCallback;

    public FastPointOverlay(OnPointClicked callback) {
        mOptions = PointOverlayOptions.getDefaultStyle()
                .setSymbol(PointOverlayOptions.Shape.SQUARE)
                .setAlgorithm(PointOverlayOptions.RenderingAlgorithm.MAXIMUM_OPTIMIZATION)
                .setRadius(17)
                .setIsClickable(true)
                .setCellSize((int) (C.DENSITY * 12))
                .setMinZoomShowLabels(17);

        mCallback = callback;
    }

    public void addStones(MapView mapView, List<Stolpersteine> stolpersteines) {
        mStolpersteines = stolpersteines;

        // Remove the old points
        if (mPointOverlay != null)
            mapView.getOverlays().remove(mPointOverlay);

        List<IGeoPoint> points = new ArrayList<>();

        for (Stolpersteine stone : stolpersteines) {
            Coordinates ords = stone.getLocation().getCoordinates();
            points.add(new LabelledGeoPoint(ords.getLatitude(), ords.getLongitude(), stone.getId(),
                    stone.getPerson().getFullName(), stone.getLocation().getStreet()));
        }
        PointTheme pointTheme = new PointTheme(points);

        // create the overlay with the theme
        mPointOverlay = new PointOverlay(pointTheme, mOptions);

        // onClick callback
        mPointOverlay.setOnClickListener((points1, point) -> {

            for (Stolpersteine element : mStolpersteines) {
                if (element.getId().equals(((LabelledGeoPoint) points1.get(point)).getId())) {
                    mCallback.onPointClicked((LabelledGeoPoint) points1.get(point), element);
                    break;
                }
            }
        });

        // add overlay
        mapView.getOverlays().add(mPointOverlay);
        mapView.invalidate();
    }
}
