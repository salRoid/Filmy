package tech.salroid.filmy.workers

import android.content.Context
import androidx.concurrent.futures.CallbackToFutureAdapter
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.google.common.util.concurrent.ListenableFuture

class TrendingWorker(
    context: Context,
    workParameters: WorkerParameters
) : ListenableWorker(context, workParameters) {

    override fun startWork(): ListenableFuture<Result> {
       /* NetworkUtil.getTrendingMovies({
            // TODO save to DB
        }, {

        })*/
        return CallbackToFutureAdapter.getFuture {
            it.set(Result.success())
        }
    }

/*    private fun syncNowTrending() {
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
    }*/


    /*private fun sendFetchFailedMessage(message: Int) {
        val intent = Intent("fetch-failed")
        intent.putExtra("message", message)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }*/
}