package me.carc.stolpersteine.data.remote.bio;

import android.arch.persistence.room.ColumnInfo;
import android.os.Parcel;
import android.os.Parcelable;

import com.fcannizzaro.jsoup.annotations.interfaces.AfterBind;

/**
 * Created by bamptonm on 11/06/2018.
 */

public class BioImages implements Parcelable {
    private static final String TAG = ImageParser.class.getName();

    @ColumnInfo(name = "imageTitle")
    private String title;
    private String publicImage;
    private String bigImage;
    private String thumbnail;

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublicImage() {
        return publicImage;
    }
    public void setPublicImage(String publicImage) {
        this.publicImage = publicImage;
    }

    public String getBigImage() {
        return bigImage;
    }
    public void setBigImage(String bigImage) {
        this.bigImage = bigImage;
    }

    public String getThumbnail() {
        return thumbnail;
    }
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.publicImage);
        dest.writeString(this.bigImage);
        dest.writeString(this.thumbnail);
    }

    public BioImages() {
    }

    protected BioImages(Parcel in) {
        this.title = in.readString();
        this.publicImage = in.readString();
        this.bigImage = in.readString();
        this.thumbnail = in.readString();
    }

    public static final Parcelable.Creator<BioImages> CREATOR = new Parcelable.Creator<BioImages>() {
        public BioImages createFromParcel(Parcel source) {
            return new BioImages(source);
        }

        public BioImages[] newArray(int size) {
            return new BioImages[size];
        }
    };

    @AfterBind
    void attached() {
    }
}
