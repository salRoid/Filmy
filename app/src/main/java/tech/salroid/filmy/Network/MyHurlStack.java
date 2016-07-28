package tech.salroid.filmy.Network;

import com.android.volley.toolbox.HurlStack;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import tech.salroid.filmy.FilmyApplication;
import tech.salroid.filmy.R;

/**
 * Created by Home on 7/23/2016.
 */
public class MyHurlStack extends HurlStack{

    @Override
    protected HttpURLConnection createConnection(URL url) throws IOException {


        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("trakt-api-version", "2");
        connection.setRequestProperty("trakt-api-key", FilmyApplication.getContext().getString(R.string.api_key));


        return connection;
    }
}
