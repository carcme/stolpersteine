
package me.carc.stolpersteine.data.remote.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Source implements Parcelable {

    @SerializedName("retrievedAt")
    @Expose
    private String retrievedAt;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("url")
    @Expose
    private String url;

    public String getRetrievedAt() {
        return retrievedAt;
    }
    public void setRetrievedAt(String retrievedAt) {
        this.retrievedAt = retrievedAt;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.retrievedAt);
        dest.writeString(this.name);
        dest.writeString(this.url);
    }

    public Source() {
    }

    protected Source(Parcel in) {
        this.retrievedAt = in.readString();
        this.name = in.readString();
        this.url = in.readString();
    }

    public static final Parcelable.Creator<Source> CREATOR = new Parcelable.Creator<Source>() {
        public Source createFromParcel(Parcel source) {
            return new Source(source);
        }

        public Source[] newArray(int size) {
            return new Source[size];
        }
    };
}
