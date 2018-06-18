package me.carc.stolpersteine.data.remote.bio;

import com.fcannizzaro.jsoup.annotations.interfaces.AfterBind;
import com.fcannizzaro.jsoup.annotations.interfaces.Selector;

import org.jsoup.nodes.Element;

import java.util.HashMap;

/**
 * Created by bamptonm on 11/06/2018.
 */

public class InfoParser {
    private static final String TAG = ImageParser.class.getName();

    private HashMap<String, String> hashMap = new HashMap<String, String>();

    public HashMap<String, String> getHashMap() {
        return hashMap;
    }

    @Selector(".biografieData")
    public void infoExtractor(Element infoElement) {

        for(Element element : infoElement.children()) {
            for (Element child : element.children()) {
                String key = element.children().eachText().get(0);
                String val = element.children().eachText().get(1);

                // Clean up the string values a bit
                if(val.startsWith("on the"))
                    val = val.replace("on the ", "");
                hashMap.put(key, val);
                break;
            }
        }
    }

    @AfterBind
    void attached() {
    }
}
