package me.carc.stolpersteine.data.remote;

import java.util.Locale;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StolpersteineApiServiceProvider {

    private static StolpersteineApi service;

    public static StolpersteineApi get() {
        if (service == null) {
            service = createService();
        }
        return service;
    }

    private static StolpersteineApi createService() {

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            HttpUrl originalHttpUrl = original.url();

            HttpUrl url = originalHttpUrl.newBuilder()
                    .addQueryParameter("city", StolpersteineApi.ONLY_BERLIN)
                    .build();

            // Request customization: add request headers
            Request.Builder requestBuilder = original.newBuilder()
                    .header("Accept-Language", Locale.getDefault().getLanguage())
                    .url(url);

            Request request = requestBuilder.build();
            return chain.proceed(request);
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(StolpersteineApi.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        return retrofit.create(StolpersteineApi.class);
    }
}