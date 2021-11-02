package tech.salroid.filmy.workers

import android.content.Context
import android.content.Intent
import androidx.concurrent.futures.CallbackToFutureAdapter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.google.common.util.concurrent.ListenableFuture
import org.json.JSONObject
import tech.salroid.filmy.BuildConfig
import tech.salroid.filmy.networking.TmdbVolleySingleton
import tech.salroid.filmy.parser.MainActivityParseWork

class TrendingWorker(
    private val context: Context,
    workParameters: WorkerParameters
) : ListenableWorker(context, workParameters) {

    private val tmdbVolleySingleton = TmdbVolleySingleton.getInstance()
    private val tmdbRequestQueue = tmdbVolleySingleton.requestQueue
    private var taskFinished = 0

    override fun startWork(): ListenableFuture<Result> {
        syncNowTrending()
        return CallbackToFutureAdapter.getFuture {
            it.set(Result.success())
        }
    }

    private fun syncNowTrending() {
        val url =
            "https://api.themoviedb.org/3/movie/popular?api_key=${BuildConfig.TMDB_API_KEY}"

        val jsonObjectRequest = JsonObjectRequest(url, null,
            { response: JSONObject ->
                parseOutput(response.toString())
                taskFinished++
                if (taskFinished == 3) {
                    //jobFinished(workParameters, false);
                    taskFinished = 0
                }
            }
        ) { error: VolleyError ->
            val networkResponse = error.networkResponse
            if (networkResponse != null) sendFetchFailedMessage(networkResponse.statusCode) else sendFetchFailedMessage(
                0
            )
        }
        tmdbRequestQueue.add(jsonObjectRequest)
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