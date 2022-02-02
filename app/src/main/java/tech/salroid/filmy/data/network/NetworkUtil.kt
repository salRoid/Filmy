package tech.salroid.filmy.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tech.salroid.filmy.BuildConfig
import tech.salroid.filmy.data.local.db.entity.MovieDetails
import tech.salroid.filmy.data.local.model.*

object NetworkUtil {

    fun getInTheatersMovies(successCallback: (MoviesResponse?) -> Unit, errorCallback: () -> Unit) {

        val call = RetrofitClient.filmyApi.getInTheatersMovies()
        call.enqueue(object : Callback<MoviesResponse> {
            override fun onResponse(
                call: Call<MoviesResponse>,
                response: Response<MoviesResponse>
            ) {
                Log.d("webi", "InTheater Movies: ${response.body()}")
                successCallback.invoke(response.body())
            }

            override fun onFailure(call: Call<MoviesResponse>, t: Throwable) {
                Log.d("webi", "InTheater Movies Error: ${t.message}")
            }
        })
    }

    fun getUpComing(successCallback: (MoviesResponse?) -> Unit, errorCallback: () -> Unit) {

        val call = RetrofitClient.filmyApi.getUpcomingMovies()
        call.enqueue(object : Callback<MoviesResponse> {
            override fun onResponse(
                call: Call<MoviesResponse>,
                response: Response<MoviesResponse>
            ) {
                Log.d("webi", "Upcoming Movies: ${response.body()}")
                successCallback.invoke(response.body())
            }

            override fun onFailure(call: Call<MoviesResponse>, t: Throwable) {
                Log.d("webi", "Upcoming Movies Error: ${t.message}")
            }
        })
    }

    fun getTrendingMovies(successCallback: (MoviesResponse?) -> Unit, errorCallback: () -> Unit) {

        val call = RetrofitClient.filmyApi.getTrendingMovies()
        call.enqueue(object : Callback<MoviesResponse> {
            override fun onResponse(
                call: Call<MoviesResponse>,
                response: Response<MoviesResponse>
            ) {
                Log.d("webi", "Trending Movies: ${response.body()}")
                successCallback.invoke(response.body())
            }

            override fun onFailure(call: Call<MoviesResponse>, t: Throwable) {
                Log.d("webi", "Trending Movies Error: ${t.message}")
            }
        })
    }

    fun getMovieDetails(
        movieId: String,
        successCallback: (MovieDetails?) -> Unit,
        errorCallback: () -> Unit
    ) {
        val call = RetrofitClient.filmyApi.getMovieDetails(movieId)
        call.enqueue(object : Callback<MovieDetails> {
            override fun onResponse(
                call: Call<MovieDetails>,
                response: Response<MovieDetails>
            ) {
                Log.d("webi", "Movie Details: ${response.body()}")
                successCallback.invoke(response.body())
            }

            override fun onFailure(call: Call<MovieDetails>, t: Throwable) {
                Log.d("webi", "Movie Details Error: ${t.message}")
            }
        })
    }

    fun getRating(
        movieId: String,
        successCallback: (RatingResponse?) -> Unit,
        errorCallback: () -> Unit
    ) {

        val call = RetrofitClient.filmyApi.getOMDBRatings(
            FilmyApi.BASE_URL_OMDB,
            movieId,
            BuildConfig.OMDB_API_KEY,
            true,
            "json"
        )
        call.enqueue(object : Callback<RatingResponse> {
            override fun onResponse(
                call: Call<RatingResponse>,
                response: Response<RatingResponse>
            ) {
                Log.d("webi", "Ratings: ${response.body()}")
                successCallback.invoke(response.body())
            }

            override fun onFailure(call: Call<RatingResponse>, t: Throwable) {
                Log.d("webi", "Ratings Error: ${t.message}")
            }
        })
    }

    fun getCastAndCrew(
        movieId: String,
        successCallback: (CastAndCrewResponse?) -> Unit,
        errorCallback: () -> Unit
    ) {
        val call = RetrofitClient.filmyApi.getCasts(movieId)
        call.enqueue(object : Callback<CastAndCrewResponse> {
            override fun onResponse(
                call: Call<CastAndCrewResponse>,
                response: Response<CastAndCrewResponse>
            ) {
                Log.d("webi", "Cast and Crew: ${response.body()}")
                successCallback.invoke(response.body())
            }

            override fun onFailure(call: Call<CastAndCrewResponse>, t: Throwable) {
                Log.d("webi", "Cast Error: ${t.message}")
            }
        })
    }

    fun getSimilarMovies(
        movieId: String,
        successCallback: (SimilarMoviesResponse?) -> Unit,
        errorCallback: () -> Unit
    ) {
        val call = RetrofitClient.filmyApi.getSimilarMovies(movieId)
        call.enqueue(object : Callback<SimilarMoviesResponse> {
            override fun onResponse(
                call: Call<SimilarMoviesResponse>,
                response: Response<SimilarMoviesResponse>
            ) {
                Log.d("webi", "Similar Movies: ${response.body()}")
                successCallback.invoke(response.body())
            }

            override fun onFailure(call: Call<SimilarMoviesResponse>, t: Throwable) {
                Log.d("webi", "Similar Movies Error: ${t.message}")
            }
        })
    }

    fun getCastDetails(
        movieId: String,
        successCallback: (CastDetailsResponse?) -> Unit,
        errorCallback: () -> Unit
    ) {

        val call = RetrofitClient.filmyApi.getCastDetails(movieId)
        call.enqueue(object : Callback<CastDetailsResponse> {
            override fun onResponse(
                call: Call<CastDetailsResponse>,
                response: Response<CastDetailsResponse>
            ) {
                Log.d("webi", "Cast Details: ${response.body()}")
                successCallback.invoke(response.body())
            }

            override fun onFailure(call: Call<CastDetailsResponse>, t: Throwable) {
                Log.d("webi", "Cast Details Error: ${t.message}")
            }
        })
    }

    fun getCastMovieDetails(
        movieId: String,
        successCallback: (CastMovieDetailsResponse?) -> Unit,
        errorCallback: () -> Unit
    ) {

        val call = RetrofitClient.filmyApi.getCastMovieDetails(movieId)
        call.enqueue(object : Callback<CastMovieDetailsResponse> {
            override fun onResponse(
                call: Call<CastMovieDetailsResponse>,
                response: Response<CastMovieDetailsResponse>
            ) {
                Log.d("webi", "Cast Movie Details: ${response.body()}")
                successCallback.invoke(response.body())
            }

            override fun onFailure(call: Call<CastMovieDetailsResponse>, t: Throwable) {
                Log.d("webi", "Cast Movie Details Error: ${t.message}")
            }
        })
    }

    suspend fun searchMovies(query: String): SearchResultResponse? {
        val response = RetrofitClient.filmyApi.searchMovies(query)
        return if (response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }

    fun isNetworkConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null
    }
}