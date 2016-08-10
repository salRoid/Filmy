package tech.salroid.filmy.Utils;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by R Ankit on 08-08-2016.
 */

public class Network {

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

}
