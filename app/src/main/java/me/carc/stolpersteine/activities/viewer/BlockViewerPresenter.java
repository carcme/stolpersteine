package me.carc.stolpersteine.activities.viewer;

import android.util.Log;

import com.fcannizzaro.jsoup.annotations.JsoupProcessor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.util.concurrent.Executors;

import javax.inject.Inject;

import me.carc.stolpersteine.activities.base.MvpBasePresenter;
import me.carc.stolpersteine.common.C;
import me.carc.stolpersteine.data.DataManager;
import me.carc.stolpersteine.data.SharedPrefsHelper;
import me.carc.stolpersteine.data.remote.bio.Biography;
import me.carc.stolpersteine.data.remote.bio.BiographyParser;
import me.carc.stolpersteine.data.remote.bio.ImageParser;
import me.carc.stolpersteine.data.remote.bio.InfoParser;
import me.carc.stolpersteine.data.remote.bio.LinksParser;


/**
 *
 */
public class BlockViewerPresenter extends MvpBasePresenter<BlockViewerMvpView> {
    private static final String TAG = BlockViewerPresenter.class.getName();

    @Inject
    public BlockViewerPresenter() {
    }

    @Inject
    SharedPrefsHelper mSharePrefs;
    @Inject
    DataManager mDataManager;


    @Override
    public void attachView(BlockViewerMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    /**
     * Add the 'en' to the url
     * https://www.stolpersteine-berlin.de/en/biografie/1420
     * @param url the base url
     * @return modified url
     */
    private String getLanguage(String url) {
        if(C.USER_LANGUAGE.equals("de"))
            return url;
        else {
            int index = url.indexOf("biografie");
            return url.substring(0, index).concat("en/").concat(url.substring(index, url.length()));
        }
    }

    void getBioData(final String url) {

        Executors.newSingleThreadExecutor().execute(() -> {

            Biography bio = new Biography();

            try {
                Element body = Jsoup.connect(getLanguage(url)).get();

                try {
                    bio.setImagesList(JsoupProcessor.from(body, ImageParser.class).getBioImagesList());
                } catch (Exception noImageElementFound){
                    Log.d(TAG, "getBioData:ImageParser - No image element found ");
                }

                try {
                    bio.setInfo(JsoupProcessor.from(body, InfoParser.class).getHashMap());
                } catch (Exception noImageElementFound){
                    Log.d(TAG, "getBioData:InfoParser - No info found ");
                }

                try {
                    BiographyParser biographyParser = JsoupProcessor.from(body, BiographyParser.class);
                    bio.setBiographyHtml(biographyParser.getHtmlText());
                    bio.setBiographyText(biographyParser.getPlainText());
                } catch (Exception noImageElementFound){
                    Log.d(TAG, "getBioData:BiographyParser - No biography found ");
                }

                try {
                    bio.setSections(JsoupProcessor.from(body, LinksParser.class).getSections());
                } catch (Exception noImageElementFound){
                    Log.d(TAG, "getBioData:FamilyParser - No family found ");
                }

                getMvpView().onHtmlParsed(bio);

            } catch (Exception e) {

                e.printStackTrace();
            }
        });
    }
}
