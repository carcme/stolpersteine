package me.carc.stolpersteine.data.translateGoogle;

import java.util.Locale;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GoogleApiServiceProvider {

    private static GoogleApi service;

    public static GoogleApi get() {
        if (service == null) {
            service = createService();
        }
        return service;
    }

    private static GoogleApi createService() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            HttpUrl originalHttpUrl = original.url();

            HttpUrl url = originalHttpUrl.newBuilder()
                    .addQueryParameter("client", "gtx")
                    .addQueryParameter("dt", "t")
                    .addQueryParameter("ie", "UTF-8")
                    .addQueryParameter("oe", "UTF-8")
                    .build();

            // Request customization: add request headers
            Request.Builder requestBuilder = original.newBuilder()
                    .header("Accept-Language", Locale.getDefault().getLanguage())
                    .url(url);

            Request request = requestBuilder.build();
            return chain.proceed(request);
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GoogleApi.TRANSLATE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        return retrofit.create(GoogleApi.class);
    }
}