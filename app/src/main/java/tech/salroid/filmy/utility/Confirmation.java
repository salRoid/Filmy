package tech.salroid.filmy.utility;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.FrameLayout;

import tech.salroid.filmy.tmdb_account.MarkingFavorite;
import tech.salroid.filmy.tmdb_account.MarkingWatchList;

/**
 * Created by R Ankit on 20-04-2017.
 */

public class Confirmation {

    public static void confirmFav(final Context context, final String movie_id) {

        new AlertDialog.Builder(context)
                .setTitle("Favorite")
                .setMessage("Are you sure you want to add this to favorite section?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        MarkingFavorite markingFavorite = new MarkingFavorite();
                        markingFavorite.markThisAsFavorite(context, movie_id);

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();

    }

    public static void confirmWatchlist(final Context context, final String movie_id) {

        new AlertDialog.Builder(context)
                .setTitle("WatchList")
                .setIcon(null)
                .setMessage("Are you sure you want to add this to watchlist section?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        MarkingWatchList markingWatchList = new MarkingWatchList();
                        markingWatchList.addToWatchList(context, movie_id);

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
