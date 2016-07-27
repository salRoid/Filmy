package tech.salroid.filmy.Network;

import com.android.volley.toolbox.HurlStack;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by Home on 7/23/2016.
 */
public class TmdbHurlStack extends HurlStack {

    @Override
    protected HttpURLConnection createConnection(URL url) throws IOException {

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("ETag", "");


        return connection;
    }
}
