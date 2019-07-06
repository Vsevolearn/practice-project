package com.uclab.everytree.services;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.uclab.everytree.BuildConfig;
import com.uclab.everytree.interceptors.TokenAuthenticator;
import com.uclab.everytree.interceptors.TokenRequest;
import com.uclab.everytree.interfaces.everyTreeAPI;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkService {
    private static NetworkService mInstance;
    private static final String BASE_URL = AppConfig.getBaseUrl() + "/api/v0/";
    private Retrofit mRetrofit;

    private OkHttpClient instantiateOkHttpClient(Context _cxt){
        //create cache object passing cache dir
        int cacheSize = 25 * 1024 * 1024;
        Cache cache = new Cache(_cxt.getCacheDir(), cacheSize);

        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
        logInterceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);

        return new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .readTimeout(28, TimeUnit.SECONDS)
                .cache(cache)
                .authenticator(new TokenAuthenticator(_cxt))
                .addInterceptor(new TokenRequest(_cxt))
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Interceptor.Chain chain) throws IOException {
                        Request.Builder requestBuilder = chain.request().newBuilder();
                        requestBuilder.header("Content-Type", "application/json");
                        requestBuilder.header("Accept", "application/json");
                        return chain.proceed(requestBuilder.build());
                    }
                })
                .addInterceptor(logInterceptor)
                .build();
    }

    private NetworkService(Context _cxt) {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(instantiateOkHttpClient(_cxt))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public static NetworkService getInstance(Context _cxt) {
        if (mInstance == null) {
            mInstance = new NetworkService(_cxt);
        }
        return mInstance;
    }

    public everyTreeAPI getEveryTreeAPI() {
        return mRetrofit.create(everyTreeAPI.class);
    }
}