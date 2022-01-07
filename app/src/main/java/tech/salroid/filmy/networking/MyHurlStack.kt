package tech.salroid.filmy.networking

import com.android.volley.toolbox.HurlStack
import tech.salroid.filmy.BuildConfig
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class MyHurlStack : HurlStack() {

    @Throws(IOException::class)
    override fun createConnection(url: URL): HttpURLConnection {
        val apiKey = BuildConfig.TMDB_API_KEY
        val connection = url.openConnection() as HttpURLConnection
        connection.setRequestProperty("Content-Type", "application/json")
        connection.setRequestProperty("trakt-api-version", "2")
        //connection.setRequestProperty("trakt-api-key", FilmyApplication.getContext().getString(R.string.api_key));
        return connection
    }
}