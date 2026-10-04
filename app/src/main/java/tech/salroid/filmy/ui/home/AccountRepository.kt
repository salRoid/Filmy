package tech.salroid.filmy.ui.home

import android.content.SharedPreferences
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import tech.salroid.filmy.data.local.db.FilmyDatabase
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
import tech.salroid.filmy.data.network.AccountApiHelper
import tech.salroid.filmy.utility.PreferenceHelper.ACCOUNT_OBJECT_ID
import tech.salroid.filmy.utility.PreferenceHelper.SESSION_ID
import tech.salroid.filmy.utility.PreferenceHelper.USER_ACCESS_TOKEN
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

    /**
     * Reactive to the local `profile` table - unlike [getProfileFromLocal],
     * this stays in sync regardless of which ViewModel instance (Account
     * screen, MyLists, details screen, etc.) actually performed the
     * login/logout that wrote to it.
     */
    fun getProfileFlow(): Flow<Profile?> =
        filmyDatabase.accountDao().getProfileFlow().map { it.firstOrNull() }

    fun isLoggedIn(): Boolean = getSessionIdFromPref() != null

    /**
     * The v4 access token (and the v4 account id it belongs to) obtained at
     * login. Lists are the only feature that needs them; everything else runs
     * on the v3 session.
     */
    fun storeUserAccessToken(accessToken: String?, accountObjectId: String?) =
        appPref.edit()
            .putString(USER_ACCESS_TOKEN, accessToken)
            .putString(ACCOUNT_OBJECT_ID, accountObjectId)
            .apply()

    fun getUserAccessToken(): String? = appPref.getString(USER_ACCESS_TOKEN, null)

    /**
     * False for someone who logged in before lists moved to v4: they have a
     * session but no stored access token, and need to log in once more.
     */
    fun canManageLists(): Boolean =
        appPref.getString(USER_ACCESS_TOKEN, null) != null && appPref.getString(ACCOUNT_OBJECT_ID, null) != null

    /** [canManageLists], re-emitted whenever a login or logout changes it. */
    fun canManageListsFlow(): Flow<Boolean> = callbackFlow {
        trySend(canManageLists())
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == USER_ACCESS_TOKEN || key == ACCOUNT_OBJECT_ID) {
                trySend(canManageLists())
            }
        }
        appPref.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { appPref.unregisterOnSharedPreferenceChangeListener(listener) }
    }.distinctUntilChanged()

    fun revokeUserAccessToken(accessToken: String): Flow<TmdbStatusResponse> =
        accountApiHelper.revokeAccessToken(accessToken)

    private fun requireUserAccessToken(): String =
        checkNotNull(getUserAccessToken()) { "No TMDB user access token - log in again to use lists" }

    private fun requireAccountObjectId(): String =
        checkNotNull(appPref.getString(ACCOUNT_OBJECT_ID, null)) { "No TMDB v4 account id - log in again to use lists" }

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

    // --- Lists (TMDB v4, see AccountApiService) ---
    // Each call needs the user's v4 access token, so callers gate on
    // canManageLists() first; without it these fail rather than silently no-op.

    fun createList(name: String, description: String = ""): Flow<CreateListResponse> =
        accountApiHelper.createList(requireUserAccessToken(), name, description)

    fun getLists(page: Int = 1): Flow<TmdbListsResponse> =
        accountApiHelper.getLists(requireUserAccessToken(), requireAccountObjectId(), page)

    fun getListDetails(listId: Int, page: Int = 1): Flow<TmdbListDetailsResponse> =
        accountApiHelper.getListDetails(requireUserAccessToken(), listId, page)

    fun isInList(listId: Int, mediaId: Int, isTv: Boolean): Flow<Boolean> =
        accountApiHelper.isInList(requireUserAccessToken(), listId, mediaId, isTv)

    fun addToList(listId: Int, mediaId: Int, isTv: Boolean): Flow<ListItemsResponse> =
        accountApiHelper.addToList(requireUserAccessToken(), listId, mediaId, isTv)

    fun removeFromList(listId: Int, mediaId: Int, isTv: Boolean): Flow<ListItemsResponse> =
        accountApiHelper.removeFromList(requireUserAccessToken(), listId, mediaId, isTv)

    fun deleteList(listId: Int): Flow<TmdbStatusResponse> =
        accountApiHelper.deleteList(requireUserAccessToken(), listId)

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