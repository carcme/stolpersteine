package me.carc.stolpersteine.data.translate;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Query the stolpersteine database
 * Created by bamptonm on 21/08/2017.
 */

public interface TranslateApi {

    String TRANSLATE_URL = "http://mymemory.translated.net/api/";

    @GET("get")
    Call<ResultTranslate> translate(@Query("q") String query,
                                    @Query("langpair") String languagePair);
}

