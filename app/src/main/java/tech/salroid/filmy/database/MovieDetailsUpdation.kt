package tech.salroid.filmy.database;

import android.content.ContentValues;
import android.content.Context;

import java.util.HashMap;

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

public class MovieDetailsUpdation {

    public static void performMovieDetailsUpdation(Context context, int type, HashMap<String, String> movieMap, String movie_id) {


        ContentValues contentValues = new ContentValues();

        contentValues.put(FilmContract.MoviesEntry.MOVIE_BANNER, movieMap.get("banner"));
        contentValues.put(FilmContract.MoviesEntry.MOVIE_TAGLINE, movieMap.get("tagline"));
        contentValues.put(FilmContract.MoviesEntry.MOVIE_DESCRIPTION, movieMap.get("overview"));
        contentValues.put(FilmContract.MoviesEntry.MOVIE_TRAILER, movieMap.get("trailer_img"));
        contentValues.put(FilmContract.MoviesEntry.MOVIE_CERTIFICATION, movieMap.get("certification"));
        contentValues.put(FilmContract.MoviesEntry.MOVIE_LANGUAGE, movieMap.get("language"));
        contentValues.put(FilmContract.MoviesEntry.MOVIE_RUNTIME, movieMap.get("runtime"));
        contentValues.put(FilmContract.MoviesEntry.MOVIE_RELEASED, movieMap.get("released"));
        contentValues.put(FilmContract.MoviesEntry.MOVIE_RATING, movieMap.get("rating"));

        switch (type) {

            case 0:

                final String selection =
                        FilmContract.MoviesEntry.TABLE_NAME +
                                "." + FilmContract.MoviesEntry.MOVIE_ID + " = ? ";
                final String[] selectionArgs = {movie_id};

                long id = context.getContentResolver().update(FilmContract.MoviesEntry.buildMovieByTag(movie_id), contentValues, selection, selectionArgs);

                if (id != -1) {
                    //  Log.d(LOG_TAG, "Movie row updated with new values.");
                }

                break;

            case 1:

                final String selection2 =
                        FilmContract.InTheatersMoviesEntry.TABLE_NAME +
                                "." + FilmContract.MoviesEntry.MOVIE_ID + " = ? ";
                final String[] selectionArgs2 = {movie_id};

                long id2 = context.getContentResolver().update(FilmContract.InTheatersMoviesEntry.buildMovieByTag(movie_id), contentValues, selection2, selectionArgs2);

                if (id2 != -1) {
                    //  Log.d(LOG_TAG, "Movie row updated with new values.");
                }
                break;

            case 2:


                final String selection3 =
                        FilmContract.UpComingMoviesEntry.TABLE_NAME +
                                "." + FilmContract.MoviesEntry.MOVIE_ID + " = ? ";
                final String[] selectionArgs3 = {movie_id};

                long id3 = context.getContentResolver().update(FilmContract.UpComingMoviesEntry.buildMovieByTag(movie_id), contentValues, selection3, selectionArgs3);

                if (id3 != -1) {
                    //  Log.d(LOG_TAG, "Movie row updated with new values.");
                }

                break;

        }


    }
}
