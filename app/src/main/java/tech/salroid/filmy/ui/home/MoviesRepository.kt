package tech.salroid.filmy.ui.home

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import tech.salroid.filmy.data.local.db.FilmyDatabase
import tech.salroid.filmy.data.local.db.entity.Movie
import tech.salroid.filmy.data.local.db.entity.MovieDetails
import tech.salroid.filmy.data.local.model.*
import tech.salroid.filmy.data.local.model.tv.TvDetails
import tech.salroid.filmy.data.local.model.watch_providers.WatchProviderResponse
import tech.salroid.filmy.data.network.MoviesApiHelper
import javax.inject.Inject

class MoviesRepository @Inject constructor(
    private val filmyDatabase: FilmyDatabase,
    private val moviesApiHelper: MoviesApiHelper
) {
    fun getMovies(type: String, isTrending: Boolean): Flow<PagingData<Movie>> =
        moviesApiHelper.getMovies(type, isTrending)

    fun getTvShows(type: String, isTrending: Boolean): Flow<PagingData<TvShow>> =
        moviesApiHelper.getTvShows(type, isTrending)

    fun getMovieDetailsFromLocal(id: Int, type: Int): MovieDetails? {
        return filmyDatabase.movieDetailsDao().getDetailsOfType(id, type)
    }

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

    fun addMovieDetailsToLocal(movieDetails: MovieDetails) {
        return filmyDatabase.movieDetailsDao().insert(movieDetails)
    }

    fun getFavorites(): List<MovieDetails> = filmyDatabase.movieDetailsDao().getAllFavorites()

    fun getWatchlist(): List<MovieDetails> = filmyDatabase.movieDetailsDao().getAllWatchlist()

    fun updateMovieDetails(movieDetails: MovieDetails): Int =
        filmyDatabase.movieDetailsDao().updateDetails(movieDetails)

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