package me.carc.stolpersteine.data.remote.bio;

import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.carc.stolpersteine.data.db.blocks.Converters;

/**
 * Created by bamptonm on 12/06/2018.
 */

public class Biography implements Parcelable {
    private static final String TAG = Biography.class.getName();

    @TypeConverters(Converters.class)
    private List<BioImages> imagesList = null;

    @TypeConverters(Converters.class)
    private List<Section> sections = null;

    @TypeConverters(Converters.class)
    private HashMap<String, String> info = new HashMap<>();

    private String biographyText;

    private String biographyHtml;


    public List<BioImages> getImagesList() {
        return imagesList;
    }
    public void setImagesList(List<BioImages> imagesList) {
        this.imagesList = imagesList;
    }

    public List<Section> getSections() {
        return sections;
    }
    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    public HashMap<String, String> getInfo() {
        return info;
    }
    public void setInfo(HashMap<String, String> info) {
        this.info = info;
    }

    public String getBiographyText() {
        return biographyText;
    }
    public void setBiographyText(String biographyText) {
        this.biographyText = biographyText;
    }

    public String getBiographyHtml() {
        return biographyHtml;
    }
    public void setBiographyHtml(String biographyHtml) {
        this.biographyHtml = biographyHtml;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(imagesList);
        dest.writeList(this.sections);
        dest.writeSerializable(this.info);
        dest.writeString(this.biographyText);
        dest.writeString(this.biographyHtml);
    }

    public Biography() {
    }

    @SuppressWarnings("unchecked")
    protected Biography(Parcel in) {
        this.imagesList = in.createTypedArrayList(BioImages.CREATOR);
        this.sections = new ArrayList<>();
        in.readList(this.sections, List.class.getClassLoader());
        this.info = (HashMap<String, String>) in.readSerializable();
        this.biographyText = in.readString();
        this.biographyHtml = in.readString();
    }

    public static final Parcelable.Creator<Biography> CREATOR = new Parcelable.Creator<Biography>() {
        public Biography createFromParcel(Parcel source) {
            return new Biography(source);
        }

        public Biography[] newArray(int size) {
            return new Biography[size];
        }
    };
}
