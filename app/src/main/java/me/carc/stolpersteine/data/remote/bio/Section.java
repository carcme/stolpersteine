package me.carc.stolpersteine.data.remote.bio;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bamptonm on 18/06/2018.
 */


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
