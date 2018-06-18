
package me.carc.stolpersteine.data.remote.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Ignore;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Coordinates implements Parcelable {

    @SerializedName("longitude")
    @Expose
    @ColumnInfo(name = "longitude")
    private Double longitude;

    @SerializedName("latitude")
    @Expose
    @ColumnInfo(name = "latitude")
    private Double latitude;

    @Ignore
    private Double distance;

    public Coordinates() {
    }

    public Double getLongitude() {
        return longitude;
    }
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getDistance() {
        return distance;
    }
    public void setDistance(Double distance) {
        this.distance = distance;
    }

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.longitude);
        dest.writeValue(this.latitude);
        dest.writeValue(this.distance);
    }
    protected Coordinates(Parcel in) {
        this.longitude = (Double) in.readValue(Double.class.getClassLoader());
        this.latitude = (Double) in.readValue(Double.class.getClassLoader());
        this.distance = (Double) in.readValue(Double.class.getClassLoader());
    }
    public static final Parcelable.Creator<Coordinates> CREATOR = new Parcelable.Creator<Coordinates>() {
        public Coordinates createFromParcel(Parcel source) {
            return new Coordinates(source);
        }

        public Coordinates[] newArray(int size) {
            return new Coordinates[size];
        }
    };
}
