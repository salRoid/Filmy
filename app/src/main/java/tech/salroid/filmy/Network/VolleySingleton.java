package tech.salroid.filmy.network;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import tech.salroid.filmy.FilmyApplication;

/**
 * Created by Home on 7/23/2016.
 */
public class VolleySingleton {

    public static VolleySingleton instance = null;
    private RequestQueue requestQueue;


    private VolleySingleton() {
        requestQueue = Volley.newRequestQueue(FilmyApplication.getContext(), new MyHurlStack());
    }


    public static VolleySingleton getInstance() {

        if (instance == null) {
            instance = new VolleySingleton();
        }


        return instance;
    }


    public RequestQueue getRequestQueue() {
        return requestQueue;
    }
}
