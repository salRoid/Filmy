package tech.salroid.filmy.utility;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import java.util.HashMap;

import tech.salroid.filmy.database.OfflineMovies;

/**
 * Created by R Ankit on 20-04-2017.
 */

public class Confirmation {

    public static void confirmFav(final Context context, final HashMap<String, String> movieMap,
                                  final String movie_id, final String movie_id_final, final int flagFavorite) {
        new AlertDialog.Builder(context)
                .setTitle("Favorite")
                .setMessage("Are you sure you want to add this to favorite section?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        OfflineMovies offlineMovies = new OfflineMovies(context);
                        offlineMovies.saveMovie(movieMap, movie_id, movie_id_final, flagFavorite);

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();

    }

    public static void confirmWatchlist(final Context context, final HashMap<String, String> movieMap,
                                        final String movie_id, final String movie_id_final,
                                        final int flagWatchlist) {

        new AlertDialog.Builder(context)
                .setTitle("WatchList")
                .setIcon(null)
                .setMessage("Are you sure you want to add this to watchlist section?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        OfflineMovies offlineMovies = new OfflineMovies(context);
                        offlineMovies.saveMovie(movieMap, movie_id, movie_id_final, flagWatchlist);

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();

    }
}
