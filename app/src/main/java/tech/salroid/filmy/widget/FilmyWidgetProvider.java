package tech.salroid.filmy.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.AppWidgetTarget;

import tech.salroid.filmy.R;
import tech.salroid.filmy.activities.MovieDetailsActivity;
import tech.salroid.filmy.database.FilmContract;
import tech.salroid.filmy.database.MovieProjection;

/*
 * Filmy Application for Android
 * Copyright (c) 2016 Ramankit Singh (http://github.com/webianks).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class FilmyWidgetProvider extends AppWidgetProvider {

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        final int N = appWidgetIds.length;

        String movie_id=" ";
        String movie_title= " ";
        String movie_poster=" ";

        for (int i=0; i<N; i++) {

            int appWidgetId = appWidgetIds[i];

            Uri moviesForTheUri = FilmContract.MoviesEntry.CONTENT_URI;
            String selection = null;
            String[] selectionArgs = null;
            Cursor cursor = context.getContentResolver().query(moviesForTheUri,MovieProjection.MOVIE_COLUMNS, selection, selectionArgs, null);

            if (cursor != null && cursor.getCount() > 0) {

                cursor.moveToFirst();

                int id_index = cursor.getColumnIndex(FilmContract.MoviesEntry.MOVIE_ID);
                int title_index = cursor.getColumnIndex(FilmContract.MoviesEntry.MOVIE_TITLE);
                int poster_index = cursor.getColumnIndex(FilmContract.MoviesEntry.MOVIE_POSTER_LINK);
                int year_index = cursor.getColumnIndex(FilmContract.MoviesEntry.MOVIE_YEAR);

                movie_id = cursor.getString(id_index);
                movie_title = cursor.getString(title_index);
                movie_poster = cursor.getString(poster_index);
                //String imdb_id = cursor.getString(id_index);
                //int movie_year = cursor.getInt(year_index);



            } else {

            }

            cursor.close();

            final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.filmy_appwidget);
            remoteViews.setTextViewText(R.id.widget_movie_name,movie_title);

            AppWidgetTarget appWidgetTarget = new AppWidgetTarget(context, R.id.widget_movie_image, remoteViews, appWidgetIds);

            Glide.with(context)
                    .asBitmap()
                    .load(movie_poster)
                    .into(appWidgetTarget);


            Intent intent = new Intent(context,MovieDetailsActivity.class);
            intent.putExtra("title", movie_title);
            intent.putExtra("activity", true);
            intent.putExtra("type", -1);
            intent.putExtra("database_applicable", false);
            intent.putExtra("network_applicable", true);
            intent.putExtra("id", movie_id);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.activity_opener, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);

        }
    }



}