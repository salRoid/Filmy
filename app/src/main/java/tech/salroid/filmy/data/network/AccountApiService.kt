package tech.salroid.filmy.data.network

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import tech.salroid.filmy.data.local.db.entity.Profile
import tech.salroid.filmy.data.local.model.MoviesResponse
import tech.salroid.filmy.data.local.model.TvShowResponse
import tech.salroid.filmy.data.local.model.account.CreateListRequest
import tech.salroid.filmy.data.local.model.account.CreateListResponse
import tech.salroid.filmy.data.local.model.account.FavoriteRequest
import tech.salroid.filmy.data.local.model.account.ListItemRequest
import tech.salroid.filmy.data.local.model.account.RatedResponse
import tech.salroid.filmy.data.local.model.account.RatingRequest
import tech.salroid.filmy.data.local.model.account.TmdbListDetailsResponse
import tech.salroid.filmy.data.local.model.account.TmdbListsResponse
import tech.salroid.filmy.data.local.model.account.TmdbStatusResponse
import tech.salroid.filmy.data.local.model.account.WatchlistRequest
import tech.salroid.filmy.data.local.model.login.*

interface AccountApiService {

    @POST("auth/request_token")
    suspend fun getRequestToken(@Body data: RequestTokenData): RequestTokenResponse

    @POST("auth/access_token")
    suspend fun getAccessToken(@Body request: RequestTokenData): RequestTokenResponse

    @POST("authentication/session/convert/4")
    suspend fun getSession(@Body request: AccessTokenData): SessionDataResponse

    @DELETE("authentication/session")
    suspend fun deleteSession(@Body request: DeleteSession): DeleteSession

    @GET("account")
    suspend fun getProfile(@Query("session_id") sessionId: String): Profile

    @POST("account/{account_id}/favorite")
    suspend fun markAsFavorite(
        @Path("account_id") accountId: Int,
        @Query("session_id") sessionId: String,
        @Body request: FavoriteRequest
    ): TmdbStatusResponse

    @POST("account/{account_id}/watchlist")
    suspend fun markAsWatchlist(
        @Path("account_id") accountId: Int,
        @Query("session_id") sessionId: String,
        @Body request: WatchlistRequest
    ): TmdbStatusResponse

    @GET("account/{account_id}/favorite/movies")
    suspend fun getFavoriteMovies(
        @Path("account_id") accountId: Int,
        @Query("session_id") sessionId: String,
        @Query("page") page: Int
    ): MoviesResponse

    @GET("account/{account_id}/favorite/tv")
    suspend fun getFavoriteTv(
        @Path("account_id") accountId: Int,
        @Query("session_id") sessionId: String,
        @Query("page") page: Int
    ): TvShowResponse

    @GET("account/{account_id}/watchlist/movies")
    suspend fun getWatchlistMovies(
        @Path("account_id") accountId: Int,
        @Query("session_id") sessionId: String,
        @Query("page") page: Int
    ): MoviesResponse

    @GET("account/{account_id}/watchlist/tv")
    suspend fun getWatchlistTv(
        @Path("account_id") accountId: Int,
        @Query("session_id") sessionId: String,
        @Query("page") page: Int
    ): TvShowResponse

    @POST("list")
    suspend fun createList(
        @Query("session_id") sessionId: String,
        @Body request: CreateListRequest
    ): CreateListResponse

    @GET("account/{account_id}/lists")
    suspend fun getLists(
        @Path("account_id") accountId: Int,
        @Query("session_id") sessionId: String,
        @Query("page") page: Int
    ): TmdbListsResponse

    @GET("list/{list_id}")
    suspend fun getListDetails(
        @Path("list_id") listId: Int,
        @Query("session_id") sessionId: String,
        @Query("page") page: Int
    ): TmdbListDetailsResponse

    @POST("list/{list_id}/add_item")
    suspend fun addToList(
        @Path("list_id") listId: Int,
        @Query("session_id") sessionId: String,
        @Body request: ListItemRequest
    ): TmdbStatusResponse

    @POST("list/{list_id}/remove_item")
    suspend fun removeFromList(
        @Path("list_id") listId: Int,
        @Query("session_id") sessionId: String,
        @Body request: ListItemRequest
    ): TmdbStatusResponse

    @DELETE("list/{list_id}")
    suspend fun deleteList(
        @Path("list_id") listId: Int,
        @Query("session_id") sessionId: String
    ): TmdbStatusResponse

    @POST("movie/{movie_id}/rating")
    suspend fun rateMovie(
        @Path("movie_id") movieId: Int,
        @Query("session_id") sessionId: String,
        @Body request: RatingRequest
    ): TmdbStatusResponse

    @DELETE("movie/{movie_id}/rating")
    suspend fun deleteMovieRating(
        @Path("movie_id") movieId: Int,
        @Query("session_id") sessionId: String
    ): TmdbStatusResponse

    @POST("tv/{tv_id}/rating")
    suspend fun rateTv(
        @Path("tv_id") tvId: Int,
        @Query("session_id") sessionId: String,
        @Body request: RatingRequest
    ): TmdbStatusResponse

    @DELETE("tv/{tv_id}/rating")
    suspend fun deleteTvRating(
        @Path("tv_id") tvId: Int,
        @Query("session_id") sessionId: String
    ): TmdbStatusResponse

    @GET("account/{account_id}/rated/movies")
    suspend fun getRatedMovies(
        @Path("account_id") accountId: Int,
        @Query("session_id") sessionId: String,
        @Query("page") page: Int
    ): RatedResponse

    @GET("account/{account_id}/rated/tv")
    suspend fun getRatedTv(
        @Path("account_id") accountId: Int,
        @Query("session_id") sessionId: String,
        @Query("page") page: Int
    ): RatedResponse
}