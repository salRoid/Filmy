package tech.salroid.filmy.ui.home

import android.content.SharedPreferences
import kotlinx.coroutines.flow.Flow
import tech.salroid.filmy.data.local.db.FilmyDatabase
import tech.salroid.filmy.data.local.db.entity.Profile
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
}