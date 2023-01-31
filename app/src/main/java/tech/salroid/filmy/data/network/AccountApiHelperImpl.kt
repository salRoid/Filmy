package tech.salroid.filmy.data.network

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import tech.salroid.filmy.data.local.db.entity.Profile
import tech.salroid.filmy.data.local.model.login.*

class AccountApiHelperImpl(private val accountApiService: AccountApiService) : AccountApiHelper {

    override fun getRequestToken(requestTokenData: RequestTokenData): Flow<RequestTokenResponse> =
        flow {
            emit(accountApiService.getRequestToken(requestTokenData))
        }

    override fun getAccessToken(requestTokenData: RequestTokenData): Flow<RequestTokenResponse> =
        flow {
            emit(accountApiService.getAccessToken(requestTokenData))
        }

    override fun getSession(accessTokenData: AccessTokenData): Flow<SessionDataResponse> =
        flow {
            emit(accountApiService.getSession(accessTokenData))
        }

    override fun deleteSession(sessionId: String): Flow<DeleteSession> =
        flow {
            emit(accountApiService.deleteSession(sessionId))
        }

    override fun getProfile(sessionId: String): Flow<Profile> =
        flow {
            emit(accountApiService.getProfile(sessionId))
        }
}