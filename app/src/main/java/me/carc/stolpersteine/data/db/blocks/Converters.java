package me.carc.stolpersteine.data.db.blocks;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.carc.stolpersteine.data.remote.bio.BioImages;
import me.carc.stolpersteine.data.remote.bio.Section;
import me.carc.stolpersteine.data.remote.model.Source;

/**
 * Created by bamptonm on 12/05/2018.
 */

public class Converters {

    @TypeConverter
    public static Source stringToSource(String str) {
        if (str == null) {
            return new Source();
        }
        return new Gson().fromJson(str, new TypeToken<Source>() {}.getType());
    }

    @TypeConverter
    public static String sourceToString(Source latLon) {
        return new Gson().toJson(latLon);
    }

    /*******************************************/

    @TypeConverter
    public static List<BioImages> stringToBioImages(String str) {
        if (str == null) {
            return new ArrayList<>();
        }
        return new Gson().fromJson(str, new TypeToken<List<BioImages>>() {}.getType());
    }

    @TypeConverter
    public static String bioImageToString(List<BioImages> bioImages) {
        return new Gson().toJson(bioImages);
    }


    /*******************************************/

    @TypeConverter
    public static List<Section> stringToSections(String str) {
        if (str == null) {
            return new ArrayList<>();
        }
        return new Gson().fromJson(str, new TypeToken<List<Section>>() {}.getType());
    }

    @TypeConverter
    public static String sectionsToString(List<Section> sections) {
        return new Gson().toJson(sections);
    }


    /*******************************************/

    @TypeConverter
    public static HashMap<String, String> stringTohashMap(String str) {
        if (str == null) {
            return new HashMap<String, String>();
        }
        return new Gson().fromJson(str, new TypeToken<HashMap<String, String>>() {}.getType());
    }

    @TypeConverter
    public static String hashMapToString(HashMap hashMap) {
        return new Gson().toJson(hashMap);
    }

    /*******************************************/

}