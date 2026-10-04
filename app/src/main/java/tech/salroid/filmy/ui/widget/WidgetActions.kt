package tech.salroid.filmy.ui.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import tech.salroid.filmy.ui.home.AccountSyncRepository
import tech.salroid.filmy.ui.home.MoviesRepository

val movieIdKey = ActionParameters.Key<Int>("movie_id")
val movieTypeKey = ActionParameters.Key<Int>("movie_type")

/**
 * Marks a watchlist widget item as watched and drops it from the watchlist -
 * for this widget, "watched" means the item is done and should leave the
 * to-watch queue it's being shown in.
 */
class MarkWatchedAction : ActionCallback {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface MarkWatchedActionEntryPoint {
        fun moviesRepository(): MoviesRepository
        fun accountSyncRepository(): AccountSyncRepository
    }

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val id = parameters[movieIdKey] ?: return
        val type = parameters[movieTypeKey] ?: return

        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            MarkWatchedActionEntryPoint::class.java
        )
        val moviesRepository = entryPoint.moviesRepository()
        val accountSyncRepository = entryPoint.accountSyncRepository()

        val existing = moviesRepository.getMovieDetailsFromLocal(id, type) ?: return
        val updated = existing.copy(watched = true, watchlist = false)
        moviesRepository.addMovieDetailsToLocal(updated)

        val pushed = accountSyncRepository.pushItemState(updated)
        if (!pushed) {
            moviesRepository.addMovieDetailsToLocal(existing)
        }
    }
}
