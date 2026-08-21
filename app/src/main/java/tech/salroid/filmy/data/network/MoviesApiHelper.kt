package tech.salroid.filmy.data.network

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import tech.salroid.filmy.data.local.db.entity.Movie
import tech.salroid.filmy.data.local.db.entity.MovieDetails
import tech.salroid.filmy.data.local.model.*
import tech.salroid.filmy.data.local.model.collection.CollectionDetailsResponse
import tech.salroid.filmy.data.local.model.discover.DiscoverFilters
import tech.salroid.filmy.data.local.model.discover.GenreResponse
import tech.salroid.filmy.data.local.model.tv.SeasonDetailsResponse
import tech.salroid.filmy.data.local.model.tv.TvDetails
import tech.salroid.filmy.data.local.model.watch_providers.WatchProviderResponse

interface MoviesApiHelper {
    fun getMovies(type: String, isTrending: Boolean): Flow<PagingData<Movie>>

    fun getMoviesFlow(type: String, isTrending: Boolean): Flow<MoviesResponse>

    fun getTvShows(type: String, isTrending: Boolean): Flow<PagingData<TvShow>>

    fun getTvShowsFlow(type: String, isTrending: Boolean): Flow<TvShowResponse>

    fun discoverMovies(filters: DiscoverFilters): Flow<PagingData<Movie>>
    fun discoverTv(filters: DiscoverFilters): Flow<PagingData<TvShow>>
    fun getMovieGenres(): Flow<GenreResponse>
    fun getTvGenres(): Flow<GenreResponse>

    fun getMovieDetails(id: String): Flow<MovieDetails>
    fun getTvShowDetails(id: String): Flow<TvDetails>
    fun getOMDBRatings(id: String): Flow<RatingResponse>
    fun getCastAndCrew(id: String): Flow<CastAndCrewResponse>
    fun getCastAndCrewTv(id: String): Flow<CastAndCrewResponse>
    fun getSimilar(id: String): Flow<SimilarMoviesResponse>
    fun getSimilarTv(id: String): Flow<SimilarMoviesResponse>
    fun getRecommendation(id: String): Flow<SimilarMoviesResponse>
    fun getRecommendationTv(id: String): Flow<SimilarMoviesResponse>
    fun getCastCrewDetails(id: String): Flow<CastCrewDetailsResponse>
    fun getCastCrewMovies(id: String): Flow<CastCrewMoviesResponse>
    fun getCastCrewTvShows(id: String): Flow<CastCrewMoviesResponse>
    fun getCombinedCredits(id: String): Flow<CombinedCreditsResponse>
    fun getPersonExternalIds(id: String): Flow<ExternalIdsResponse>
    fun searchMovies(query: String): Flow<SearchResultResponse>
    fun searchMulti(query: String): Flow<SearchResultResponse>
    fun getReviews(id: String): Flow<ReviewResponse>
    fun getTvReviews(id: String): Flow<ReviewResponse>
    fun getWatchProviders(id: String): Flow<WatchProviderResponse>
    fun getWatchProvidersTv(id: String): Flow<WatchProviderResponse>
    fun getPeople(): Flow<PagingData<Person>>

    fun getMovieCertification(id: String): Flow<ReleaseDatesResponse>
    fun getTvCertification(id: String): Flow<ContentRatingsResponse>
    fun getCollectionDetails(id: Int): Flow<CollectionDetailsResponse>
    fun getSeasonDetails(tvId: String, seasonNumber: Int): Flow<SeasonDetailsResponse>
    fun getTvExternalIds(tvId: String): Flow<ExternalIdsResponse>
    fun getMovieExternalIds(movieId: String): Flow<ExternalIdsResponse>
    fun getMovieImages(movieId: String): Flow<ImagesResponse>
    fun getTvImages(tvId: String): Flow<ImagesResponse>
    fun getMovieKeywords(movieId: String): Flow<List<Keyword>>
    fun getTvKeywords(tvId: String): Flow<List<Keyword>>
}