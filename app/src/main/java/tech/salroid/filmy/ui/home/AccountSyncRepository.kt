package tech.salroid.filmy.ui.home

import kotlinx.coroutines.flow.first
import tech.salroid.filmy.data.local.db.entity.MovieDetails
import tech.salroid.filmy.data.local.model.MoviesResponse
import tech.salroid.filmy.data.local.model.TvShowResponse
import tech.salroid.filmy.ui.details.toMovieDetails
import javax.inject.Inject
import javax.inject.Singleton

/** (id, type) keys TMDB already has favorited/watchlisted/rated, after pulling them locally. */
private data class PulledKeys(
    val favoriteKeys: Set<Pair<Int, Int>>,
    val watchlistKeys: Set<Pair<Int, Int>>,
    val ratedKeys: Set<Pair<Int, Int>>
)

@Singleton
class AccountSyncRepository @Inject constructor(
    private val accountRepository: AccountRepository,
    private val moviesRepository: MoviesRepository
) {
    private var hasSyncedThisSession = false

    /**
     * Two-way merges local Watched/Watchlist with the user's real TMDB account:
     * pulls TMDB's favorite/watchlist lists into local storage, then pushes any
     * local watched/watchlist entries TMDB doesn't already have (e.g. items marked
     * before the user ever logged in). Runs at most once per app process (guarded
     * by [hasSyncedThisSession]) and is a no-op if the user isn't logged in.
     *
     * Note: this is a union merge, not full two-way sync — it never *removes*
     * anything from either side, so an item un-watchlisted locally while logged
     * out but still watchlisted on TMDB will reappear locally. Deletion tracking
     * would need real conflict resolution (e.g. last-modified timestamps), which
     * is out of scope here.
     */
    suspend fun syncIfNeeded() {
        if (hasSyncedThisSession) return
        if (!accountRepository.isLoggedIn()) return
        val sessionId = accountRepository.getSessionIdFromPref() ?: return
        val accountId = accountRepository.getProfileFromLocal()?.id ?: return

        hasSyncedThisSession = true
        try {
            val pulled = pullFavoritesAndWatchlist(accountId, sessionId)
            pushLocalOnlyEntries(accountId, sessionId, pulled)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** Pulls favorites/watchlist/ratings from TMDB, saves them locally, and returns what TMDB already has. */
    private suspend fun pullFavoritesAndWatchlist(
        accountId: Int,
        sessionId: String
    ): PulledKeys {
        data class MediaFlags(
            var favorite: Boolean = false,
            var watchlist: Boolean = false,
            var rating: Float? = null
        )

        val movieFlags = mutableMapOf<Int, MediaFlags>()
        val tvFlags = mutableMapOf<Int, MediaFlags>()

        suspend fun collectMoviePages(
            fetchPage: suspend (Int) -> MoviesResponse,
            mark: (MediaFlags) -> Unit
        ) {
            var page = 1
            var totalPages = 1
            while (page <= totalPages) {
                val response = fetchPage(page)
                totalPages = response.totalPages ?: 1
                response.results.forEach { movie ->
                    mark(movieFlags.getOrPut(movie.id) { MediaFlags() })
                }
                page++
            }
        }

        suspend fun collectTvPages(
            fetchPage: suspend (Int) -> TvShowResponse,
            mark: (MediaFlags) -> Unit
        ) {
            var page = 1
            var totalPages = 1
            while (page <= totalPages) {
                val response = fetchPage(page)
                totalPages = response.totalPages ?: 1
                response.results.forEach { show ->
                    mark(tvFlags.getOrPut(show.id) { MediaFlags() })
                }
                page++
            }
        }

        suspend fun collectRatedMoviePages() {
            var page = 1
            var totalPages = 1
            while (page <= totalPages) {
                val response = accountRepository.getRatedMovies(accountId, sessionId, page).first()
                totalPages = response.totalPages ?: 1
                response.results.forEach { item ->
                    movieFlags.getOrPut(item.id) { MediaFlags() }.rating = item.rating
                }
                page++
            }
        }

        suspend fun collectRatedTvPages() {
            var page = 1
            var totalPages = 1
            while (page <= totalPages) {
                val response = accountRepository.getRatedTv(accountId, sessionId, page).first()
                totalPages = response.totalPages ?: 1
                response.results.forEach { item ->
                    tvFlags.getOrPut(item.id) { MediaFlags() }.rating = item.rating
                }
                page++
            }
        }

        collectMoviePages({ accountRepository.getFavoriteMovies(accountId, sessionId, it).first() }) { it.favorite = true }
        collectMoviePages({ accountRepository.getWatchlistMovies(accountId, sessionId, it).first() }) { it.watchlist = true }
        collectTvPages({ accountRepository.getFavoriteTv(accountId, sessionId, it).first() }) { it.favorite = true }
        collectTvPages({ accountRepository.getWatchlistTv(accountId, sessionId, it).first() }) { it.watchlist = true }
        collectRatedMoviePages()
        collectRatedTvPages()

        // Fetch full details only for items whose flags actually differ from what's
        // already stored locally, then write everything in one batch — so the
        // Collections screen sees at most one clean update, not one per item.
        val pendingWrites = mutableListOf<MovieDetails>()

        movieFlags.forEach { (id, flags) ->
            val existing = moviesRepository.getMovieDetailsFromLocal(id, 0)
            if (existing != null &&
                existing.watched == flags.favorite &&
                existing.watchlist == flags.watchlist &&
                existing.userRating == flags.rating
            ) {
                return@forEach
            }
            try {
                val details = moviesRepository.getMovieDetailsFromNetwork(id.toString()).first()
                pendingWrites.add(
                    details.copy(
                        watched = flags.favorite,
                        watchlist = flags.watchlist,
                        userRating = flags.rating,
                        type = 0
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        tvFlags.forEach { (id, flags) ->
            val existing = moviesRepository.getMovieDetailsFromLocal(id, 1)
            if (existing != null &&
                existing.watched == flags.favorite &&
                existing.watchlist == flags.watchlist &&
                existing.userRating == flags.rating
            ) {
                return@forEach
            }
            try {
                val details = moviesRepository.getTvShowDetailsFromNetwork(id.toString()).first()
                pendingWrites.add(
                    details.toMovieDetails(existing = existing, watched = flags.favorite, watchlist = flags.watchlist)
                        .copy(userRating = flags.rating)
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        moviesRepository.saveMovieDetailsBatch(pendingWrites)

        val favoriteKeys = movieFlags.filterValues { it.favorite }.keys.map { it to 0 }.toSet() +
            tvFlags.filterValues { it.favorite }.keys.map { it to 1 }.toSet()
        val watchlistKeys = movieFlags.filterValues { it.watchlist }.keys.map { it to 0 }.toSet() +
            tvFlags.filterValues { it.watchlist }.keys.map { it to 1 }.toSet()
        val ratedKeys = movieFlags.filterValues { it.rating != null }.keys.map { it to 0 }.toSet() +
            tvFlags.filterValues { it.rating != null }.keys.map { it to 1 }.toSet()

        return PulledKeys(favoriteKeys, watchlistKeys, ratedKeys)
    }

    /** Pushes local watched/watchlist/rating rows TMDB doesn't already know about (e.g. set before login). */
    private suspend fun pushLocalOnlyEntries(
        accountId: Int,
        sessionId: String,
        pulled: PulledKeys
    ) {
        val localWatched = moviesRepository.getWatched().first()
        val localWatchlist = moviesRepository.getWatchlist().first()
        val localRated = moviesRepository.getRated().first()

        localWatched.forEach { movie ->
            if (movie.id to movie.type !in pulled.favoriteKeys) {
                try {
                    accountRepository.markFavorite(
                        accountId, sessionId, mediaType(movie.type), movie.id, favorite = true
                    ).first()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        localWatchlist.forEach { movie ->
            if (movie.id to movie.type !in pulled.watchlistKeys) {
                try {
                    accountRepository.markWatchlist(
                        accountId, sessionId, mediaType(movie.type), movie.id, watchlist = true
                    ).first()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        localRated.forEach { movie ->
            val rating = movie.userRating ?: return@forEach
            if (movie.id to movie.type !in pulled.ratedKeys) {
                try {
                    if (movie.type == 1) {
                        accountRepository.rateTv(movie.id, sessionId, rating).first()
                    } else {
                        accountRepository.rateMovie(movie.id, sessionId, rating).first()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * Pushes [movieDetails]'s current watched(->favorite)/watchlist state to TMDB.
     * No-ops (returns true) if the user isn't logged in — there's nothing to sync,
     * so callers shouldn't treat that as a failure worth rolling back for.
     * Returns false only when a logged-in push actually fails (network/API error),
     * so callers can roll back their optimistic local write.
     */
    suspend fun pushItemState(movieDetails: MovieDetails): Boolean {
        if (!accountRepository.isLoggedIn()) return true
        val sessionId = accountRepository.getSessionIdFromPref() ?: return true
        val accountId = accountRepository.getProfileFromLocal()?.id ?: return true

        return try {
            val type = mediaType(movieDetails.type)
            accountRepository.markFavorite(
                accountId, sessionId, type, movieDetails.id, movieDetails.watched
            ).first()
            accountRepository.markWatchlist(
                accountId, sessionId, type, movieDetails.id, movieDetails.watchlist
            ).first()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Pushes [movieDetails]'s current [MovieDetails.userRating] to TMDB — rates it if
     * non-null, deletes any existing TMDB rating if null. Same no-op-if-logged-out /
     * false-on-failure contract as [pushItemState], but kept separate since rating is
     * a distinct user action, not something to redundantly re-push on every
     * watched/watchlist toggle.
     */
    suspend fun pushRating(movieDetails: MovieDetails): Boolean {
        if (!accountRepository.isLoggedIn()) return true
        val sessionId = accountRepository.getSessionIdFromPref() ?: return true

        return try {
            val rating = movieDetails.userRating
            if (movieDetails.type == 1) {
                if (rating != null) {
                    accountRepository.rateTv(movieDetails.id, sessionId, rating).first()
                } else {
                    accountRepository.deleteTvRating(movieDetails.id, sessionId).first()
                }
            } else {
                if (rating != null) {
                    accountRepository.rateMovie(movieDetails.id, sessionId, rating).first()
                } else {
                    accountRepository.deleteMovieRating(movieDetails.id, sessionId).first()
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun mediaType(type: Int): String = if (type == 1) "tv" else "movie"
}
