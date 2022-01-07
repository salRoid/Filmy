package tech.salroid.filmy.workers

import android.content.Context
import android.util.Log
import androidx.concurrent.futures.CallbackToFutureAdapter
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.google.common.util.concurrent.ListenableFuture
import org.json.JSONObject
import tech.salroid.filmy.BuildConfig
import tech.salroid.filmy.networking.TmdbVolleySingleton
import tech.salroid.filmy.parser.MainActivityParseWork

class InTheatersWorker(
    private val context: Context,
    workParameters: WorkerParameters
) : ListenableWorker(context, workParameters) {

    private val tmdbRequestQueue = TmdbVolleySingleton.requestQueue
    private var taskFinished = 0

    override fun startWork(): ListenableFuture<Result> {
        syncNowInTheaters()
        return CallbackToFutureAdapter.getFuture {
            it.set(Result.success())
        }
    }

    private fun syncNowInTheaters() {
        val inTheatersBaseUrl =
            "https://api.themoviedb.org/3/movie/now_playing?api_key=${BuildConfig.TMDB_API_KEY}"
        val jsonObjectRequest = JsonObjectRequest(inTheatersBaseUrl, null,
            { response: JSONObject ->
                inTheatresParseOutput(response.toString(), 2)
                taskFinished++
                if (taskFinished == 3) {
                    //jobFinished(jobParameters, false);
                    taskFinished = 0
                }
            }) { error: VolleyError -> Log.e("webi", "Volley Error: " + error.cause) }
        tmdbRequestQueue.add(jsonObjectRequest)
    }

    private fun inTheatresParseOutput(s: String, type: Int) {
        val pa = MainActivityParseWork(context, s)
        pa.inTheatres()
    }
}