package me.carc.stolpersteine.data.remote.bio;

import com.fcannizzaro.jsoup.annotations.interfaces.AfterBind;
import com.fcannizzaro.jsoup.annotations.interfaces.Selector;

import org.jsoup.nodes.Element;

import me.carc.stolpersteine.common.Commons;

/**
 * Created by bamptonm on 11/06/2018.
 */

public class BiographyParser {
    private static final String TAG = BiographyParser.class.getName();

    private String plainText;
    private String htmlText;

    public String getPlainText() {
        return plainText;
    }
    public String getHtmlText() {
        return htmlText;
    }

    @Selector(".field-name-field-st-biografie")
    public void bioExtractor(Element bioElement) {

        if(Commons.isNotNull(bioElement)) {
            plainText = bioElement.children().text();
            htmlText = bioElement.children().html();
        }
    }

    @AfterBind
    void attached() {
    }
}
