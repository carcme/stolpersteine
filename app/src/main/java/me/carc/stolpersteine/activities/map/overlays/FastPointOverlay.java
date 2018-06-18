package me.carc.stolpersteine.activities.map.overlays;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.view.View;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.List;

import me.carc.stolpersteine.activities.map.MapActivity;
import me.carc.stolpersteine.activities.viewer.BlockViewerActivity;
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

    private Context mContext;

    public FastPointOverlay(Context context) {
        mContext = context;
        mOptions = PointOverlayOptions.getDefaultStyle()
                .setSymbol(PointOverlayOptions.Shape.SQUARE)
                .setAlgorithm(PointOverlayOptions.RenderingAlgorithm.MAXIMUM_OPTIMIZATION)
                .setRadius(17)
                .setIsClickable(true)
                .setCellSize((int) (C.DENSITY * 12))
                .setMinZoomShowLabels(17);
    }

    public void addStones(MapView mapView, List<Stolpersteine> stolpersteines) {
        mStolpersteines = stolpersteines;

        // Remove the old points
        if (mPointOverlay != null)
            mapView.getOverlays().remove(mPointOverlay);

        List<IGeoPoint> points = new ArrayList<>();

        for (Stolpersteine stone : stolpersteines) {
            Coordinates ords = stone.getLocation().getCoordinates();
            points.add(new LabelledGeoPoint(ords.getLatitude(), ords.getLongitude(), stone.getId(), stone.getPerson().getFullName()));
        }
        PointTheme pointTheme = new PointTheme(points);

        // create the overlay with the theme
        mPointOverlay = new PointOverlay(pointTheme, mOptions);

        // onClick callback
        mPointOverlay.setOnClickListener(new PointOverlay.OnClickListener() {
            @Override
            public void onClick(PointOverlay.PointAdapter points, Integer point) {

                for (Stolpersteine element : mStolpersteines) {
                    if (element.getId().equals(((LabelledGeoPoint) points.get(point)).getId())) {

                        final Snackbar snackBar = Snackbar.make(mapView, ((LabelledGeoPoint) points.get(point)).getLabel(), Snackbar.LENGTH_LONG)
                                .setAction("View", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(mapView.getContext(), BlockViewerActivity.class);
                                        intent.putExtra(BlockViewerActivity.BLOCK_DATA, element);
                                        mapView.getContext().startActivity(intent);
                                    }
                                });
                        snackBar.show();
                        break;
                    }
                }
            }
        });

        // add overlay
        mapView.getOverlays().add(mPointOverlay);
        mapView.invalidate();
    }
}
