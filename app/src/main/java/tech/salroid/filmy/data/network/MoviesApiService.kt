package tech.salroid.filmy.data.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url
import tech.salroid.filmy.data.local.db.entity.MovieDetails
import tech.salroid.filmy.data.local.model.*
import tech.salroid.filmy.data.local.model.tv.TvDetails
import tech.salroid.filmy.data.local.model.watch_providers.WatchProviderResponse

interface MoviesApiService {

    companion object {
        var BASE_URL = "https://api.themoviedb.org/3/"
        var BASE_URL_OMDB = "https://www.omdbapi.com/"
    }

    @GET("trending/movie/{time_window}")
    suspend fun getTrendingMovies(
        @Path("time_window") timeWindow: String,
        @Query("page") page: Int
    ): MoviesResponse

    @GET("movie/{movie_type}")
    suspend fun getAllMovies(
        @Path("movie_type") movieType: String,
        @Query("page") page: Int
    ): MoviesResponse

    @GET("trending/tv/{time_window}")
    suspend fun getTrendingTvShows(
        @Path("time_window") timeWindow: String,
        @Query("page") page: Int
    ): TvShowResponse

    @GET("tv/{tv_type}")
    suspend fun getAllTvShows(
        @Path("tv_type") movieType: String,
        @Query("page") page: Int
    ): TvShowResponse


    @GET("movie/{movie_id}?append_to_response=videos")
    suspend fun getMovieDetails(@Path("movie_id") movieId: String?): MovieDetails

    @GET("tv/{show_id}?append_to_response=videos")
    suspend fun getTvShowDetails(@Path("show_id") showId: String?): TvDetails

    @GET
    suspend fun getOMDBRatings(
        @Url url: String,
        @Query("i") movieId: String,
        @Query("apikey") apiKey: String,
        @Query("tomatoes") tomatoes: Boolean,
        @Query("r") r: String,
    ): RatingResponse

    @GET("movie/{movie_id}/casts")
    suspend fun getCasts(@Path("movie_id") movieId: String): CastAndCrewResponse

    @GET("tv/{tv_id}/credits")
    suspend fun getCastsTv(@Path("tv_id") movieId: String): CastAndCrewResponse

    @GET("movie/{movie_id}/similar")
    suspend fun getSimilarMovies(@Path("movie_id") movieId: String): SimilarMoviesResponse

    @GET("movie/{movie_id}/recommendations")
    suspend fun getRecommendation(@Path("movie_id") movieId: String): SimilarMoviesResponse

    @GET("tv/{tv_id}/similar")
    suspend fun getSimilarTv(@Path("tv_id") movieId: String): SimilarMoviesResponse

    @GET("tv/{tv_id}/recommendations")
    suspend fun getRecommendationTv(@Path("tv_id") movieId: String): SimilarMoviesResponse

    @GET("person/{person_id}")
    suspend fun getCastCrewDetails(@Path("person_id") personId: String): CastCrewDetailsResponse

    @GET("person/{person_id}/movie_credits")
    suspend fun getCastCrewMovies(@Path("person_id") personId: String): CastCrewMoviesResponse

    @GET("person/{person_id}/tv_credits")
    suspend fun getCastCrewTvShows(@Path("person_id") personId: String): CastCrewMoviesResponse

    @GET("search/movie")
    suspend fun searchMovies(@Query("query") query: String): SearchResultResponse

    @GET("search/multi")
    suspend fun searchMulti(@Query("query") query: String): SearchResultResponse

    @GET("movie/{movie_id}/reviews")
    suspend fun getReviews(@Path("movie_id") movieId: String): ReviewResponse

    @GET("tv/{tv_id}/reviews")
    suspend fun getTvReviews(@Path("tv_id") tvId: String): ReviewResponse

    @GET("movie/{movie_id}/watch/providers")
    suspend fun getWatchProviders(@Path("movie_id") movieId: String): WatchProviderResponse

    @GET("tv/{tv_id}/watch/providers")
    suspend fun getWatchProvidersTv(@Path("tv_id") movieId: String): WatchProviderResponse
}