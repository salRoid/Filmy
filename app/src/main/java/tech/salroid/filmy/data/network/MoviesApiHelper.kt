package tech.salroid.filmy.data.network

import kotlinx.coroutines.flow.Flow
import tech.salroid.filmy.data.local.db.entity.MovieDetails
import tech.salroid.filmy.data.local.model.*

interface MoviesApiHelper {
    fun getTrending() : Flow<MoviesResponse>
    fun getUpcoming() : Flow<MoviesResponse>
    fun getInTheaters() : Flow<MoviesResponse>
    fun getMovieDetails(id: String) : Flow<MovieDetails>
    fun getOMDBRatings(id: String) : Flow<RatingResponse>
    fun getCastAndCew(id: String) : Flow<CastAndCrewResponse>
    fun getSimilar(id: String) : Flow<SimilarMoviesResponse>
    fun getCastCrewDetails(id: String) : Flow<CastCrewDetailsResponse>
    fun getCastCrewMovies(id: String) : Flow<CastCrewMoviesResponse>
    fun searchMovies(query: String) : Flow<SearchResultResponse>
}