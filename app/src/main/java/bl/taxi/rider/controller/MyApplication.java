package bl.taxi.rider.controller;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import java.util.concurrent.TimeUnit;

import bl.taxi.rider.R;
import bl.taxi.rider.interfaces.RetrofitAPI;
import bl.taxi.rider.utils.Constants;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by DELL on 09/07/2017.
 **/

public class MyApplication extends Application {

    private static Retrofit instance;
    private static RetrofitAPI service;
    private static RetrofitAPI cache_service;
    private static OkHttpClient okHttpClient;

    @Override
    public void onCreate() {
        super.onCreate();

        MultiDex.install(this);
        setInstance();

        int cacheSize = 10 * 1024 * 1024; // 10 MB
        Cache cache = new Cache(getCacheDir(), cacheSize);

        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .cache(cache)
                .build();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getString(R.string.font_regular))
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    private synchronized static void setInstance () {
        instance = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = instance.create(RetrofitAPI.class);
    }

    public static RetrofitAPI getService () {
        if (instance == null || service == null) {
            setInstance();
        }
        return service;
    }

    public synchronized static void setCacheInstance (String baseUrl) {

        Retrofit cache_instance = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        cache_service = cache_instance.create(RetrofitAPI.class);
    }

    public static RetrofitAPI getCacheService () {

        return cache_service;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
        MultiDex.install(this);
    }
}