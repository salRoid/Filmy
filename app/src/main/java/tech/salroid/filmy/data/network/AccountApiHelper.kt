package tech.salroid.filmy.data.network

import kotlinx.coroutines.flow.Flow
import tech.salroid.filmy.data.local.db.entity.Profile
import tech.salroid.filmy.data.local.model.MoviesResponse
import tech.salroid.filmy.data.local.model.TvShowResponse
import tech.salroid.filmy.data.local.model.account.CreateListResponse
import tech.salroid.filmy.data.local.model.account.ListItemsResponse
import tech.salroid.filmy.data.local.model.account.RatedResponse
import tech.salroid.filmy.data.local.model.account.TmdbListDetailsResponse
import tech.salroid.filmy.data.local.model.account.TmdbListsResponse
import tech.salroid.filmy.data.local.model.account.TmdbStatusResponse
import tech.salroid.filmy.data.local.model.login.*

interface AccountApiHelper {
    fun getRequestToken(requestTokenData: RequestTokenData): Flow<RequestTokenResponse>
    fun getAccessToken(requestTokenData: RequestTokenData): Flow<RequestTokenResponse>
    fun getSession(accessTokenData: AccessTokenData): Flow<SessionDataResponse>
    fun deleteSession(sessionId: String): Flow<DeleteSession>
    fun revokeAccessToken(accessToken: String): Flow<TmdbStatusResponse>
    fun getProfile(sessionId: String): Flow<Profile>

    fun markAsFavorite(
        accountId: Int,
        sessionId: String,
        mediaType: String,
        mediaId: Int,
        favorite: Boolean
    ): Flow<TmdbStatusResponse>

    fun markAsWatchlist(
        accountId: Int,
        sessionId: String,
        mediaType: String,
        mediaId: Int,
        watchlist: Boolean
    ): Flow<TmdbStatusResponse>

    fun getFavoriteMovies(accountId: Int, sessionId: String, page: Int): Flow<MoviesResponse>
    fun getFavoriteTv(accountId: Int, sessionId: String, page: Int): Flow<TvShowResponse>
    fun getWatchlistMovies(accountId: Int, sessionId: String, page: Int): Flow<MoviesResponse>
    fun getWatchlistTv(accountId: Int, sessionId: String, page: Int): Flow<TvShowResponse>

    // Lists - TMDB v4, authorised with the user's own access token.
    fun createList(accessToken: String, name: String, description: String): Flow<CreateListResponse>
    fun getLists(accessToken: String, accountObjectId: String, page: Int): Flow<TmdbListsResponse>
    fun getListDetails(accessToken: String, listId: Int, page: Int): Flow<TmdbListDetailsResponse>
    fun isInList(accessToken: String, listId: Int, mediaId: Int, isTv: Boolean): Flow<Boolean>
    fun addToList(accessToken: String, listId: Int, mediaId: Int, isTv: Boolean): Flow<ListItemsResponse>
    fun removeFromList(accessToken: String, listId: Int, mediaId: Int, isTv: Boolean): Flow<ListItemsResponse>
    fun deleteList(accessToken: String, listId: Int): Flow<TmdbStatusResponse>

    fun rateMovie(movieId: Int, sessionId: String, value: Float): Flow<TmdbStatusResponse>
    fun deleteMovieRating(movieId: Int, sessionId: String): Flow<TmdbStatusResponse>
    fun rateTv(tvId: Int, sessionId: String, value: Float): Flow<TmdbStatusResponse>
    fun deleteTvRating(tvId: Int, sessionId: String): Flow<TmdbStatusResponse>
    fun getRatedMovies(accountId: Int, sessionId: String, page: Int): Flow<RatedResponse>
    fun getRatedTv(accountId: Int, sessionId: String, page: Int): Flow<RatedResponse>
}