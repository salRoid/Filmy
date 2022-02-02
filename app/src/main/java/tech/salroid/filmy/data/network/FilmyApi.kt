package tech.salroid.filmy.data.network

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url
import tech.salroid.filmy.data.local.db.entity.MovieDetails
import tech.salroid.filmy.data.local.model.*

interface FilmyApi {

    companion object{
        var BASE_URL = "https://api.themoviedb.org/3/"
        var BASE_URL_OMDB = "http://www.omdbapi.com/"
    }

    @GET("movie/popular")
    fun getTrendingMovies(): Call<MoviesResponse>

    @GET("movie/upcoming")
    fun getUpcomingMovies(): Call<MoviesResponse>

    @GET("movie/now_playing")
    fun getInTheatersMovies(): Call<MoviesResponse>

    @GET("movie/{movie_id}?append_to_response=trailers")
    fun getMovieDetails(@Path("movie_id") movieId: String?): Call<MovieDetails>

    @GET
    fun getOMDBRatings(@Url url: String,
                       @Query("i") movieId: String,
                       @Query("apikey") apiKey: String,
                       @Query("tomatoes") tomatoes: Boolean,
                       @Query("r") r: String,
    ): Call<RatingResponse>

    @GET("movie/{movie_id}/casts")
    fun getCasts(@Path("movie_id") movieId: String): Call<CastAndCrewResponse>

    @GET("movie/{movie_id}/recommendations")
    fun getSimilarMovies(@Path("movie_id") movieId: String): Call<SimilarMoviesResponse>

    @GET("person/{person_id}")
    fun getCastDetails(@Path("person_id") personId: String): Call<CastDetailsResponse>

    @GET("person/{person_id}/movie_credits")
    fun getCastMovieDetails(@Path("person_id") personId: String): Call<CastMovieDetailsResponse>

    @GET("search/movie")
    suspend fun searchMovies(@Query("query") query: String): Response<SearchResultResponse>
}