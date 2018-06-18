package me.carc.stolpersteine.data.db.blocks;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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


}