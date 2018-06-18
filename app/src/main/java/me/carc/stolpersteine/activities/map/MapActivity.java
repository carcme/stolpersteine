package me.carc.stolpersteine.activities.map;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.carc.stolpersteine.R;
import me.carc.stolpersteine.activities.PermissionActivity;
import me.carc.stolpersteine.activities.base.MvpBaseActivity;
import me.carc.stolpersteine.activities.map.overlays.FastPointOverlay;
import me.carc.stolpersteine.activities.settings.SettingsActivity;
import me.carc.stolpersteine.activities.viewer.BlockViewerActivity;
import me.carc.stolpersteine.common.C;
import me.carc.stolpersteine.common.Commons;
import me.carc.stolpersteine.common.utils.ImageUtils;
import me.carc.stolpersteine.common.utils.ViewUtils;
import me.carc.stolpersteine.data.DataManager;
import me.carc.stolpersteine.data.SharedPrefsHelper;
import me.carc.stolpersteine.data.db.AppDatabase;
import me.carc.stolpersteine.data.db.blocks.StolpersteineDao;
import me.carc.stolpersteine.data.db.blocks.StolpersteineViewModel;
import me.carc.stolpersteine.data.remote.model.Stolpersteine;
import me.carc.stolpersteine.data.translate.ResponseTranslate;
import me.carc.stolpersteine.data.translate.ResultTranslate;
import me.carc.stolpersteine.data.translate.TranslateApi;
import me.carc.stolpersteine.data.translate.TranslateApiServiceProvider;
import me.carc.stolpersteine.fragments.BlockListDialogFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Main Screen used to show stolpersteine locaitons
 *
 * Created by bamptonm on 08/06/2018.
 */

public class MapActivity extends MvpBaseActivity implements MapMvpView, MapEventsReceiver {
    private static final String TAG = MapActivity.class.getName();
    private static final int MAX_VALID_ACCRACY = 100;

    @Inject MapPresenter mPresenter;
    @Inject SharedPrefsHelper mSharePrefs;
    @Inject DataManager mDataMngr;
//    @Inject BTownFusedLocation fusedLocation;

    private FastPointOverlay mPointsOverlay;

    private MyLocationNewOverlay mLocationOverlay;

    private StolpersteineViewModel mViewModel;

    private boolean disableLookups;

    private Unbinder unbinder;

    @BindView(R.id.mapView)                 MapView mMap;
    @BindView(R.id.featureProgressDialog)   ProgressBar featureProgressDialog;
    @BindView(R.id.fabLocation)             FloatingActionButton fabLocation;
    @BindView(R.id.fabBack)                 FloatingActionButton fabBack;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        setContentView(R.layout.activity_map);
        unbinder = ButterKnife.bind(this);

        if (C.HAS_M && !mDataMngr.hasPermissions() ) {
            startActivity(new Intent(this, PermissionActivity.class));
            finish();
            return;
        }

        mViewModel = ViewModelProviders.of(this).get(StolpersteineViewModel.class);
//        fusedLocation.setCallback(locationCallbackListener);
        mPresenter.attachView(this);


        mPointsOverlay = new FastPointOverlay(getApplicationContext());


        Intent intent = getIntent();
        if(Commons.isNotNull(intent) && getIntent().hasExtra(BlockViewerActivity.BLOCK_DATA)) {
            disableLookups = true;

            Stolpersteine stolpersteine = intent.getParcelableExtra(BlockViewerActivity.BLOCK_DATA);
            mPresenter.initMap(mMap, stolpersteine);
            mMap.getOverlays().remove(mPointsOverlay);

            List<Stolpersteine> list = new ArrayList<>();
            list.add(stolpersteine);
            mPointsOverlay.addStones(mMap, list);
            fabBack.show();
            fabBack.setOnClickListener(v -> onBackPressed());

        } else {
            mPresenter.initMap(mMap, null);

            getRemoteData();
        }
    }

    private void getRemoteData() {
        // TODO: 10/06/2018 does live data work with empty database?
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                if(null == AppDatabase.getDatabase(getApplicationContext()).stolpersteineDao().getAnyBlock())
                    mPresenter.getStumblingBlocks(0);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        mDataMngr.setLastPosition((GeoPoint) mMap.getMapCenter());
        mDataMngr.setZoom((float) mMap.getZoomLevelDouble());
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }


    /******* VIEW CLICK EVENTS *****/

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        return false;
    }

    @Override
    public boolean longPressHelper(GeoPoint p) {
        return false;
    }


    @OnClick(R.id.fab_search)
    void onShowBlocks() {
        BlockListDialogFragment.showInstance(getApplicationContext(), mMap.getMapCenter());
    }

    @OnClick(R.id.fab_Settings)
    void onShowSettings() {
        startActivity(new Intent(this, SettingsActivity.class));
    }


    @OnClick(R.id.fab_ZoomIn)
    void onZoomInFab() {
        mMap.getController().zoomIn();
    }

    @OnClick(R.id.fab_ZoomOut)
    void onZoomOutFab() {
        mMap.getController().zoomOut();
    }

    @OnClick(R.id.fabLocation)
    void onFindLocation() {
//        translate();
        mPresenter.zoomToMyLocation(mLocationOverlay.getMyLocation());
    }


    String temp = "";

    void translate() {

        TranslateApi service = TranslateApiServiceProvider.get();


        String testString = "Hello from my application. Here is the second sentence. ";


        String[] split = testString.split("\\.", 450);

        for (int i = 0; i < split.length; i++) {
            if (!TextUtils.isEmpty(split[i]) && Character.isDigit(split[i].charAt(split[i].length() - 1))) {
                split[i] = split[i].concat(split[++i]);
                split[i] = "";

                if(i >= split.length)
                    break;
            }
        }

        for (String query : split) {

            if(!TextUtils.isEmpty(query)) {
                Call<ResultTranslate> call = service.translate(query, "de|en");
                call.enqueue(new Callback<ResultTranslate>() {

                    @SuppressWarnings({"ConstantConditions"})
                    @Override
                    public void onResponse(@NonNull Call<ResultTranslate> call, @NonNull Response<ResultTranslate> response) {

                        if (response.body() != null) {
                            Log.d(TAG, "onResponse: ");
                            ResponseTranslate translate = response.body().getResponseData();

                            temp = temp + translate.getTranslatedText() + "\n";
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResultTranslate> call, @NonNull Throwable t) {
                        Log.d(TAG, "onFailure: " + t.getMessage());
                        Commons.Toast(MapActivity.this, R.string.error_translate, Color.RED, Toast.LENGTH_LONG);
                    }
                });
            }
        }
    }








    /* ****** MVP METHODS **** */

    /**
     * Toggle the tracking FAB
     *
     * @param trackingMode true = follow my location, false = free map movement
     */
    @Override
    public void setTrackingMode(boolean trackingMode) {
        if (trackingMode) {
            ViewUtils.changeFabColour(this, fabLocation, R.color.colorAccent);
            ViewUtils.changeFabIcon(this, fabLocation, R.drawable.ic_compass);
            fabLocation.setKeepScreenOn(true);
            mLocationOverlay.enableFollowLocation();
        } else {
            ViewUtils.changeFabColour(this, fabLocation, R.color.fabColorAlmostClear);
            ViewUtils.changeFabIcon(this, fabLocation, R.drawable.ic_my_location);
            fabLocation.setKeepScreenOn(false);
            mLocationOverlay.disableFollowLocation();
        }
        fabLocation.setTag(trackingMode);
    }


    @Override
    public void enableLocationOverlay(boolean mode) {
        mLocationOverlay.setEnabled(mode);
    }

    /**
     * Define and add the map overlays
     */
    @Override
    public void addOverlays() {
        MapEventsOverlay eventsOverlay = new MapEventsOverlay(this);
        mLocationOverlay = new MyLocationNewOverlay(mMap);

        mMap.getOverlays().add(eventsOverlay);
        mMap.getOverlays().add(mLocationOverlay);

        mLocationOverlay.enableMyLocation();

        Bitmap bm = ImageUtils.drawableToBitmap(ContextCompat.getDrawable(this, R.drawable.ic_navigation_no_compass));
        mLocationOverlay.setPersonIcon(bm);
        mLocationOverlay.setPersonHotspot(24 * C.DENSITY * 0.5f, 24 * C.DENSITY * 0.5f);
    }

    @Override
    public void addBlocksToDB(final int offset, final ArrayList<Stolpersteine> list) {
        final StolpersteineDao dao = AppDatabase.getDatabase(getApplicationContext()).stolpersteineDao();
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                for (Stolpersteine item : list) {
                    Log.d(TAG, "insert to DB:: " + item.getPerson().getLastName());
                    dao.insert(item);
                }

                if (list.size() == MapPresenter.REQUEST_BATCH_SIZE)
                    mPresenter.getStumblingBlocks(offset + MapPresenter.REQUEST_BATCH_SIZE);
            }
        });
   }

    @Override
    public void mapMoved(GeoPoint geoPoint) {
        if(!disableLookups) {
            featureProgressDialog.setVisibility(View.VISIBLE);
            mViewModel.getAroundLocation(mMap.getBoundingBox()).observe(this, new Observer<List<Stolpersteine>>() {
                @Override
                public void onChanged(@Nullable List<Stolpersteine> stolpersteines) {
                    mMap.getOverlays().remove(mPointsOverlay);
                    mPointsOverlay.addStones(mMap, stolpersteines);
                    featureProgressDialog.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public void showError() {

    }
}
