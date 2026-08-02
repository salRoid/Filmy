package tech.salroid.filmy.data.network

import kotlinx.coroutines.flow.Flow
import tech.salroid.filmy.data.local.db.entity.Profile
import tech.salroid.filmy.data.local.model.login.*

interface AccountApiHelper {
    fun getRequestToken(requestTokenData: RequestTokenData): Flow<RequestTokenResponse>
    fun getAccessToken(requestTokenData: RequestTokenData): Flow<RequestTokenResponse>
    fun getSession(accessTokenData: AccessTokenData): Flow<SessionDataResponse>
    fun deleteSession(sessionId: String): Flow<DeleteSession>
    fun getProfile(sessionId: String): Flow<Profile>
}