package me.carc.stolpersteine.data.translate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by bamptonm on 15/06/2018.
 */

public class ResponseTranslate {

    @SerializedName("translatedText")
    @Expose
    private String translatedText;

    @SerializedName("match")
    @Expose
    private double match;

    public String getTranslatedText() {
        return translatedText;
    }

    public void setTranslatedText(String translatedText) {
        this.translatedText = translatedText;
    }

    public double getMatch() {
        return match;
    }

    public void setMatch(double match) {
        this.match = match;
    }
}
