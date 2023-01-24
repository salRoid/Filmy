package tech.salroid.filmy.data.network

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import tech.salroid.filmy.BuildConfig.OMDB_API_KEY
import tech.salroid.filmy.data.local.db.entity.MovieDetails
import tech.salroid.filmy.data.local.model.*
import tech.salroid.filmy.data.network.MoviesApiService.Companion.BASE_URL_OMDB

class MoviesApiHelperImpl(private val apiService: MoviesApiService) : MoviesApiHelper {

    override fun getTrending(): Flow<MoviesResponse> = flow {
        emit(apiService.getTrending())
    }

    override fun getInTheaters(): Flow<MoviesResponse> = flow {
        emit(apiService.getInTheaters())
    }

    override fun getUpcoming(): Flow<MoviesResponse> = flow {
        emit(apiService.getUpcoming())
    }

    override fun getMovieDetails(id: String): Flow<MovieDetails> = flow {
        emit(apiService.getMovieDetails(id))
    }

    override fun getOMDBRatings(id: String): Flow<RatingResponse> = flow {
        emit(
            apiService.getOMDBRatings(BASE_URL_OMDB, id, OMDB_API_KEY, true, "json")
        )
    }

    override fun getCastAndCew(id: String): Flow<CastAndCrewResponse> = flow {
        emit(apiService.getCasts(id))
    }

    override fun getSimilar(id: String): Flow<SimilarMoviesResponse> = flow {
        emit(apiService.getSimilarMovies(id))
    }

    override fun getCastCrewDetails(id: String): Flow<CastCrewDetailsResponse> = flow {
        emit(apiService.getCastCrewDetails(id))
    }

    override fun getCastCrewMovies(id: String): Flow<CastCrewMoviesResponse> = flow {
        emit(apiService.getCastCrewMovies(id))
    }

    override fun searchMovies(query: String): Flow<SearchResultResponse> = flow {
        emit(apiService.searchMovies(query))
    }
}