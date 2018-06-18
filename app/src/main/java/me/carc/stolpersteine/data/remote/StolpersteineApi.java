package me.carc.stolpersteine.data.remote;

import java.util.List;

import me.carc.stolpersteine.data.remote.model.Stolpersteine;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by bamptonm on 21/08/2017.
 */

public interface StolpersteineApi {

    String API_BASE_URL = "http://api.stolpersteineapp.org/v1/";
    String ONLY_BERLIN = "Berlin";

    @GET("stolpersteine")
    Call<List<Stolpersteine>> getStolpersteines(@Query("offset") int offset,
                                                @Query("limit") int limit);



}

