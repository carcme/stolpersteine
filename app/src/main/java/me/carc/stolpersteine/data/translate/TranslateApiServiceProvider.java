package me.carc.stolpersteine.data.translate;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TranslateApiServiceProvider {

    private static TranslateApi service;

    public static TranslateApi get() {
        if (service == null) {
            service = createService();
        }
        return service;
    }

    private static TranslateApi createService() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TranslateApi.TRANSLATE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(TranslateApi.class);
    }
}