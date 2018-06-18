
package me.carc.stolpersteine.data.remote.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Person implements Parcelable {

    @SerializedName("firstName")
    @Expose
    private String firstName;

    @SerializedName("lastName")
    @Expose
    private String lastName;
    @SerializedName("biographyUrl")
    @Expose
    private String biographyUrl;

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBiographyUrl() {
        return biographyUrl;
    }
    public void setBiographyUrl(String biographyUrl) {
        this.biographyUrl = biographyUrl;
    }

    public String getFullName() {
        if(TextUtils.isEmpty(firstName))
            return lastName;

        // the remote database has first and last name around the wrong way!!
        return lastName.concat(" ").concat(firstName);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeString(this.biographyUrl);
    }

    public Person() {
    }

    protected Person(Parcel in) {
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.biographyUrl = in.readString();
    }

    public static final Parcelable.Creator<Person> CREATOR = new Parcelable.Creator<Person>() {
        public Person createFromParcel(Parcel source) {
            return new Person(source);
        }

        public Person[] newArray(int size) {
            return new Person[size];
        }
    };
}
