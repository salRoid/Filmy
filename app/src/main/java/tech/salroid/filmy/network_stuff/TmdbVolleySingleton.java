package tech.salroid.filmy.network_stuff;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import tech.salroid.filmy.FilmyApplication;

/**
 * Created by Home on 8/3/2016.
 */
public class TmdbVolleySingleton {


    public static TmdbVolleySingleton instance = null;
    private RequestQueue requestQueue;


    private TmdbVolleySingleton() {
        requestQueue = Volley.newRequestQueue(FilmyApplication.getContext());
    }

    public static TmdbVolleySingleton getInstance() {

        if (instance == null) {
            instance = new TmdbVolleySingleton();
        }
        return instance;
    }


    public RequestQueue getRequestQueue() {
        return requestQueue;
    }
}
