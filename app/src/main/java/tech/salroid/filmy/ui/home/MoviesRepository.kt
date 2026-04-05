package tech.salroid.filmy.ui.home

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import tech.salroid.filmy.data.local.db.FilmyDatabase
import tech.salroid.filmy.data.local.db.entity.Movie
import tech.salroid.filmy.data.local.db.entity.MovieDetails
import tech.salroid.filmy.data.local.model.*
import tech.salroid.filmy.data.local.model.tv.TvDetails
import tech.salroid.filmy.data.local.model.watch_providers.WatchProviderResponse
import tech.salroid.filmy.data.network.MoviesApiHelper
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import tech.salroid.filmy.ui.widget.WidgetRefresher

@Singleton
class MoviesRepository @Inject constructor(
    private val filmyDatabase: FilmyDatabase,
    private val moviesApiHelper: MoviesApiHelper,
    @param:ApplicationContext private val context: Context
) {
    private val repositoryScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private fun refreshWidget() {
        repositoryScope.launch {
            WidgetRefresher.refresh(context)
        }
    }

    fun getMovies(type: String, isTrending: Boolean): Flow<PagingData<Movie>> =
        moviesApiHelper.getMovies(type, isTrending)

    fun getMoviesFlow(type: String, isTrending: Boolean): Flow<Result<MoviesResponse>> =
        moviesApiHelper.getMoviesFlow(type, isTrending)
            .map { response ->
                Result.success(response)
            }
            .catch { throwable ->
                emit(Result.failure(throwable))
            }

    fun getTvShows(type: String, isTrending: Boolean): Flow<PagingData<TvShow>> =
        moviesApiHelper.getTvShows(type, isTrending)

    fun getTvShowsFlow(type: String, isTrending: Boolean): Flow<Result<TvShowResponse>> =
        moviesApiHelper.getTvShowsFlow(type, isTrending)
            .map { response ->
                Result.success(response)
            }.catch { throwable ->
                emit(Result.failure(throwable))
            }

    fun getMovieDetailsFromLocal(id: Int, type: Int): MovieDetails? {
        return filmyDatabase.movieDetailsDao().getDetailsOfType(id, type)
    }

    fun getMovieDetailsFlow(id: Int, type: Int): Flow<MovieDetails?> =
        filmyDatabase.movieDetailsDao().getDetailsFlow(id, type)

    fun getRatings(id: String): Flow<RatingResponse> = moviesApiHelper.getOMDBRatings(id)

    fun getMovieDetailsFromNetwork(id: String): Flow<MovieDetails> {
        return moviesApiHelper.getMovieDetails(id)
    }

    fun getTvShowDetailsFromNetwork(id: String): Flow<TvDetails> {
        return moviesApiHelper.getTvShowDetails(id)
    }

    fun getCastAndCrew(id: String): Flow<CastAndCrewResponse> = moviesApiHelper.getCastAndCrew(id)

    fun getCastAndCrewTv(id: String): Flow<CastAndCrewResponse> =
        moviesApiHelper.getCastAndCrewTv(id)

    fun getCastCrewDetails(id: String): Flow<CastCrewDetailsResponse> =
        moviesApiHelper.getCastCrewDetails(id)

    fun getCastCrewMovies(id: String): Flow<CastCrewMoviesResponse> =
        moviesApiHelper.getCastCrewMovies(id)

    fun getCastCrewTvShows(id: String): Flow<CastCrewMoviesResponse> =
        moviesApiHelper.getCastCrewTvShows(id)

    fun getSimilar(id: String): Flow<SimilarMoviesResponse> = moviesApiHelper.getSimilar(id)

    fun getSimilarTv(id: String): Flow<SimilarMoviesResponse> = moviesApiHelper.getSimilarTv(id)

    fun getRecommendation(id: String): Flow<SimilarMoviesResponse> =
        moviesApiHelper.getRecommendation(id)

    fun getRecommendationTv(id: String): Flow<SimilarMoviesResponse> =
        moviesApiHelper.getRecommendationTv(id)

    fun searchMovies(query: String): Flow<SearchResultResponse> =
        moviesApiHelper.searchMovies(query)

    fun searchMoviesFlow(query: String): Flow<Result<SearchResultResponse>> =
        moviesApiHelper.searchMovies(query).map {
            Result.success(it)
        }.catch {
            emit(Result.failure(it))
        }

    fun searchMultiFlow(query: String): Flow<Result<SearchResultResponse>> =
        moviesApiHelper.searchMulti(query).map {
            Result.success(it)
        }.catch {
            emit(Result.failure(it))
        }

    fun addMovieDetailsToLocal(movieDetails: MovieDetails) {
        filmyDatabase.movieDetailsDao().insert(movieDetails)
        refreshWidget()
    }

    fun getWatched(): Flow<List<MovieDetails>> = filmyDatabase.movieDetailsDao().getAllWatched()

    fun getWatchlist(): Flow<List<MovieDetails>> = filmyDatabase.movieDetailsDao().getAllWatchlist()

    fun updateMovieDetails(movieDetails: MovieDetails): Int {
        val result = filmyDatabase.movieDetailsDao().updateDetails(movieDetails)
        refreshWidget()
        return result
    }

    fun addAllMoviesToDb(movies: List<Movie>) {
        filmyDatabase.movieDao().insertAll(movies)
    }

    fun getReviews(id: String): Flow<ReviewResponse> = moviesApiHelper.getReviews(id)
    fun getTvReviews(id: String): Flow<ReviewResponse> = moviesApiHelper.getTvReviews(id)

    fun getWatchProviders(id: String): Flow<WatchProviderResponse> =
        moviesApiHelper.getWatchProviders(id)

    fun getWatchProvidersTv(id: String): Flow<WatchProviderResponse> =
        moviesApiHelper.getWatchProvidersTv(id)
}
