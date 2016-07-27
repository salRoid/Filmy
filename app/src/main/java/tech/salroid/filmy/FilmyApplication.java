package tech.salroid.filmy;

import android.app.Application;
import android.content.Context;

/**
 * Created by Home on 7/23/2016.
 */
public class FilmyApplication extends Application {

    private static FilmyApplication instance;


    @Override
    public void onCreate() {

        super.onCreate();

        instance = this;
    }

    public static FilmyApplication getInstance() {
        return instance;
    }


    public static Context getContext() {
        return instance.getApplicationContext();

    }
}
