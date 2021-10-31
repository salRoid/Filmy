package tech.salroid.filmy.utility

import android.content.Context
import android.content.DialogInterface
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import tech.salroid.filmy.R
import tech.salroid.filmy.database.OfflineMovies

object Confirmation {

    fun confirmFav(
        context: Context, movieMap: MutableMap<String, String?>,
        movieId: String?, movieIdFinal: String?, flagFavorite: Int
    ) {
        MaterialAlertDialogBuilder(context, R.style.AppTheme_Base_Dialog)
            .setTitle("Favorite")
            .setMessage("Are you sure you want to add this to favorite section?")
            .setPositiveButton(android.R.string.yes) { _: DialogInterface?, _: Int ->
                val offlineMovies = OfflineMovies(
                    context
                )
                offlineMovies.saveMovie(movieMap, movieId, movieIdFinal, flagFavorite)
            }
            .setNegativeButton(android.R.string.no) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            .show()
    }

    fun confirmWatchlist(
        context: Context, movieMap: MutableMap<String, String?>,
        movieId: String?, movieIdFinal: String?,
        flagWatchlist: Int
    ) {
        MaterialAlertDialogBuilder(context, R.style.AppTheme_Base_Dialog)
            .setTitle("WatchList")
            .setIcon(null)
            .setMessage("Are you sure you want to add this to watchlist section?")
            .setPositiveButton(android.R.string.yes) { _: DialogInterface?, _: Int ->
                val offlineMovies = OfflineMovies(
                    context
                )
                offlineMovies.saveMovie(movieMap, movieId, movieIdFinal, flagWatchlist)
            }
            .setNegativeButton(android.R.string.no) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            .show()
    }
}