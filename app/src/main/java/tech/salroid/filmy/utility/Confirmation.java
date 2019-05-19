package tech.salroid.filmy.utility;

import android.content.Context;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.HashMap;

import tech.salroid.filmy.R;
import tech.salroid.filmy.database.OfflineMovies;

/**
 * Created by R Ankit on 20-04-2017.
 */

public class Confirmation {

    public static void confirmFav(final Context context, final HashMap<String, String> movieMap,
                                  final String movie_id, final String movie_id_final, final int flagFavorite) {
        new MaterialAlertDialogBuilder(context, R.style.AppTheme_Base_Dialog)
                .setTitle("Favorite")
                .setMessage("Are you sure you want to add this to favorite section?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    OfflineMovies offlineMovies = new OfflineMovies(context);
                    offlineMovies.saveMovie(movieMap, movie_id, movie_id_final, flagFavorite);
                })
                .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.cancel())
                .show();

    }

    public static void confirmWatchlist(final Context context, final HashMap<String, String> movieMap,
                                        final String movie_id, final String movie_id_final,
                                        final int flagWatchlist) {

        new MaterialAlertDialogBuilder(context, R.style.AppTheme_Base_Dialog)
                .setTitle("WatchList")
                .setIcon(null)
                .setMessage("Are you sure you want to add this to watchlist section?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    OfflineMovies offlineMovies = new OfflineMovies(context);
                    offlineMovies.saveMovie(movieMap, movie_id, movie_id_final, flagWatchlist);
                })
                .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.cancel())
                .show();

    }
}
