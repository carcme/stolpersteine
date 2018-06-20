package me.carc.stolpersteine.activities.map;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import me.carc.stolpersteine.activities.base.MvpBasePresenter;
import me.carc.stolpersteine.activities.map.overlays.BrowsingLocation;
import me.carc.stolpersteine.common.Commons;
import me.carc.stolpersteine.data.DataManager;
import me.carc.stolpersteine.data.SharedPrefsHelper;
import me.carc.stolpersteine.data.remote.StolpersteineApi;
import me.carc.stolpersteine.data.remote.StolpersteineApiServiceProvider;
import me.carc.stolpersteine.data.remote.model.Coordinates;
import me.carc.stolpersteine.data.remote.model.Stolpersteine;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 *
 */
public class MapPresenter extends MvpBasePresenter<MapMvpView> {

    private static final String TAG = MapPresenter.class.getName();

    final static int REQUEST_BATCH_SIZE = 500;
    private final static int GENERIC_DELAY = 300;
    private static float MIN_POI_LOOKUP_ZOOM_LVL = 16;

    @Inject public MapPresenter() {}
    @Inject SharedPrefsHelper mSharePrefs;
    @Inject DataManager mDataManager;

    private MapView mMap;
    private boolean mTrackingMode, bAllowReturnLocation;
    private BrowsingLocation browsingLocation;

    private Handler delayedTaskHandler = new Handler();

    @Override
    public void attachView(MapMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
    }


    void initMap(MapView mapView, Stolpersteine stolpersteine) {
        mMap = mapView;

        mMap.setTilesScaledToDpi(true);
        mMap.setBuiltInZoomControls(false);
        mMap.setMultiTouchControls(true);

        ScaleBarOverlay scaleBarOverlay = new ScaleBarOverlay(mMap);
        mMap.getOverlays().add(scaleBarOverlay);

        getMvpView().addOverlays();

        mMap.setOnTouchListener(onMapTouchListener);

        mMap.setTileSource(TileSourceFactory.HIKEBIKEMAP);

        if(Commons.isNull(stolpersteine)) {
            GeoPoint geoPoint = mDataManager.getLastPosition();
            mMap.getController().setZoom(mDataManager.getZoom());
            mMap.getController().animateTo(geoPoint);
            getMvpView().mapMoved(geoPoint);
        } else {
            Coordinates ords =stolpersteine.getLocation().getCoordinates();
            GeoPoint geoPoint = new GeoPoint(ords.getLatitude(), ords.getLongitude());
            mMap.getController().setZoom(17f);
            mMap.getController().animateTo(geoPoint);
        }
    }

    public void zoomToMyLocation(final GeoPoint geoPoint) {

        mTrackingMode = !mTrackingMode;

        if (browsingLocation == null)
            browsingLocation = new BrowsingLocation((GeoPoint) mMap.getMapCenter(), mMap.getZoomLevel());

        updateUIWithTrackingMode(geoPoint);
    }

    private void updateUIWithTrackingMode(GeoPoint geoPoint) {
        getMvpView().setTrackingMode(mTrackingMode);

        if (mTrackingMode) {
            bAllowReturnLocation = true;

            if (Commons.isNotNull(geoPoint)) {
                double currentZoomLvl = mMap.getZoomLevelDouble();

                if (currentZoomLvl < MIN_POI_LOOKUP_ZOOM_LVL) {

                    // Dont like this little hack - does make the animateTo more accurate though
                    if (currentZoomLvl < 9)
                        mMap.getController().setZoom(9f);
                    
                    mMap.getController().animateTo(geoPoint);

                    getMvpView().enableLocationOverlay(false);

                    delayedTaskHandler.postDelayed(animateToTask, GENERIC_DELAY);
                } else {
                    // already at a decent zoom level - just animate
                    mMap.getController().animateTo(geoPoint);
                    getMvpView().mapMoved(geoPoint);
                }
            }
        } else {
            if (bAllowReturnLocation && Commons.isNotNull(browsingLocation)) {
                new Handler().postDelayed(() -> {
                    mMap.getController().zoomTo(browsingLocation.getZoomLvl());
                    browsingLocation = null;
                }, GENERIC_DELAY);
                mMap.getController().animateTo(browsingLocation.getBrowsingLocation());
            }
        }
    }

    private Runnable animateToTask = new Runnable() {
        public void run() {
            if (!mMap.isAnimating()) {
                new Handler().postDelayed(() -> {
                    getMvpView().enableLocationOverlay(true);
                    getMvpView().mapMoved((GeoPoint) mMap.getMapCenter());
                }, GENERIC_DELAY);

                mMap.getController().zoomTo(MIN_POI_LOOKUP_ZOOM_LVL);

            } else {
                delayedTaskHandler.removeCallbacks(this);
                delayedTaskHandler.postDelayed(this, GENERIC_DELAY);
            }
        }
    };

    private Runnable mapMoveTask = new Runnable() {
        public void run() {
            Log.d(TAG, "taskRunner: Map Center = " + mMap.getMapCenter());
            getMvpView().mapMoved((GeoPoint) mMap.getMapCenter());
        }
    };


    private static int counter = 0;
    /**
     * Override the map touch listener
     */
    private MapView.OnTouchListener onMapTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    break;

                case MotionEvent.ACTION_MOVE:
                    if(counter++ > 10) {   // goldilocks number
                        delayedTaskHandler.removeCallbacks(mapMoveTask);
                        delayedTaskHandler.postDelayed(mapMoveTask, GENERIC_DELAY);
                        if (mTrackingMode) {
                            mTrackingMode = false;
                            getMvpView().setTrackingMode(false);
                            bAllowReturnLocation = false;
                            browsingLocation = null;
                            return true;
                        }
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    counter = 0;
                    break;

                case MotionEvent.ACTION_CANCEL:
                    break;
            }
            return false;
        }
    };

    void getStumblingBlocks(final int offset) {
        StolpersteineApi service = StolpersteineApiServiceProvider.get();
        Call<List<Stolpersteine>> call = service.getStolpersteines(offset, REQUEST_BATCH_SIZE);
        call.enqueue(new Callback<List<Stolpersteine>>() {

            @SuppressWarnings({"ConstantConditions"})
            @Override
            public void onResponse(@NonNull Call<List<Stolpersteine>> call, @NonNull Response<List<Stolpersteine>> response) {

                if(response.body() != null ){
                    ArrayList<Stolpersteine> list = new ArrayList<>(response.body());

                    if(list.size() > 0)
                        getMvpView().addBlocksToDB(offset, list);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Stolpersteine>> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
}
