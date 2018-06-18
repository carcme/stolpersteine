package me.carc.stolpersteine.activities.viewer;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.carc.stolpersteine.BuildConfig;
import me.carc.stolpersteine.R;
import me.carc.stolpersteine.activities.base.MvpBaseActivity;
import me.carc.stolpersteine.activities.map.MapActivity;
import me.carc.stolpersteine.activities.viewer.adapters.SectionsCard;
import me.carc.stolpersteine.activities.viewer.adapters.SectionsRecyclerAdapter;
import me.carc.stolpersteine.common.Commons;
import me.carc.stolpersteine.common.utils.AndroidUtils;
import me.carc.stolpersteine.common.utils.IntentUtils;
import me.carc.stolpersteine.common.utils.MapUtils;
import me.carc.stolpersteine.common.utils.ViewUtils;
import me.carc.stolpersteine.data.DataManager;
import me.carc.stolpersteine.data.SharedPrefsHelper;
import me.carc.stolpersteine.data.db.blocks.StolpersteineViewModel;
import me.carc.stolpersteine.data.remote.bio.BioImages;
import me.carc.stolpersteine.data.remote.bio.Biography;
import me.carc.stolpersteine.data.remote.bio.LinksParser;
import me.carc.stolpersteine.data.remote.model.Coordinates;
import me.carc.stolpersteine.data.remote.model.Stolpersteine;
import me.carc.stolpersteine.data.translate.ResponseTranslate;
import me.carc.stolpersteine.data.translate.ResultTranslate;
import me.carc.stolpersteine.data.translate.TranslateApi;
import me.carc.stolpersteine.data.translate.TranslateApiServiceProvider;
import me.carc.stolpersteine.fragments.ImageDialog;
import me.carc.stolpersteine.interfaces.RecyclerClickListener;
import me.carc.stolpersteine.interfaces.RecyclerTouchListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.deanwild.flowtextview.FlowTextView;


public class BlockViewerActivity extends MvpBaseActivity implements BlockViewerMvpView, View.OnClickListener {
    private static final String TAG = BlockViewerActivity.class.getName();

    private static final String BASE_URL = "http://www.stolpersteine-berlin.de/";

    public static final int RESULT_RELATED = 111;
    public static final String BLOCK_DATA = "BLOCK_DATA";
    public static final String BIO_DATA = "BIO_DATA";

    private static final int GOOGLE_TRANSLATE = 0;
    private static final int CLOUD_TRANSLATE = 1;



    // EN
    public static final String INFO_LOCATION = "LOCATION";
    public static final String INFO_DISTRICT = "DISTRICT";
    public static final String INFO_STONE = "STONE WAS LAID";
    public static final String INFO_BORN = "BORN";
    public static final String INFO_OCCUPATION = "OCCUPATION";
    public static final String INFO_DEPORTATION = "DEPORTATION";
    public static final String INFO_MURDERED = "MURDERED";
    public static final String INFO_DEAD = "DEAD";

    //DE
    public static final String INFO_LOCATION_DE = "VERLEGEORT";
    public static final String INFO_DISTRICT_DE = "BEZIRK/ORTSTEIL";
    public static final String INFO_STONE_DE = "VERLEGEDATUM";
    public static final String INFO_BORN_DE = "GEBOREN";
    public static final String INFO_OCCUPATION_DE = "BERUF";
    public static final String INFO_DEPORTATION_DE = "DEPORTATION";
    public static final String INFO_MURDERED_DE = "ERMORDET";
    public static final String INFO_DEAD_DE = "TOT";

    @Inject BlockViewerPresenter mPresenter;
    @Inject SharedPrefsHelper mSharePrefs;
    @Inject DataManager mDataMngr;

    private Stolpersteine mStolpersteine;
    private Biography mBiography;
    private Unbinder unbinder;

    @BindView(R.id.viewerNestedScrollView) NestedScrollView viewerNestedScrollView;
    @BindView(R.id.addressLayout) LinearLayout addressLayout;
    @BindView(R.id.addressMap) ImageView addressMap;
    @BindView(R.id.address) TextView address;

    @BindView(R.id.translateBtn) ImageButton translateBtn;

    @BindView(R.id.born) TextView born;
    @BindView(R.id.occupation) TextView occupation;
    @BindView(R.id.deported) TextView deported;
    @BindView(R.id.murdered) TextView murdered;


    @BindView(R.id.flowTextContainer) LinearLayout flowTextContainer;
    @BindView(R.id.flowTextView) FlowTextView flowTextView;


//    @BindViews({ R.id.image1, R.id.image2, R.id.image3})
    List<ImageView> imageViews;

    @BindView(R.id.sectionsRecyclerView) RecyclerView sectionsRecyclerView;


    @BindViews({ R.id.born, R.id.occupation, R.id.deported, R.id.murdered})
    List<TextView> infoViews;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.fabViewer) FloatingActionButton fabViewer;


    private NestedScrollView.OnScrollChangeListener onScrollListener = new NestedScrollView.OnScrollChangeListener() {
        @Override
        public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            if (scrollY - oldScrollY > 0)
                fabViewer.hide();
            else if (scrollY - oldScrollY < 0)
                fabViewer.show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        setContentView(R.layout.activity_block_viewer);
        unbinder = ButterKnife.bind(this);

        mPresenter.attachView(this);
        viewerNestedScrollView.setOnScrollChangeListener(onScrollListener);

        Intent intent = getIntent();
        if (Commons.isNotNull(intent) && intent.hasExtra(BLOCK_DATA)) {

            mStolpersteine = intent.getParcelableExtra(BLOCK_DATA);
            // have data
            address.setText(mStolpersteine.getLocation().buildVerticalAddress());
            toolbar.setTitle(mStolpersteine.getPerson().getFullName());

            // get data
            if(Commons.isNull(savedInstanceState))
                mPresenter.getBioData(mStolpersteine.getPerson().getBiographyUrl());
            else
                onHtmlParsed(savedInstanceState.getParcelable(BIO_DATA));

            // get map image
            generateMap(mStolpersteine.getLocation().getCoordinates());

            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> exit());

        } else
            finish();
    }

/*
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_block_viewer, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_translate:
                Toast.makeText(getApplicationContext(), "About menu item pressed", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
*/


        @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_RELATED:
                fabViewer.show();
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BIO_DATA, mBiography);
    }


    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

    private void generateMap(Coordinates location) {

        final double dLat = location.getLatitude();
        final double dLon = location.getLongitude();

        ViewTreeObserver viewTreeObserver = addressLayout.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    addressLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int viewWidth = (int) ViewUtils.dpFromPx(BlockViewerActivity.this, addressLayout.getWidth());
                    int viewHeight = (int) ViewUtils.dpFromPx(BlockViewerActivity.this, addressLayout.getHeight());

                    String size = viewWidth + "x" + viewHeight;

                    String url = MapUtils.buildStaticOsmMapImageMarkerRight(dLat, dLon, size, 16);

                    // EG: http://staticmap.openstreetmap.de/staticmap.php?center=52.523121068264295,13.297143505009352&zoom=16&size=360x230&maptype=mapnik&markers=52.523121068264295,13.299143505009352,red-pushpin
                    Glide.with(BlockViewerActivity.this)
                            .load(Uri.parse(url))
                            .into(addressMap);
                }
            });
        }
    }


    @OnClick({R.id.addressMap, R.id.venueMapOverlay, R.id.address, R.id.addressContainer})
    void showMap() {
        Intent mapIntent = new Intent(this, MapActivity.class);
        mapIntent.putExtra(BLOCK_DATA, (Parcelable) mStolpersteine);
        startActivity(mapIntent);
    }


    @OnClick(R.id.viewerDirections)
    void route() {
        if (Commons.isNotNull(mStolpersteine)) {
            try {
                Coordinates ords = mStolpersteine.getLocation().getCoordinates();
                Intent intent = IntentUtils.sendGeoIntent(ords.getLatitude(), ords.getLongitude(), mStolpersteine.getPerson().getFullName());
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, R.string.error_no_maps_app, Toast.LENGTH_SHORT).show();
            }
        }
    }


    @OnClick(R.id.viewerShare)
    void share() {
        try {
            if (Commons.isNotNull(mStolpersteine)) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, mStolpersteine.getPerson().getFullName());
                sendIntent.putExtra(Intent.EXTRA_TEXT, mStolpersteine.getPerson().getBiographyUrl());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getText(R.string.shared_string_send_to)));
            }
        } catch (NullPointerException e) {
            Log.d(TAG, "setupDialog ERROR: " + e.getLocalizedMessage());
        }
    }

    List<String> listTranslations = new ArrayList<>();
    int indexTranslation = 0;
    int retyCount = 0;

    @OnClick(R.id.translateBtn)
    void translate() {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setIcon(R.drawable.ic_translate);
        builderSingle.setTitle("Translate using:");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add("Use Google Translate");
        arrayAdapter.add("Try Online Translate");

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case GOOGLE_TRANSLATE:

                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setPackage("com.google.android.apps.translate");

                        Uri uri = new Uri.Builder()
                                .scheme("http")
                                .authority("translate.google.com")
                                .path("/m/translate")
                                .appendQueryParameter("q", mBiography.getBiographyText())
                                .appendQueryParameter("tl", "en") // target language
                                .appendQueryParameter("sl", "de") // source language
                                .build();
                        //intent.setType("text/plain"); //not needed, but possible
                        intent.setData(uri);
                        startActivity(intent);

                        break;
                    case CLOUD_TRANSLATE:
                        onlineTranlate();
                        break;
                }
                dialog.dismiss();
            }
        });
        builderSingle.show();
    }

    private void onlineTranlate() {
        flowTextView.setText("");

        String[] split = mBiography.getBiographyText().split("\\.", 450);

        for (int i = 0; i < split.length; i++) {
            if (!TextUtils.isEmpty(split[i]) && Character.isDigit(split[i].charAt(split[i].length() - 1))) {
                listTranslations.add(split[i].concat(split[++i]).concat("."));
                split[i] = "";

                if(i >= split.length)
                    break;
            } else if (!TextUtils.isEmpty(split[i])) {
                listTranslations.add(split[i].concat("."));
            }
        }

        lineTranslate();

    }

    private void lineTranslate() {
        if(indexTranslation < listTranslations.size())
            translate(listTranslations.get(indexTranslation));
        else
            mBiography.setBiographyHtml(flowTextView.getText().toString());
    }

    private void translate(String query) {
        TranslateApi service = TranslateApiServiceProvider.get();
        Call<ResultTranslate> call = service.translate(query, "de|en");
        call.enqueue(new Callback<ResultTranslate>() {

            @SuppressWarnings({"ConstantConditions"})
            @Override
            public void onResponse(@NonNull Call<ResultTranslate> call, @NonNull Response<ResultTranslate> response) {

                if (response.body() != null) {
                    Log.d(TAG, "onResponse: ");
                    ResponseTranslate translate = response.body().getResponseData();

                    String html = "<p>" + translate.getTranslatedText() + "</p>";

                    flowTextView.setText(Html.fromHtml(flowTextView.getText() + html ));
                    indexTranslation++;
                    retyCount = 0;
                    lineTranslate();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResultTranslate> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());

                if(retyCount < 3) {
                    retyCount++;
                    lineTranslate();  // retry the same line
                }
            }
        });
    }

    @OnClick(R.id.fabViewer)
    void exit() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        ViewUtils.createAlphaAnimator(fabViewer, false, getResources()
                .getInteger(R.integer.gallery_alpha_duration) * 2)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        setResult(RESULT_OK, getIntent());
                        finish();
                    }
                }).start();
    }

    @Override
    public void onClick(View v) {
        BioImages images;

        switch (v.getId()) {
            case R.id.image1:
                images = mBiography.getImagesList().get(0);
                ImageDialog.showInstance(getApplication(), images.getPublicImage(), images.getBigImage(),
                        mStolpersteine.getPerson().getFullName(), null);
                break;
            case R.id.image2:
                images = mBiography.getImagesList().get(1);
                ImageDialog.showInstance(getApplication(), images.getPublicImage(), images.getBigImage(),
                        mStolpersteine.getPerson().getFullName(), null);
                break;
            case R.id.image3:
                images = mBiography.getImagesList().get(2);
                ImageDialog.showInstance(getApplication(), images.getPublicImage(), images.getBigImage(),
                        mStolpersteine.getPerson().getFullName(), null);
                break;
            default:
        }
    }


    /* ****** MVP METHODS **** */

    @Override
    public void onHtmlParsed(Biography bio) {

        mBiography = bio;

        runOnUiThread(() -> {
            Iterator myVeryOwnIterator = bio.getInfo().keySet().iterator();
            while (myVeryOwnIterator.hasNext()) {
                String key = (String) myVeryOwnIterator.next();
                String value = key.concat(": ").concat(bio.getInfo().get(key));

                switch (key) {
                    case INFO_LOCATION:
                    case INFO_LOCATION_DE:
                        break;

                    case INFO_DISTRICT:
                    case INFO_DISTRICT_DE:
                        break;

                    case INFO_STONE:
                    case INFO_STONE_DE:
                        break;

                    case INFO_BORN:
                    case INFO_BORN_DE:
                        born.setVisibility(View.VISIBLE);
                        born.setText(value);
                        break;

                    case INFO_OCCUPATION:
                    case INFO_OCCUPATION_DE:
                        occupation.setVisibility(View.VISIBLE);
                        occupation.setText(value);
                        break;

                    case INFO_DEPORTATION:
                        deported.setVisibility(View.VISIBLE);
                        deported.setText(value);
                        break;

                    case INFO_MURDERED:
                    case INFO_MURDERED_DE:
                    case INFO_DEAD:
                    case INFO_DEAD_DE:
                        murdered.setVisibility(View.VISIBLE);
                        murdered.setText(value);
                        break;

                    default:
                        if(BuildConfig.DEBUG)
                            Toast.makeText(BlockViewerActivity.this, "Not handled: " + key, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onHtmlParsed: Not handled: " + key + " : " + value);
                }
            }
            // Hide unused info views
            for (TextView v : infoViews) {
                if (TextUtils.isEmpty(v.getText()))
                    v.setVisibility(View.GONE);
            }

            boolean hasText = false;
            imageViews = new ArrayList<>();

            if(!TextUtils.isEmpty(bio.getBiographyHtml())) {
                hasText = true;
                flowTextView.setText(Html.fromHtml(bio.getBiographyHtml()));
                imageViews.add((ImageView) findViewById(R.id.image1));
                imageViews.add((ImageView) findViewById(R.id.image2));
                imageViews.add((ImageView) findViewById(R.id.image3));

                final ClipboardManager clipboardManager = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                final ClipData clipData = ClipData.newPlainText(mStolpersteine.getPerson().getFullName(), bio.getBiographyText());
                assert clipboardManager != null;
                clipboardManager.setPrimaryClip(clipData);

            } else if (bio.getImagesList() != null) {

                translateBtn.setVisibility(View.GONE);

                flowTextContainer.removeAllViews();

                LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

                int padding = AndroidUtils.getPixelsFromDPs(getResources(), 16);
                imageParams.setMargins(padding, padding / 2, padding, padding / 2);

                for (int i = 0; i < bio.getImagesList().size(); i++) {
                    final ImageView imageView = new ImageView(BlockViewerActivity.this);

                    if(i== 0)
                        imageView.setId(R.id.image1);
                    else if(i == 1)
                        imageView.setId(R.id.image2);
                    else if(i == 2)
                        imageView.setId(R.id.image3);
                    imageView.setLayoutParams(imageParams);
                    imageViews.add(imageView);
                    flowTextContainer.addView(imageView);
                }
            } else {

                // not much to display :/
                translateBtn.setVisibility(View.GONE);
            }

            if (bio.getImagesList() != null) {

                for (int i = 0; i < imageViews.size(); i++) {
                    if (bio.getImagesList().size() > i) {
                        Glide.with(BlockViewerActivity.this)
                                .load(hasText ? bio.getImagesList().get(i).getThumbnail() : bio.getImagesList().get(i).getPublicImage())
                                .into(imageViews.get(i));

                        imageViews.get(i).setOnClickListener(BlockViewerActivity.this);
                        imageViews.get(i).setVisibility(View.VISIBLE);
                    }
                }
            }

            if (bio.getSections().size() > 0) {
                new SetupMore(bio.getSections()).run();
            }
        });

        // Update the database on this Thread (not main)
        if(bio.getImagesList() != null)
            updateWithImages(bio.getImagesList());
    }

    private class SetupMore implements Runnable {
        List<LinksParser.Section> sections;

        SetupMore(List<LinksParser.Section> sections) {
            this.sections = sections;
        }

        @Override
        public void run() {
            populateMore();
        }

        private void populateMore() {
            final ArrayList<SectionsCard> items = new ArrayList<>();

            for (LinksParser.Section section : sections ) {

                assert section.getText().size() == section.getLink().size();

                items.add(new SectionsCard(section.getType(), SectionsCard.ItemType.NONE, R.drawable.ic_block_attribution)); // Add the title

                for (int i = 0; i < section.getLink().size(); i++) {
                    // Add the elements
                    items.add(new SectionsCard(section.getText().get(i), section.getLink().get(i), SectionsCard.ItemType.WEB));
                }
            }

            if (items.isEmpty()) {
                sectionsRecyclerView.setVisibility(View.GONE);
            } else {

                final SectionsRecyclerAdapter mAdapter = new SectionsRecyclerAdapter(items);

                sectionsRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(BlockViewerActivity.this, sectionsRecyclerView, new RecyclerClickListener() {

                    @Override
                    public void onClick(View view, int pos) {
                        final SectionsCard item = items.get(pos);
                        Intent intent;

                        switch (item.getType()) {
                            case WEB :
                                String url = item.getData();
                                ///en/biografie/824
                                if(item.getData().startsWith("/en/"))
                                    url = BASE_URL.concat(url.replace("/en/", ""));

                                StolpersteineViewModel mViewModel;
                                mViewModel = ViewModelProviders.of(BlockViewerActivity.this).get(StolpersteineViewModel.class);
                                final LiveData<Stolpersteine> data = mViewModel.getBlock(url);
                                data.observe(BlockViewerActivity.this, stolpersteine -> {
                                    data.removeObservers(BlockViewerActivity.this);

                                    if(Commons.isNotNull(stolpersteine)) {

                                        Intent i = new Intent(BlockViewerActivity.this, BlockViewerActivity.class);
                                        i.putExtra(BlockViewerActivity.BLOCK_DATA, stolpersteine);
                                        startActivityForResult(i, RESULT_RELATED);
                                    } else
                                        Toast.makeText(BlockViewerActivity.this, "DB read error", Toast.LENGTH_SHORT).show();
                                });
                                break;

                            case CLIPBOARD:
                                AndroidUtils.copyToClipboard(BlockViewerActivity.this, item.getData());
                                Toast.makeText(BlockViewerActivity.this, "Copied to clipboard:\n" + item.getData(), Toast.LENGTH_SHORT).show();
                                break;

                            default:
                        }
                    }

                    @Override
                    public void onLongClick(View view, int position) { /* IGNORED */}
                }));

                sectionsRecyclerView.setNestedScrollingEnabled(true);
                LinearLayoutManager layoutManager = new LinearLayoutManager(BlockViewerActivity.this);
                sectionsRecyclerView.setLayoutManager(layoutManager);
                sectionsRecyclerView.setAdapter(mAdapter);
            }
        }
    }

    private void updateWithImages(List<BioImages> images) {
        mStolpersteine.setImages(images.get(0));
        StolpersteineViewModel mViewModel = ViewModelProviders.of(this).get(StolpersteineViewModel.class);
        mViewModel.insert(mStolpersteine);
    }
}
