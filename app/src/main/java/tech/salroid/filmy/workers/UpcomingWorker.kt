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

class UpcomingWorker(
    private val context: Context,
    workParameters: WorkerParameters
) : ListenableWorker(context, workParameters) {

    private val tmdbRequestQueue = TmdbVolleySingleton.requestQueue
    private var taskFinished = 0

    override fun startWork(): ListenableFuture<Result> {
        syncNowUpComing()
        return CallbackToFutureAdapter.getFuture {
            it.set(Result.success())
        }
    }

    private fun syncNowUpComing() {
        val url = "https://api.themoviedb.org/3/movie/upcoming?api_key=${BuildConfig.TMDB_API_KEY}"
        val request = JsonObjectRequest(url, null,
            { response: JSONObject ->
                upComingParseOutput(response.toString())
                taskFinished++
                if (taskFinished == 3) {
                    //jobFinished(workParameters, false);
                    taskFinished = 0
                }
            }) { error: VolleyError -> Log.e("webi", "Volley Error: " + error.cause) }
        tmdbRequestQueue.add(request)
    }

    private fun upComingParseOutput(result_upcoming: String) {
        val pa = MainActivityParseWork(context, result_upcoming)
        pa.parseUpcoming()
    }
}