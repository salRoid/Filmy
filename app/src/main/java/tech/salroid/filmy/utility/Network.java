package tech.salroid.filmy.utility;

import android.content.Context;
import android.net.ConnectivityManager;


public class Network {

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

}
