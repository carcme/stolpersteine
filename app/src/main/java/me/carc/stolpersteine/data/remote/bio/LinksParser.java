package me.carc.stolpersteine.data.remote.bio;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.fcannizzaro.jsoup.annotations.interfaces.AfterBind;
import com.fcannizzaro.jsoup.annotations.interfaces.Selector;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.util.ArrayList;
import java.util.List;

import me.carc.stolpersteine.common.Commons;

/**
 * Created by bamptonm on 12/06/2018.
 */

public class LinksParser {
    private static final String TAG = LinksParser.class.getName();

    private List<Section> sections;

    public List<Section> getSections() {
        return sections;
    }

    @Selector(".verweise")
    public void linksExtractor(Element linksElement) {

        if(Commons.isNotNull(linksElement)) {
            sections = new ArrayList<>();
            for (Element item : linksElement.children()) {
                Log.d(TAG, "linksExtractor: ");
                if(item.tagName().equals("div")) {
                    Section section = new Section();
                    for (Node node : item.childNodes()) {
                        if(node instanceof Element) {
                            if (((Element) node).tag().toString().equals("h5")) {
                                section.type = ((Element) node).getAllElements().text();
                            } else {
                                section.textList.add(((Element) node).getElementsByAttribute("href").text());
                                section.linkList.add(((Element) node).getElementsByAttribute("href").attr("href"));
                            }
                        }
                    }
                    sections.add(section);
                }
            }
        }
    }

    public class Section implements Parcelable {
        String type;
        List<String> linkList = new ArrayList<>();
        List<String> textList = new ArrayList<>();

        public String getType() {
            return type;
        }

        public List<String> getLink() {
            return linkList;
        }

        public List<String> getText() {
            return textList;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.type);
            dest.writeStringList(this.linkList);
            dest.writeStringList(this.textList);
        }

        public Section() {
        }

        protected Section(Parcel in) {
            this.type = in.readString();
            this.linkList = in.createStringArrayList();
            this.textList = in.createStringArrayList();
        }

        public final Parcelable.Creator<Section> CREATOR = new Parcelable.Creator<Section>() {
            public Section createFromParcel(Parcel source) {
                return new Section(source);
            }

            public Section[] newArray(int size) {
                return new Section[size];
            }
        };
    }

    @AfterBind
    void attached() {
    }
}
