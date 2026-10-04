package tech.salroid.filmy.data.network

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import tech.salroid.filmy.data.local.db.entity.Profile
import tech.salroid.filmy.data.local.model.MoviesResponse
import tech.salroid.filmy.data.local.model.TvShowResponse
import tech.salroid.filmy.data.local.model.account.CreateListRequest
import tech.salroid.filmy.data.local.model.account.CreateListResponse
import tech.salroid.filmy.data.local.model.account.FavoriteRequest
import tech.salroid.filmy.data.local.model.account.ListItemRef
import tech.salroid.filmy.data.local.model.account.ListItemsRequest
import tech.salroid.filmy.data.local.model.account.ListItemsResponse
import tech.salroid.filmy.data.local.model.account.RatedResponse
import tech.salroid.filmy.data.local.model.account.RatingRequest
import tech.salroid.filmy.data.local.model.account.TmdbListDetailsResponse
import tech.salroid.filmy.data.local.model.account.TmdbListsResponse
import tech.salroid.filmy.data.local.model.account.TmdbStatusResponse
import tech.salroid.filmy.data.local.model.account.WatchlistRequest
import tech.salroid.filmy.data.local.model.login.*

private const val HTTP_NOT_FOUND = 404

class AccountApiHelperImpl(private val accountApiService: AccountApiService) : AccountApiHelper {

    private fun bearer(accessToken: String) = "Bearer $accessToken"

    private fun listItemsOf(mediaId: Int, isTv: Boolean) =
        ListItemsRequest(listOf(ListItemRef(ListItemRef.mediaTypeOf(isTv), mediaId)))

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
            emit(accountApiService.deleteSession(DeleteSession(sessionId = sessionId)))
        }

    override fun revokeAccessToken(accessToken: String): Flow<TmdbStatusResponse> =
        flow {
            emit(accountApiService.revokeAccessToken(AccessTokenData(accessToken = accessToken)))
        }

    override fun getProfile(sessionId: String): Flow<Profile> =
        flow {
            emit(accountApiService.getProfile(sessionId))
        }

    override fun markAsFavorite(
        accountId: Int,
        sessionId: String,
        mediaType: String,
        mediaId: Int,
        favorite: Boolean
    ): Flow<TmdbStatusResponse> =
        flow {
            emit(
                accountApiService.markAsFavorite(
                    accountId,
                    sessionId,
                    FavoriteRequest(mediaType = mediaType, mediaId = mediaId, favorite = favorite)
                )
            )
        }

    override fun markAsWatchlist(
        accountId: Int,
        sessionId: String,
        mediaType: String,
        mediaId: Int,
        watchlist: Boolean
    ): Flow<TmdbStatusResponse> =
        flow {
            emit(
                accountApiService.markAsWatchlist(
                    accountId,
                    sessionId,
                    WatchlistRequest(mediaType = mediaType, mediaId = mediaId, watchlist = watchlist)
                )
            )
        }

    override fun getFavoriteMovies(accountId: Int, sessionId: String, page: Int): Flow<MoviesResponse> =
        flow {
            emit(accountApiService.getFavoriteMovies(accountId, sessionId, page))
        }

    override fun getFavoriteTv(accountId: Int, sessionId: String, page: Int): Flow<TvShowResponse> =
        flow {
            emit(accountApiService.getFavoriteTv(accountId, sessionId, page))
        }

    override fun getWatchlistMovies(accountId: Int, sessionId: String, page: Int): Flow<MoviesResponse> =
        flow {
            emit(accountApiService.getWatchlistMovies(accountId, sessionId, page))
        }

    override fun getWatchlistTv(accountId: Int, sessionId: String, page: Int): Flow<TvShowResponse> =
        flow {
            emit(accountApiService.getWatchlistTv(accountId, sessionId, page))
        }

    override fun createList(accessToken: String, name: String, description: String): Flow<CreateListResponse> =
        flow {
            emit(
                accountApiService.createList(
                    bearer(accessToken),
                    CreateListRequest(name = name, description = description)
                )
            )
        }

    override fun getLists(accessToken: String, accountObjectId: String, page: Int): Flow<TmdbListsResponse> =
        flow {
            emit(accountApiService.getLists(bearer(accessToken), accountObjectId, page))
        }

    override fun getListDetails(accessToken: String, listId: Int, page: Int): Flow<TmdbListDetailsResponse> =
        flow {
            emit(accountApiService.getListDetails(bearer(accessToken), listId, page))
        }

    override fun isInList(accessToken: String, listId: Int, mediaId: Int, isTv: Boolean): Flow<Boolean> =
        flow {
            val isMember = try {
                accountApiService.getListItemStatus(
                    bearer(accessToken), listId, mediaId, ListItemRef.mediaTypeOf(isTv)
                )
                true
            } catch (e: HttpException) {
                // 404 is TMDB's "not in this list"; anything else is a real failure.
                if (e.code() == HTTP_NOT_FOUND) false else throw e
            }
            emit(isMember)
        }

    override fun addToList(accessToken: String, listId: Int, mediaId: Int, isTv: Boolean): Flow<ListItemsResponse> =
        flow {
            emit(accountApiService.addToList(bearer(accessToken), listId, listItemsOf(mediaId, isTv)))
        }

    override fun removeFromList(accessToken: String, listId: Int, mediaId: Int, isTv: Boolean): Flow<ListItemsResponse> =
        flow {
            emit(accountApiService.removeFromList(bearer(accessToken), listId, listItemsOf(mediaId, isTv)))
        }

    override fun deleteList(accessToken: String, listId: Int): Flow<TmdbStatusResponse> =
        flow {
            emit(accountApiService.deleteList(bearer(accessToken), listId))
        }

    override fun rateMovie(movieId: Int, sessionId: String, value: Float): Flow<TmdbStatusResponse> =
        flow {
            emit(accountApiService.rateMovie(movieId, sessionId, RatingRequest(value = value)))
        }

    override fun deleteMovieRating(movieId: Int, sessionId: String): Flow<TmdbStatusResponse> =
        flow {
            emit(accountApiService.deleteMovieRating(movieId, sessionId))
        }

    override fun rateTv(tvId: Int, sessionId: String, value: Float): Flow<TmdbStatusResponse> =
        flow {
            emit(accountApiService.rateTv(tvId, sessionId, RatingRequest(value = value)))
        }

    override fun deleteTvRating(tvId: Int, sessionId: String): Flow<TmdbStatusResponse> =
        flow {
            emit(accountApiService.deleteTvRating(tvId, sessionId))
        }

    override fun getRatedMovies(accountId: Int, sessionId: String, page: Int): Flow<RatedResponse> =
        flow {
            emit(accountApiService.getRatedMovies(accountId, sessionId, page))
        }

    override fun getRatedTv(accountId: Int, sessionId: String, page: Int): Flow<RatedResponse> =
        flow {
            emit(accountApiService.getRatedTv(accountId, sessionId, page))
        }
}