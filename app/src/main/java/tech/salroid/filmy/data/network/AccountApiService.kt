package tech.salroid.filmy.data.network

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import tech.salroid.filmy.data.local.db.entity.Profile
import tech.salroid.filmy.data.local.model.*
import tech.salroid.filmy.data.local.model.login.*

interface AccountApiService {

    @POST("auth/request_token")
    suspend fun getRequestToken(@Body data: RequestTokenData): RequestTokenResponse

    @POST("auth/access_token")
    suspend fun getAccessToken(@Body request: RequestTokenData): RequestTokenResponse

    @POST("authentication/session/convert/4")
    suspend fun getSession(@Body request: AccessTokenData): SessionDataResponse

    @DELETE("authentication/session")
    suspend fun deleteSession(@Query("session_id") sessionId: String): DeleteSession

    @GET("account")
    suspend fun getProfile(@Query("session_id") sessionId: String): Profile
}