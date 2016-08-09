package tech.salroid.filmy.database;

import android.content.ContentValues;
import android.content.Context;
import java.util.HashMap;


public class MovieDetailsUpdation {

    public static void performMovieDetailsUpdation(Context context, int type, HashMap<String,String> movieMap,String movie_id){


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
