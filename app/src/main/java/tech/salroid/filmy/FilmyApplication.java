package tech.salroid.filmy;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Home on 7/23/2016.
 */
public class FilmyApplication extends Application {

    private static FilmyApplication instance;

    public static FilmyApplication getInstance() {
        return instance;
    }

    public static Context getContext() {
        return instance.getApplicationContext();

    }

    @Override
    public void onCreate() {

        super.onCreate();

        Fabric.with(this, new Crashlytics());

        instance = this;
    }
}
