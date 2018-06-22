package me.carc.stolpersteine.activities.viewer;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.carc.stolpersteine.App;
import me.carc.stolpersteine.BuildConfig;
import me.carc.stolpersteine.R;
import me.carc.stolpersteine.activities.base.MvpBaseActivity;
import me.carc.stolpersteine.activities.map.MapActivity;
import me.carc.stolpersteine.activities.viewer.adapters.SectionsCard;
import me.carc.stolpersteine.activities.viewer.adapters.SectionsRecyclerAdapter;
import me.carc.stolpersteine.common.C;
import me.carc.stolpersteine.common.Commons;
import me.carc.stolpersteine.common.utils.AndroidUtils;
import me.carc.stolpersteine.common.utils.IntentUtils;
import me.carc.stolpersteine.common.utils.MapUtils;
import me.carc.stolpersteine.common.utils.ViewUtils;
import me.carc.stolpersteine.common.views.flowtextview.FlowTextView;
import me.carc.stolpersteine.common.views.flowtextview.listeners.OnLinkClickListener;
import me.carc.stolpersteine.data.DataManager;
import me.carc.stolpersteine.data.SharedPrefsHelper;
import me.carc.stolpersteine.data.db.blocks.StolpersteineViewModel;
import me.carc.stolpersteine.data.remote.bio.BioImages;
import me.carc.stolpersteine.data.remote.bio.Biography;
import me.carc.stolpersteine.data.remote.bio.Section;
import me.carc.stolpersteine.data.remote.model.Coordinates;
import me.carc.stolpersteine.data.remote.model.Stolpersteine;
import me.carc.stolpersteine.data.translate.ResponseTranslate;
import me.carc.stolpersteine.data.translate.ResultTranslate;
import me.carc.stolpersteine.data.translate.TranslateApi;
import me.carc.stolpersteine.data.translate.TranslateApiServiceProvider;
import me.carc.stolpersteine.data.translateGoogle.GoogleApi;
import me.carc.stolpersteine.data.translateGoogle.GoogleApiServiceProvider;
import me.carc.stolpersteine.fragments.ImageDialog;
import me.carc.stolpersteine.interfaces.RecyclerClickListener;
import me.carc.stolpersteine.interfaces.RecyclerTouchListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BlockViewerActivity extends MvpBaseActivity implements BlockViewerMvpView, View.OnClickListener {
    private static final String TAG = BlockViewerActivity.class.getName();

    private static final String BASE_URL = "http://www.stolpersteine-berlin.de/";

    public static final int RESULT_RELATED = 111;
    public static final int RESULT_GOOGLE_TRANSLATE = 112;
    public static final String BLOCK_DATA = "BLOCK_DATA";
    public static final String BIO_DATA = "BIO_DATA";

    // EN
    public static final String INFO_LOCATION = "LOCATION";
    public static final String INFO_DISTRICT = "DISTRICT";
    public static final String INFO_STONE = "STONE WAS LAID";
    public static final String INFO_BORN = "BORN";
    public static final String INFO_OCCUPATION = "OCCUPATION";
    public static final String INFO_DEPORTATION = "DEPORTATION";
    public static final String INFO_LATER_DEPORTATION = "LATER DEPORTED";
    public static final String INFO_MURDERED = "MURDERED";
    public static final String INFO_DEAD = "DEAD";
    public static final String INFO_DIED = "DIED";
    public static final String INFO_ESCAPE = "ESCAPE";


    //DE
    public static final String INFO_LOCATION_DE = "VERLEGEORT";
    public static final String INFO_DISTRICT_DE = "BEZIRK/ORTSTEIL";
    public static final String INFO_STONE_DE = "VERLEGEDATUM";
    public static final String INFO_BORN_DE = "GEBOREN";
    public static final String INFO_OCCUPATION_DE = "BERUF";
    public static final String INFO_DEPORTATION_DE = "DEPORTATION";
    public static final String INFO_LATER_DEPORTATION_DE = "WEITERE DEPORTATION";
    public static final String INFO_MURDERED_DE = "ERMORDET";
    public static final String INFO_DEAD_DE = "TOT";
    public static final String INFO_DIED_DE = "GESTORBEN";
    public static final String INFO_ESCAPE_DE = "FLUCHT";



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

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.toolbarTitle) TextView toolbarTitle;
    @BindView(R.id.translateBtn) ImageButton translateBtn;

    @BindView(R.id.born) TextView born;
    @BindView(R.id.occupation) TextView occupation;
    @BindView(R.id.deported) TextView deported;
    @BindView(R.id.murdered) TextView murdered;

    @BindView(R.id.flowTextContainer) LinearLayout flowTextContainer;
    @BindView(R.id.flowTextView) FlowTextView flowTextView;

    private List<ImageView> imageViews;

    @BindView(R.id.sectionsRecyclerView) RecyclerView sectionsRecyclerView;

    @BindViews({ R.id.born, R.id.occupation, R.id.deported, R.id.murdered}) List<TextView> infoViews;

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
            toolbarTitle.setText(mStolpersteine.getPerson().getFullName());

            // get data
            if(Commons.isNull(savedInstanceState))
                mPresenter.getBioData(mStolpersteine.getPerson().getBiographyUrl());
            else
                onHtmlParsed(savedInstanceState.getParcelable(BIO_DATA));

            // get map image
            generateMap(mStolpersteine.getLocation().getCoordinates());

            setSupportActionBar(toolbar);
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

            case RESULT_GOOGLE_TRANSLATE:
                Log.d(TAG, "onActivityResult: ");
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
        mapIntent.putExtra(BLOCK_DATA, mStolpersteine);
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
                String text = mStolpersteine.getPerson().getFullName().concat(" - ")
                        .concat(mStolpersteine.getPerson().getBiographyUrl()).concat("\n")
                        .concat(String.format(getString(R.string.play_store_link), getPackageName()));

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
                sendIntent.putExtra(Intent.EXTRA_TEXT, text);
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
    boolean addFormat = false;
    String mTranslatedText = "";

    @OnClick(R.id.translateBtn)
    void translate() {

        final PackageManager pm = getPackageManager();
        boolean hasTranslator = pm.getLaunchIntentForPackage("com.google.android.apps.translate") != null;

        if(hasTranslator && mDataMngr.useGoogleTranslate()) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.apps.translate");

            Uri uri = new Uri.Builder()
                    .scheme("http")
                    .authority("translate.google.com")
                    .path("/m/translate")
                    .appendQueryParameter("q", mBiography.getBiographyText())
                    .appendQueryParameter("tl", C.USER_LANGUAGE) // target language
                    .appendQueryParameter("sl", "de") // source language
                    .build();
            intent.setData(uri);
            startActivity(intent);

        } else if(((App)getApplication()).isNetworkAvailable()) {
            onlineTranlate();

        } else {

            AlertDialog.Builder bldr = new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_translate)
                    .setTitle(R.string.translateTitle)
                    .setMessage(R.string.translateMessage)
                    .setPositiveButton(R.string.translateInstallGoogle, (dlg, which) -> {
                       startActivity(IntentUtils.openPlayStore(BlockViewerActivity.this, "com.google.android.apps.translate"));
                        dlg.dismiss();
                    })
                    .setNegativeButton(R.string.translateEnableData, (dlg, which ) -> {
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$DataUsageSummaryActivity"));
                        startActivity(intent);
                        dlg.dismiss();
                    });

            bldr.show();
        }
    }

    private void onlineTranlate() {
        if(mDataMngr.isMyMemoryQuotaUsed())
            googleTranslate(mBiography.getBiographyHtml());
        else
            memoryTranslate(mBiography.getBiographyText());
    }

    private void memoryTranslate(String query) {
        flowTextView.setText("");
        splitHtml();

/*
        String[] split = query.split("\\.", 450);

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
*/
    }

    private void googleTranslate(String query) {
        flowTextView.setText("");

        if(query.startsWith("<div"))
            query = query.substring(query.indexOf("<p>"));

        query = query.replaceAll("</div>", "");

        while(query.contains(" title=\"")) {
            int startIndex = query.indexOf(" title=\"");
            int endIndex = query.indexOf(">", startIndex);
            query = query.replace(query.substring(startIndex, endIndex), "");
        }

        googleTranslateService(query);
    }

    private void googleTranslateService(String query) {
        GoogleApi service = GoogleApiServiceProvider.get();
        Call<JsonArray> call = service.translate(query, "en", "de");
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (response.body() != null) {
                    JsonArray array1 = response.body().get(0).getAsJsonArray();

                    String translatedText = "";
                    for (int i = 0; i < array1.size(); i++) {
                        String str = array1.get(i).getAsJsonArray().get(0).getAsString();
                        str = str.replaceAll(" \\+", "");

                        if (str.contains(" href=\"/de/glossar"))
                            str = str.replace("href=\"/de/glossar", "href=\"https://www.stolpersteine-berlin.de/de/glossar");
                        else if (str.contains(" href=\"/en/glossar")) {
                            str = str.replace("href=\"/en/glossar", "href=\"https://www.stolpersteine-berlin.de/de/glossar");

                            // TODO: 22/06/2018 get the englsih translations - its not a direct lang replacement :/
/*                            String hashValue = Commons.capitalise(str.substring(str.indexOf("\">") + 2, str.indexOf("</a>")).trim());
                            int replaceStart = str.indexOf("href=\"");
                            int replaceEnd = str.indexOf("\">");
                            str = str.replace(str.substring(replaceStart, replaceEnd), "href=\"https://www.stolpersteine-berlin.de/en/glossar#" + hashValue);
*/                      }

                        translatedText = translatedText.concat(str);
                    }
                    flowTextView.setText(Html.fromHtml(translatedText));
                    mBiography.setBiographyHtml(translatedText);

                } else {
                    flowTextView.setText(mBiography.getBiographyHtml());
                    Commons.Toast(BlockViewerActivity.this, R.string.error_translate, Commons.RED, Toast.LENGTH_LONG);
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                flowTextView.setText(mBiography.getBiographyHtml());
                Commons.Toast(BlockViewerActivity.this, R.string.error_translate, Commons.RED, Toast.LENGTH_LONG);
            }
        });
    }

    private void splitHtml() {

        String[] split = mBiography.getBiographyHtml().split("</p>", 450);

        for (int i = 0; i < split.length; i++) {

            if(split[i].startsWith("<div"))
                split[i] = split[i].substring(split[i].indexOf("<p>"));

            if(split[i].startsWith("Mit "))
                Log.d(TAG, "REMOVE ME: ");


            if(split[i].contains("</div>"))
                continue;

            while(split[i].contains(" title=\"")) {
                int startIndex = split[i].indexOf(" title=\"");
                int endIndex = split[i].indexOf(">", startIndex);
                split[i] = split[i].replace(split[i].substring(startIndex, endIndex), "");
            }

            String[] tooLong;
            if(split[i].length() > 480) {  // 500 char limit on MyMemory
                tooLong = split[i].split("\\. ");   // find end of line
                for (int j = 0; j < tooLong.length; j++) {
                    if (!TextUtils.isEmpty(tooLong[j]) && Character.isDigit(tooLong[j].charAt(tooLong[j].length() - 1))) {
                        listTranslations.add(tooLong[j].concat(tooLong[++j]).concat("."));
                        tooLong[j] = "";
                    } else {
                        if(tooLong[j].length() > 480) {
                            String[] gettingSilly = tooLong[j].split("\\?");
                            for (String str : gettingSilly) {
                                listTranslations.add(str.concat("?"));
                            }
                        } else
                            listTranslations.add(tooLong[j].concat("."));
                    }

                }
            } else
                listTranslations.add(split[i]);
        }

        lineTranslate();
    }

    private void lineTranslate() {
        if(indexTranslation < listTranslations.size())
            translate(listTranslations.get(indexTranslation));
        else
            mBiography.setBiographyHtml(mTranslatedText);
    }

    private void translate(String query) {
        if(query.contains("<p>")) {
            addFormat = true;

            try {
                query = query.substring(query.indexOf("<p>") + 3);
            }catch (StringIndexOutOfBoundsException e) {
                //skip this line
                mTranslatedText = mTranslatedText.concat(getString(R.string.error_missing_translation));
                indexTranslation++;
                retyCount = 0;
                addFormat = false;
                lineTranslate();
                return;
            }
        }
        TranslateApi service = TranslateApiServiceProvider.get();
        Call<ResultTranslate> call = service.translate(query, "de|en");
        call.enqueue(new Callback<ResultTranslate>() {

            @SuppressWarnings({"ConstantConditions"})
            @Override
            public void onResponse(@NonNull Call<ResultTranslate> call, @NonNull Response<ResultTranslate> response) {
                if (response.body() != null) {

                    if (response.body().isQuotaFinished()) {
                        mDataMngr.setMyMemoryQuotaUsed(System.currentTimeMillis() + C.TIME_12_HOUR);
                        googleTranslate(mBiography.getBiographyHtml());
                        return;
                    }

                    ResponseTranslate translate = response.body().getResponseData();

                    String str;
                    if (addFormat)
                        str = "<p>" + translate.getTranslatedText() + "</p>";
                    else
                        str = translate.getTranslatedText();


                    if (str.contains(" href=\"/de/glossar"))
                        str = str.replace("href=\"/de/glossar", "href=\"https://www.stolpersteine-berlin.de/de/glossar");
                    else if (str.contains(" href=\"/en/glossar")) {
                        str = str.replace("href=\"/en/glossar", "href=\"https://www.stolpersteine-berlin.de/de/glossar");
                    }

                    // catch some conversion errors
                    str = str.replaceAll("&lt;", "<");
                    str = str.replaceAll("&gt;", ">");

                    if (Commons.isNotNull(str)) {
                        mTranslatedText = mTranslatedText.concat(str);
                        flowTextView.setText(Html.fromHtml(mTranslatedText));
                    }
                    indexTranslation++;
                    retyCount = 0;
                    addFormat = false;
                    lineTranslate();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResultTranslate> call, @NonNull Throwable t) {
                if (retyCount < 5) {
                    retyCount++;
                    lineTranslate();  // retry the same line
                } else
                    Commons.Toast(BlockViewerActivity.this, R.string.error_translate, Commons.RED, Toast.LENGTH_LONG);
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
                .withEndAction(() -> {
                    setResult(RESULT_OK, getIntent());
                    finish();
                }).start();
    }

    @Override
    public void onClick(View v) {
        BioImages images = null;

        switch (v.getId()) {
            case R.id.image1:
                images = mBiography.getImagesList().get(0);
                break;
            case R.id.image2:
                images = mBiography.getImagesList().get(1);
                break;
            case R.id.image3:
                images = mBiography.getImagesList().get(2);
                break;
            case R.id.image4:
                images = mBiography.getImagesList().get(3);
                break;
            case R.id.image5:
                images = mBiography.getImagesList().get(4);
                break;
            default:
        }

        if(Commons.isNotNull(images))
            ImageDialog.showInstance(getApplication(), images.getPublicImage(), images.getBigImage(),
                    mStolpersteine.getPerson().getFullName(), buildSubTitle());
    }

    private String buildSubTitle() {

        String subtitle = born.getText().toString();

        if(subtitle.isEmpty())
            subtitle = murdered.getText().toString();
        else
            subtitle = subtitle.concat("\n").concat(murdered.getText().toString());


        return subtitle;
    }

    private OnLinkClickListener linkListener = new OnLinkClickListener() {
        @Override
        public void onLinkClick(String url) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        }
    };

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "onLinkClick: ");
        }
    };

    /* ****** MVP METHODS **** */


    @Override
    public void onHtmlParsed(Biography bio) {

        mBiography = bio;

        runOnUiThread(() -> {
            Iterator myVeryOwnIterator = bio.getInfo().keySet().iterator();
            while (myVeryOwnIterator.hasNext()) {
                String key = (String) myVeryOwnIterator.next();
                String value = key.toLowerCase().concat(": ").concat(bio.getInfo().get(key));

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

                    case INFO_LATER_DEPORTATION:
                    case INFO_LATER_DEPORTATION_DE:
                        break;

                    case INFO_MURDERED:
                    case INFO_MURDERED_DE:
                    case INFO_DEAD:
                    case INFO_DEAD_DE:
                    case INFO_DIED:
                    case INFO_DIED_DE:
                        murdered.setVisibility(View.VISIBLE);
                        murdered.setText(value);
                        break;

                    case INFO_ESCAPE:
                    case INFO_ESCAPE_DE:
                        break;

                    default:
                        if(BuildConfig.DEBUG)
                            Toast.makeText(BlockViewerActivity.this, "Not handled: " + key, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onHtmlParsed: Not handled: " + key + " : " + value);
                        Log.d(TAG, "onHtmlParsed: Link: " + mStolpersteine.getPerson().getBiographyUrl());
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
                String text = bio.getBiographyHtml().replaceAll("<a href=\"/de/glossar#", "<a href=\"http://www.stolpersteine-berlin.de/de/glossar#");

                // check and replace EN links while here
                if(text.contains("href=\"/en/glossar"))
                    text = text.replaceAll("<a href=\"/en/glossar#", "<a href=\"http://www.stolpersteine-berlin.de/en/glossar#");

                flowTextView.setText(Html.fromHtml(text));

//                flowTextView.setClickable(true);
                flowTextView.setOnLinkClickListener(linkListener);
                flowTextView.setOnClickListener(clickListener);

                // TODO: 22/06/2018 make links work - change color so hidden for now
//                TextPaint p = flowTextView.getTextPaint();
//                p.linkColor = Color.BLACK;
//                flowTextView.setLinkPaint(p);

                // Can make this dynamic at some point
                imageViews.add((ImageView) findViewById(R.id.image1));
                imageViews.add((ImageView) findViewById(R.id.image2));
                imageViews.add((ImageView) findViewById(R.id.image3));
                imageViews.add((ImageView) findViewById(R.id.image4));
                imageViews.add((ImageView) findViewById(R.id.image5));

                final ClipboardManager clipboardManager = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                final ClipData clipData = ClipData.newPlainText(mStolpersteine.getPerson().getFullName(), bio.getBiographyText());
                assert clipboardManager != null;
                clipboardManager.setPrimaryClip(clipData);

                translateBtn.setVisibility(View.VISIBLE);


            } else if (bio.getImagesList() != null) {

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
                    else if(i == 3)
                        imageView.setId(R.id.image4);
                    else if(i == 4)
                        imageView.setId(R.id.image5);

                    imageView.setLayoutParams(imageParams);
                    imageViews.add(imageView);
                    flowTextContainer.addView(imageView);
                }
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
        List<Section> sections;

        SetupMore(List<Section> sections) {
            this.sections = sections;
        }

        @Override
        public void run() {
            populateMore();
        }

        private void populateMore() {
            final ArrayList<SectionsCard> items = new ArrayList<>();

            for (Section section : sections ) {

                if (BuildConfig.DEBUG && section.getText().size() != section.getLink().size()) throw new AssertionError();

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
        mStolpersteine.setHasBiography(!TextUtils.isEmpty(mBiography.getBiographyText()));
        mStolpersteine.setNumImages(mBiography.getImagesList().size());
        StolpersteineViewModel mViewModel = ViewModelProviders.of(this).get(StolpersteineViewModel.class);
        mViewModel.insert(mStolpersteine);
    }
}
