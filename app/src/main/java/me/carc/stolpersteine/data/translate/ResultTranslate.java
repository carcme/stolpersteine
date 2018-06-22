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

    @SerializedName("quotaFinished")
    private boolean quotaFinished;

    @SerializedName("responseStatus")
    private int responseStatus;


    public ResponseTranslate getResponseData() {
        return responseData;
    }
    public void setResponseData(ResponseTranslate responseData) {
        this.responseData = responseData;
    }

    public boolean isQuotaFinished() {
        return quotaFinished;
    }
    public void setQuotaFinished(boolean quotaFinished) {
        this.quotaFinished = quotaFinished;
    }

    public int getResponseStatus() {
        return responseStatus;
    }
    public void setResponseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
    }
}
