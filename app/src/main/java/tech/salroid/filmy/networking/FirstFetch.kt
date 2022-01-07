package tech.salroid.filmy.networking

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject
import tech.salroid.filmy.BuildConfig
import tech.salroid.filmy.parser.MainActivityParseWork
import tech.salroid.filmy.workers.FilmyWorkManager

class FirstFetch(private val context: Context) {

    private val tmdbRequestQueue = TmdbVolleySingleton.requestQueue

    fun start() {
        syncNowTrending()
        syncNowInTheaters()
        syncNowUpComing()
        val workManager = FilmyWorkManager(context)
        workManager.createWork()
    }

    private fun syncNowInTheaters() {
        val apiKey = BuildConfig.TMDB_API_KEY
        val inTheatresBaseUrl = "https://api.themoviedb.org/3/movie/now_playing?api_key=$apiKey"
        val inTheatresJsonObjectRequest = JsonObjectRequest(inTheatresBaseUrl, null,
            { response: JSONObject ->
                inTheatresParseOutput(
                    response.toString(),
                    2
                )
            }) { error: VolleyError -> Log.e("webi", "Volley Error: " + error.cause) }
        tmdbRequestQueue.add(inTheatresJsonObjectRequest)
    }

    private fun syncNowUpComing() {
        val apiKey = BuildConfig.TMDB_API_KEY
        val baseUri = "https://api.themoviedb.org/3/movie/upcoming?api_key=$apiKey"
        val jsonRequest = JsonObjectRequest(baseUri, null,
            { response: JSONObject -> upcomingParseOutput(response.toString()) }) { error: VolleyError ->
            Log.e(
                "webi",
                "Volley Error: " + error.cause
            )
        }
        tmdbRequestQueue.add(jsonRequest)
    }

    private fun syncNowTrending() {
        val apiKey = BuildConfig.TMDB_API_KEY
        val url = "https://api.themoviedb.org/3/movie/popular?api_key=$apiKey"
        val jsonObjectRequest = JsonObjectRequest(url, null,
            { response: JSONObject -> parseOutput(response.toString()) }
        ) { error: VolleyError ->
            val networkResponse = error.networkResponse
            if (networkResponse != null) {
                sendFetchFailedMessage(networkResponse.statusCode)
            } else {
                sendFetchFailedMessage(0)
            }
        }
        tmdbRequestQueue.add(jsonObjectRequest)
    }

    private fun inTheatresParseOutput(s: String, type: Int) {
        val pa = MainActivityParseWork(context, s)
        pa.inTheatres()
    }

    private fun upcomingParseOutput(result_upcoming: String) {
        val pa = MainActivityParseWork(context, result_upcoming)
        pa.parseUpcoming()
    }

    private fun parseOutput(result: String) {
        val pa = MainActivityParseWork(context, result)
        pa.parse()
    }

    private fun sendFetchFailedMessage(message: Int) {
        val intent = Intent("fetch-failed")
        intent.putExtra("message", message)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }
}