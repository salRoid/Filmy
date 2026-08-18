package tech.salroid.filmy.ui.home

import android.content.SharedPreferences
import kotlinx.coroutines.flow.Flow
import tech.salroid.filmy.data.local.db.FilmyDatabase
import tech.salroid.filmy.data.local.db.entity.Profile
import tech.salroid.filmy.data.local.model.MoviesResponse
import tech.salroid.filmy.data.local.model.TvShowResponse
import tech.salroid.filmy.data.local.model.account.CreateListResponse
import tech.salroid.filmy.data.local.model.account.RatedResponse
import tech.salroid.filmy.data.local.model.account.TmdbListDetailsResponse
import tech.salroid.filmy.data.local.model.account.TmdbListsResponse
import tech.salroid.filmy.data.local.model.account.TmdbStatusResponse
import tech.salroid.filmy.data.local.model.login.*
import tech.salroid.filmy.data.network.AccountApiHelper
import tech.salroid.filmy.utility.PreferenceHelper.SESSION_ID
import javax.inject.Inject

class AccountRepository @Inject constructor(
    private val appPref: SharedPreferences,
    private val filmyDatabase: FilmyDatabase,
    private val accountApiHelper: AccountApiHelper
) {

    fun getRequestToken(requestTokenData: RequestTokenData): Flow<RequestTokenResponse> =
        accountApiHelper.getRequestToken(requestTokenData)

    fun getAccessToken(requestTokenData: RequestTokenData): Flow<RequestTokenResponse> =
        accountApiHelper.getAccessToken(requestTokenData)

    fun getSession(accessTokenData: AccessTokenData): Flow<SessionDataResponse> =
        accountApiHelper.getSession(accessTokenData)

    fun deleteSession(sessionId: String): Flow<DeleteSession> =
        accountApiHelper.deleteSession(sessionId)

    fun getProfile(sessionId: String): Flow<Profile> =
        accountApiHelper.getProfile(sessionId)

    fun clearProfile(): Int =
        filmyDatabase.accountDao().deleteAll()

    fun saveProfileToLocal(profile: Profile) {
        filmyDatabase.accountDao().apply {
            delete(profile)
            insert(profile)
        }
    }

    fun storeSessionId(sessionId: String?) = appPref.edit().putString(SESSION_ID, sessionId).apply()

    fun getSessionIdFromPref(): String? = appPref.getString(SESSION_ID, null)

    fun getProfileFromLocal(): Profile? = filmyDatabase.accountDao().getProfile().firstOrNull()

    fun isLoggedIn(): Boolean = getSessionIdFromPref() != null

    fun markFavorite(
        accountId: Int,
        sessionId: String,
        mediaType: String,
        mediaId: Int,
        favorite: Boolean
    ): Flow<TmdbStatusResponse> =
        accountApiHelper.markAsFavorite(accountId, sessionId, mediaType, mediaId, favorite)

    fun markWatchlist(
        accountId: Int,
        sessionId: String,
        mediaType: String,
        mediaId: Int,
        watchlist: Boolean
    ): Flow<TmdbStatusResponse> =
        accountApiHelper.markAsWatchlist(accountId, sessionId, mediaType, mediaId, watchlist)

    fun getFavoriteMovies(accountId: Int, sessionId: String, page: Int): Flow<MoviesResponse> =
        accountApiHelper.getFavoriteMovies(accountId, sessionId, page)

    fun getFavoriteTv(accountId: Int, sessionId: String, page: Int): Flow<TvShowResponse> =
        accountApiHelper.getFavoriteTv(accountId, sessionId, page)

    fun getWatchlistMovies(accountId: Int, sessionId: String, page: Int): Flow<MoviesResponse> =
        accountApiHelper.getWatchlistMovies(accountId, sessionId, page)

    fun getWatchlistTv(accountId: Int, sessionId: String, page: Int): Flow<TvShowResponse> =
        accountApiHelper.getWatchlistTv(accountId, sessionId, page)

    fun createList(sessionId: String, name: String, description: String = ""): Flow<CreateListResponse> =
        accountApiHelper.createList(sessionId, name, description)

    fun getLists(accountId: Int, sessionId: String, page: Int = 1): Flow<TmdbListsResponse> =
        accountApiHelper.getLists(accountId, sessionId, page)

    fun getListDetails(listId: Int, sessionId: String, page: Int = 1): Flow<TmdbListDetailsResponse> =
        accountApiHelper.getListDetails(listId, sessionId, page)

    fun addToList(listId: Int, sessionId: String, mediaId: Int): Flow<TmdbStatusResponse> =
        accountApiHelper.addToList(listId, sessionId, mediaId)

    fun removeFromList(listId: Int, sessionId: String, mediaId: Int): Flow<TmdbStatusResponse> =
        accountApiHelper.removeFromList(listId, sessionId, mediaId)

    fun deleteList(listId: Int, sessionId: String): Flow<TmdbStatusResponse> =
        accountApiHelper.deleteList(listId, sessionId)

    fun rateMovie(movieId: Int, sessionId: String, value: Float): Flow<TmdbStatusResponse> =
        accountApiHelper.rateMovie(movieId, sessionId, value)

    fun deleteMovieRating(movieId: Int, sessionId: String): Flow<TmdbStatusResponse> =
        accountApiHelper.deleteMovieRating(movieId, sessionId)

    fun rateTv(tvId: Int, sessionId: String, value: Float): Flow<TmdbStatusResponse> =
        accountApiHelper.rateTv(tvId, sessionId, value)

    fun deleteTvRating(tvId: Int, sessionId: String): Flow<TmdbStatusResponse> =
        accountApiHelper.deleteTvRating(tvId, sessionId)

    fun getRatedMovies(accountId: Int, sessionId: String, page: Int): Flow<RatedResponse> =
        accountApiHelper.getRatedMovies(accountId, sessionId, page)

    fun getRatedTv(accountId: Int, sessionId: String, page: Int): Flow<RatedResponse> =
        accountApiHelper.getRatedTv(accountId, sessionId, page)
}