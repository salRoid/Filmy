package tech.salroid.filmy.data.network

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import tech.salroid.filmy.BuildConfig.OMDB_API_KEY
import tech.salroid.filmy.data.datasource.DiscoverMoviesPagingSource
import tech.salroid.filmy.data.datasource.DiscoverTvPagingSource
import tech.salroid.filmy.data.datasource.MoviesPagingSource
import tech.salroid.filmy.data.datasource.PeoplePagingSource
import tech.salroid.filmy.data.datasource.TvShowsPagingSource
import tech.salroid.filmy.data.local.db.entity.Movie
import tech.salroid.filmy.data.local.db.entity.MovieDetails
import tech.salroid.filmy.data.local.model.*
import tech.salroid.filmy.data.local.model.discover.DiscoverFilters
import tech.salroid.filmy.data.local.model.discover.GenreResponse
import tech.salroid.filmy.data.local.model.tv.TvDetails
import tech.salroid.filmy.data.local.model.watch_providers.WatchProviderResponse
import tech.salroid.filmy.data.network.MoviesApiService.Companion.BASE_URL_OMDB

class MoviesApiHelperImpl(private val apiService: MoviesApiService) : MoviesApiHelper {

    override fun getMovies(type: String, isTrending: Boolean): Flow<PagingData<Movie>> = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            MoviesPagingSource(
                apiService = apiService,
                movieType = type,
                isTrending = isTrending
            )
        }
    ).flow

    override fun getMoviesFlow(
        type: String,
        isTrending: Boolean
    ): Flow<MoviesResponse> = flow {
        emit(apiService.getTrendingMovies("day", 1))
    }

    override fun getTvShows(type: String, isTrending: Boolean): Flow<PagingData<TvShow>> = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            TvShowsPagingSource(
                apiService = apiService,
                showType = type,
                isTrending = isTrending
            )
        }
    ).flow

    override fun getTvShowsFlow(
        type: String,
        isTrending: Boolean
    ): Flow<TvShowResponse> = flow {
        emit(apiService.getTrendingTvShows("day", 1))
    }

    override fun getMovieDetails(id: String): Flow<MovieDetails> = flow {
        emit(apiService.getMovieDetails(movieId = id))
    }

    override fun getTvShowDetails(id: String): Flow<TvDetails> = flow {
        emit(apiService.getTvShowDetails(id))
    }

    override fun getOMDBRatings(id: String): Flow<RatingResponse> = flow {
        emit(
            apiService.getOMDBRatings(BASE_URL_OMDB, id, OMDB_API_KEY, true, "json")
        )
    }

    override fun getCastAndCrew(id: String): Flow<CastAndCrewResponse> = flow {
        emit(apiService.getCasts(id))
    }

    override fun getCastAndCrewTv(id: String): Flow<CastAndCrewResponse> = flow {
        emit(apiService.getCastsTv(id))
    }

    override fun getSimilar(id: String): Flow<SimilarMoviesResponse> = flow {
        emit(apiService.getSimilarMovies(id))
    }

    override fun getRecommendation(id: String): Flow<SimilarMoviesResponse> = flow {
        emit(apiService.getRecommendation(id))
    }

    override fun getSimilarTv(id: String): Flow<SimilarMoviesResponse> = flow {
        emit(apiService.getSimilarTv(id))
    }

    override fun getRecommendationTv(id: String): Flow<SimilarMoviesResponse> = flow {
        emit(apiService.getRecommendationTv(id))
    }

    override fun getCastCrewDetails(id: String): Flow<CastCrewDetailsResponse> = flow {
        emit(apiService.getCastCrewDetails(id))
    }

    override fun getCastCrewMovies(id: String): Flow<CastCrewMoviesResponse> = flow {
        emit(apiService.getCastCrewMovies(id))
    }

    override fun getCastCrewTvShows(id: String): Flow<CastCrewMoviesResponse> = flow {
        emit(apiService.getCastCrewTvShows(id))
    }

    override fun searchMovies(query: String): Flow<SearchResultResponse> = flow {
        emit(apiService.searchMovies(query))
    }

    override fun searchMulti(query: String): Flow<SearchResultResponse> = flow {
        emit(apiService.searchMulti(query))
    }

    override fun getReviews(id: String): Flow<ReviewResponse> = flow {
        emit(apiService.getReviews(id))
    }

    override fun getTvReviews(id: String): Flow<ReviewResponse> = flow {
        emit(apiService.getTvReviews(id))
    }

    override fun getWatchProviders(id: String): Flow<WatchProviderResponse> = flow {
        emit(apiService.getWatchProviders(id))
    }

    override fun getWatchProvidersTv(id: String): Flow<WatchProviderResponse> = flow {
        emit(apiService.getWatchProvidersTv(id))
    }

    override fun discoverMovies(filters: DiscoverFilters): Flow<PagingData<Movie>> = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            DiscoverMoviesPagingSource(apiService = apiService, filters = filters)
        }
    ).flow

    override fun discoverTv(filters: DiscoverFilters): Flow<PagingData<TvShow>> = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            DiscoverTvPagingSource(apiService = apiService, filters = filters)
        }
    ).flow

    override fun getMovieGenres(): Flow<GenreResponse> = flow {
        emit(apiService.getMovieGenres())
    }

    override fun getTvGenres(): Flow<GenreResponse> = flow {
        emit(apiService.getTvGenres())
    }

    override fun getPeople(): Flow<PagingData<Person>> = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            PeoplePagingSource(apiService = apiService)
        }
    ).flow
}