package me.carc.stolpersteine.activities.map;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import me.carc.stolpersteine.activities.map.overlays.LabelledGeoPoint;
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
import me.carc.stolpersteine.fragments.BlockListDialogFragment;
import me.carc.stolpersteine.fragments.settings.SendFeedback;

/**
 * Main Screen used to show stolpersteine locaitons
 *
 * Created by bamptonm on 08/06/2018.
 */

@SuppressWarnings("ALL")
public class MapActivity extends MvpBaseActivity implements MapMvpView, MapEventsReceiver, FastPointOverlay.OnPointClicked {
    private static final String TAG = MapActivity.class.getName();

    @Inject MapPresenter mPresenter;
    @Inject SharedPrefsHelper mSharePrefs;
    @Inject DataManager mDataMngr;

    private FastPointOverlay mPointsOverlay;

    private MyLocationNewOverlay mLocationOverlay;

    private StolpersteineViewModel mViewModel;

    private boolean disableLookups;

    private Snackbar mSnackBar;

    private Unbinder unbinder;

    @BindView(R.id.fabBase)                 CoordinatorLayout fabBase;
    @BindView(R.id.mapView)                 MapView mMap;
    @BindView(R.id.featureProgressDialog)   ProgressBar featureProgressDialog;
    @BindView(R.id.fab_search)              FloatingActionButton fabSearch;
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
        mPresenter.attachView(this);

        mPointsOverlay = new FastPointOverlay(this);

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
        Executors.newSingleThreadExecutor().execute(() -> {
            if(null == AppDatabase.getDatabase(getApplicationContext()).stolpersteineDao().getAnyBlock()) {
                mPresenter.getStumblingBlocks(0);
                disableLookups = true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        new SendFeedback(this, SendFeedback.TYPE_RATE, SendFeedback.SESSION_COUNT);
        mLocationOverlay.enableMyLocation();
        mapMoved((GeoPoint) mMap.getMapCenter());
    }

    @Override
    protected void onPause() {
        mDataMngr.setLastPosition((GeoPoint) mMap.getMapCenter());
        mDataMngr.setZoom((float) mMap.getZoomLevelDouble());
        mLocationOverlay.disableMyLocation();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if(Commons.isNotNull(mSnackBar) && mSnackBar.isShown())
            mSnackBar.dismiss();
        else
            super.onBackPressed();
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
        BlockListDialogFragment.showInstance(getApplicationContext(), mMap.getBoundingBox());
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
        mPresenter.zoomToMyLocation(mLocationOverlay.getMyLocation());
    }



    /* ****** CALLBACK METHODS **** */


    @SuppressWarnings("Annotator")
    @Override
    public void onPointClicked(final LabelledGeoPoint point, final Stolpersteine element) {

        mSnackBar = Snackbar.make(fabBase, point.getLabel(), Snackbar.LENGTH_INDEFINITE);

        // Hide the Snackbar textview
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) mSnackBar.getView();
        TextView textView = layout.findViewById(android.support.design.R.id.snackbar_text);
        textView.setVisibility(View.INVISIBLE);

        View snackView = LayoutInflater.from(this).inflate(R.layout.custom_snackbar, layout, false);

        ((TextView)snackView.findViewById(R.id.snackFullName)).setText(point.getLabel());
        ((TextView)snackView.findViewById(R.id.snackAddress)).setText(point.getAddress());
        Button btn = snackView.findViewById(R.id.viewBtn);
        btn.setOnClickListener(v -> {
            mSnackBar.dismiss();
            Intent intent = new Intent(v.getContext(), BlockViewerActivity.class);
            intent.putExtra(BlockViewerActivity.BLOCK_DATA, element);
            v.getContext().startActivity(intent);
        });

        snackView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        layout.addView(snackView, 0);
        layout.setPadding(0,0,0,0);

        mSnackBar.show();
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

        // Set up the direciton and standing still map icons
        Bitmap person = ImageUtils.drawableToBitmap(ContextCompat.getDrawable(this, R.drawable.ic_navigation_no_compass));
        Bitmap direction = ImageUtils.drawableToBitmap(ContextCompat.getDrawable(this, R.drawable.ic_navigation));
        mLocationOverlay.setDirectionArrow(person, direction);
        mLocationOverlay.setPersonHotspot(24 * C.DENSITY * 0.5f, 24 * C.DENSITY * 0.5f);
    }

    @Override
    public void addBlocksToDB(final int offset, final ArrayList<Stolpersteine> list) {
        final StolpersteineDao dao = AppDatabase.getDatabase(getApplicationContext()).stolpersteineDao();
        Executors.newSingleThreadExecutor().execute(() -> {
            for (Stolpersteine item : list)
                dao.insert(item);

            if (list.size() == MapPresenter.REQUEST_BATCH_SIZE)
                mPresenter.getStumblingBlocks(offset + MapPresenter.REQUEST_BATCH_SIZE);
            else
                disableLookups = false;

            runOnUiThread(() -> {
                if(disableLookups) {
                    Commons.Toast(MapActivity.this, R.string.updating_database, Commons.RED, Toast.LENGTH_SHORT);
                    if(Commons.isNotNull(fabSearch)) fabSearch.setVisibility(View.INVISIBLE);
                } else {
                    Commons.Toast(MapActivity.this, R.string.updated_database, Commons.GREEN, Toast.LENGTH_LONG);
                    if(Commons.isNotNull(fabSearch))fabSearch.setVisibility(View.VISIBLE);
                }
            });
        });
   }

    @Override
    public void mapMoved(GeoPoint geoPoint) {
        if(Commons.isNotNull(mSnackBar))
            mSnackBar.dismiss();

        if(!disableLookups) {
            featureProgressDialog.setVisibility(View.VISIBLE);
            mViewModel.getAroundLocation(mMap.getBoundingBox()).observe(this, stolpersteines -> {
                mMap.getOverlays().remove(mPointsOverlay);
                mPointsOverlay.addStones(mMap, stolpersteines);
                featureProgressDialog.setVisibility(View.GONE);
            });
        }
    }

    @Override
    public void showError() {

    }
}
