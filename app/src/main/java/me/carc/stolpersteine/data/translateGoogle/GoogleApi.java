package me.carc.stolpersteine.data.translateGoogle;

import com.google.gson.JsonArray;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Query the stolpersteine database
 * Created by bamptonm on 21/08/2017.
 */

public interface GoogleApi {

    String TRANSLATE_URL = "http://translate.google.com/translate_a/";

    @GET("single")
    Call<JsonArray> translate(@Query("q") String query,
                              @Query("tl") String targetLang,
                              @Query("sl") String sourceLang);
}

