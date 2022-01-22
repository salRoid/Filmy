package tech.salroid.filmy.workers

import android.content.Context
import androidx.concurrent.futures.CallbackToFutureAdapter
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.google.common.util.concurrent.ListenableFuture
import tech.salroid.filmy.data.network.NetworkUtil

class InTheatersWorker(
    context: Context,
    workParameters: WorkerParameters
) : ListenableWorker(context, workParameters) {

    override fun startWork(): ListenableFuture<Result> {
        NetworkUtil.getInTheatersMovies({
            // TODO save to DB
        }, {

        })
        return CallbackToFutureAdapter.getFuture {
            it.set(Result.success())
        }
    }

    /*   private fun syncNowInTheaters() {
           val inTheatersBaseUrl =
               "https://api.themoviedb.org/3/movie/now_playing?api_key=${BuildConfig.TMDB_API_KEY}"
           val jsonObjectRequest = JsonObjectRequest(inTheatersBaseUrl, null,
               { response: JSONObject ->

                   taskFinished++
                   if (taskFinished == 3) {
                       //jobFinished(jobParameters, false);
                       taskFinished = 0
                   }
               }) { error: VolleyError -> Log.e("webi", "Volley Error: " + error.cause) }
           tmdbRequestQueue.add(jsonObjectRequest)
       }*/
}