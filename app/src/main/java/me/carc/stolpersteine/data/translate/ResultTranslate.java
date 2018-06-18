package me.carc.stolpersteine.data.translate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by bamptonm on 15/06/2018.
 */

public class ResultTranslate {

    @SerializedName("responseData")
    @Expose
    private ResponseTranslate responseData;

    public ResponseTranslate getResponseData() {
        return responseData;
    }

    public void setResponseData(ResponseTranslate responseData) {
        this.responseData = responseData;
    }
}
