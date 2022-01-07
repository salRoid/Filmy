package tech.salroid.filmy.networking

import android.util.Log
import com.android.volley.toolbox.JsonObjectRequest
import tech.salroid.filmy.BuildConfig
import tech.salroid.filmy.FilmyApplication.Companion.context
import tech.salroid.filmy.R

class GetDataFromNetwork {

    private var mDataFetchedListener: DataFetchedListener? = null

    fun getMovieDetailsFromNetwork(movieId: String?) {
        val requestQueue = VolleySingleton.requestQueue

        val apiKey = BuildConfig.TMDB_API_KEY
        val baseUrl = "${context.resources.getString(R.string.tmdb_movie_base_url)}$movieId?api_key=$apiKey&append_to_response=trailers"

        val jsonObjectRequestForMovieDetails = JsonObjectRequest(baseUrl, null,
            { response ->
                mDataFetchedListener!!.dataFetched(
                    response.toString(),
                    MOVIE_DETAILS_CODE
                )
            }
        ) { error -> Log.e("webi", "Volley Error: " + error.cause) }
        requestQueue.add(jsonObjectRequestForMovieDetails)
    }

    fun setDataFetchedListener(dataFetchedListener: DataFetchedListener?) {
        mDataFetchedListener = dataFetchedListener
    }

    interface DataFetchedListener {
        fun dataFetched(response: String, type: Int)
    }

    companion object {
        const val MOVIE_DETAILS_CODE = 1
        const val CAST_CODE = 2
    }
}