
package me.carc.stolpersteine.data.remote.model;

import android.arch.persistence.room.Embedded;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Location implements Parcelable {

    @SerializedName("street")
    @Expose
    private String street;

    @SerializedName("zipCode")
    @Expose
    private String zipCode;

    @SerializedName("city")
    @Expose
    private String city;

    @SerializedName("sublocality1")
    @Expose
    private String sublocality1;

    @SerializedName("sublocality2")
    @Expose
    private String sublocality2;

    @SerializedName("state")
    @Expose
    private String state;

    @SerializedName("coordinates")
    @Expose
    @Embedded
    private Coordinates coordinates;

    public String getStreet() {
        return street;
    }
    public void setStreet(String street) {
        this.street = street;
    }

    public String getZipCode() {
        return zipCode;
    }
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    public String getSublocality1() {
        return sublocality1;
    }
    public void setSublocality1(String sublocality1) {
        this.sublocality1 = sublocality1;
    }

    public String getSublocality2() {
        return sublocality2;
    }
    public void setSublocality2(String sublocality2) {
        this.sublocality2 = sublocality2;
    }

    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }
    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }


    public String buildAddress() {
        StringBuilder sb = new StringBuilder(street);

        if(sublocality1.equalsIgnoreCase(sublocality2))
            sb.append(", ").append(sublocality1);
        else if(!TextUtils.isEmpty(sublocality1))
            sb.append(", ").append(sublocality1);
        else if(!TextUtils.isEmpty(sublocality2))
            sb.append(", ").append(sublocality2);

        if(!TextUtils.isEmpty(zipCode))
            sb.append(", ").append(zipCode);

        if(!TextUtils.isEmpty(zipCode))
            sb.append(", ").append(zipCode);

        if(city.equalsIgnoreCase(state))
            sb.append(", ").append(city);
        else
            sb.append(", ").append(city).append(", ").append(state);

        return sb.toString();
    }


    public String buildShortAddress() {
        StringBuilder sb = new StringBuilder(street);

        if(sublocality1.equalsIgnoreCase(sublocality2))
            sb.append(", ").append(sublocality1);
        else if(!TextUtils.isEmpty(sublocality1))
            sb.append(", ").append(sublocality1);
        else if(!TextUtils.isEmpty(sublocality2))
            sb.append(", ").append(sublocality2);

        return sb.toString();
    }


    public String buildVerticalAddress() {
        StringBuilder sb = new StringBuilder(street);

        if(sublocality1.equalsIgnoreCase(sublocality2))
            sb.append("\n").append(sublocality1);
        else if(!TextUtils.isEmpty(sublocality1))
            sb.append("\n").append(sublocality1);
        else if(!TextUtils.isEmpty(sublocality2))
            sb.append("\n").append(sublocality2);

        if(!TextUtils.isEmpty(zipCode))
            sb.append("\n").append(zipCode);

        if(!TextUtils.isEmpty(zipCode))
            sb.append("\n").append(zipCode);

        if(city.equalsIgnoreCase(state))
            sb.append("\n").append(city);
        else
            sb.append("\n").append(city).append(", ").append(state);

        sb.append("\n");  // add extra line

        return sb.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.street);
        dest.writeString(this.zipCode);
        dest.writeString(this.city);
        dest.writeString(this.sublocality1);
        dest.writeString(this.sublocality2);
        dest.writeString(this.state);
        dest.writeParcelable(this.coordinates, flags);
    }

    public Location() {
    }

    protected Location(Parcel in) {
        this.street = in.readString();
        this.zipCode = in.readString();
        this.city = in.readString();
        this.sublocality1 = in.readString();
        this.sublocality2 = in.readString();
        this.state = in.readString();
        this.coordinates = in.readParcelable(Coordinates.class.getClassLoader());
    }

    public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator<Location>() {
        public Location createFromParcel(Parcel source) {
            return new Location(source);
        }

        public Location[] newArray(int size) {
            return new Location[size];
        }
    };
}
