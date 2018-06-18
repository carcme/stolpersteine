
package me.carc.stolpersteine.data.remote.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import me.carc.stolpersteine.data.db.blocks.Converters;
import me.carc.stolpersteine.data.remote.bio.BioImages;

@Keep
@Entity(tableName = "stolpersteine_table")
public class Stolpersteine implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "blockId")
    private int blockId;

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;

    @SerializedName("createdAt")
    @Expose
    private String createdAt;

    @SerializedName("source")
    @Expose
    @TypeConverters(Converters.class)
    private Source source;

    @SerializedName("location")
    @Expose
    @Embedded
    private Location location;

    @SerializedName("person")
    @Expose
    @Embedded
    private Person person;

    @SerializedName("id")
    @Expose
    private String id;

    @Embedded
    private BioImages images;


    public Stolpersteine(int blockId, String type, String updatedAt, String createdAt, Source source, Location location, Person person, BioImages images, String id) {
        this.blockId = blockId;
        this.type = type;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
        this.source = source;
        this.location = location;
        this.person = person;
        this.images = images;
        this.id = id;
    }

    public int getBlockId() {
        return blockId;
    }
    public void setBlockId(int blockId) {
        this.blockId = blockId;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Source getSource() {
        return source;
    }
    public void setSource(Source source) {
        this.source = source;
    }

    public Location getLocation() {
        return location;
    }
    public void setLocation(Location location) {
        this.location = location;
    }

    public Person getPerson() {
        return person;
    }
    public void setPerson(Person person) {
        this.person = person;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public BioImages getImages() {
        return images;
    }
    public void setImages(BioImages images) {
        this.images = images;
    }

    public static final DiffUtil.ItemCallback<Stolpersteine> DIFF_CALLBACK = new DiffUtil.ItemCallback<Stolpersteine>() {

        @Override
        public boolean areItemsTheSame(@NonNull Stolpersteine oldStone, @NonNull Stolpersteine newStone) {
            return oldStone.id.equals(newStone.id);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Stolpersteine oldStone, @NonNull Stolpersteine newStone) {
            return oldStone.getPerson().getFullName().equals(newStone.getPerson().getFullName()) &&
                    oldStone.getLocation().getStreet().equals(newStone.getLocation().getStreet());
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.blockId);
        dest.writeString(this.type);
        dest.writeString(this.updatedAt);
        dest.writeString(this.createdAt);
        dest.writeParcelable(this.source, flags);
        dest.writeParcelable(this.location, flags);
        dest.writeParcelable(this.person, flags);
        dest.writeParcelable(this.images, flags);
        dest.writeString(this.id);
    }

    protected Stolpersteine(Parcel in) {
        this.blockId = in.readInt();
        this.type = in.readString();
        this.updatedAt = in.readString();
        this.createdAt = in.readString();
        this.source = in.readParcelable(Source.class.getClassLoader());
        this.location = in.readParcelable(Location.class.getClassLoader());
        this.person = in.readParcelable(Person.class.getClassLoader());
        this.images = in.readParcelable(BioImages.class.getClassLoader());
        this.id = in.readString();
    }

    public static final Parcelable.Creator<Stolpersteine> CREATOR = new Parcelable.Creator<Stolpersteine>() {
        public Stolpersteine createFromParcel(Parcel source) {
            return new Stolpersteine(source);
        }

        public Stolpersteine[] newArray(int size) {
            return new Stolpersteine[size];
        }
    };
}
