/*
package tech.salroid.filmy.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.AppWidgetTarget
import tech.salroid.filmy.R
import tech.salroid.filmy.ui.details.MovieDetailsActivity
import tech.salroid.filmy.data.local.database.FilmContract

class FilmyWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        var movieId = " "
        var movieTitle = " "
        var moviePoster = " "

        for (element in appWidgetIds) {

            val appWidgetId = element
            val moviesForTheUri = FilmContract.MoviesEntry.CONTENT_URI
            val selection: String? = null
            val selectionArgs: Array<String>? = null

            val cursor = context.contentResolver.query(
                moviesForTheUri,
                MovieProjection.MOVIE_COLUMNS,
                selection,
                selectionArgs,
                null
            )

            if (cursor != null && cursor.count > 0) {
                cursor.moveToFirst()
                val idIndex = cursor.getColumnIndex(FilmContract.MoviesEntry.MOVIE_ID)
                val titleIndex = cursor.getColumnIndex(FilmContract.MoviesEntry.MOVIE_TITLE)
                val posterIndex = cursor.getColumnIndex(FilmContract.MoviesEntry.MOVIE_POSTER_LINK)
                val yearIndex = cursor.getColumnIndex(FilmContract.MoviesEntry.MOVIE_YEAR)
                movieId = cursor.getString(idIndex)
                movieTitle = cursor.getString(titleIndex)
                moviePoster = cursor.getString(posterIndex)
                //String imdb_id = cursor.getString(id_index);
                //int movie_year = cursor.getInt(year_index);
            }
            cursor?.close()

            val remoteViews = RemoteViews(context.packageName, R.layout.filmy_appwidget)
            remoteViews.setTextViewText(R.id.widget_movie_name, movieTitle)
            val appWidgetTarget =
                AppWidgetTarget(context, R.id.widget_movie_image, remoteViews, *appWidgetIds)

            Glide.with(context)
                .asBitmap()
                .load(moviePoster)
                .into(appWidgetTarget)

            val intent = Intent(context, MovieDetailsActivity::class.java)
            intent.putExtra("title", movieTitle)
            intent.putExtra("activity", true)
            intent.putExtra("type", -1)
            intent.putExtra("database_applicable", false)
            intent.putExtra("network_applicable", true)
            intent.putExtra("id", movieId)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            val pendingIntent =
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            remoteViews.setOnClickPendingIntent(R.id.activity_opener, pendingIntent)
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
        }
    }
}*/
