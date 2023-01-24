package tech.salroid.filmy.workers

import android.content.Context
import androidx.concurrent.futures.CallbackToFutureAdapter
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.google.common.util.concurrent.ListenableFuture

class UpcomingWorker(
    context: Context,
    workParameters: WorkerParameters
) : ListenableWorker(context, workParameters) {

    override fun startWork(): ListenableFuture<Result> {
      /*  NetworkUtil.getUpComing({
            // TODO save to DB
        }, {

        })*/
        return CallbackToFutureAdapter.getFuture {
            it.set(Result.success())
        }
    }

    /*private fun syncNowUpComing() {
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
*/
    private fun upComingParseOutput(result_upcoming: String) {
        // val pa = MainActivityParseWork(context, result_upcoming)
        // pa.parseUpcoming()
    }
}