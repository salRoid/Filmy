package tech.salroid.filmy.database

import android.content.ContentValues
import android.content.Context

object MovieDetailsUpdate {

    fun performMovieDetailsUpdate(
        context: Context,
        type: Int,
        movieMap: MutableMap<String, String?>,
        movieId: String?
    ) {

        val contentValues = ContentValues()
        contentValues.put(FilmContract.MoviesEntry.MOVIE_BANNER, movieMap["banner"])
        contentValues.put(FilmContract.MoviesEntry.MOVIE_TAGLINE, movieMap["tagline"])
        contentValues.put(FilmContract.MoviesEntry.MOVIE_DESCRIPTION, movieMap["overview"])
        contentValues.put(FilmContract.MoviesEntry.MOVIE_TRAILER, movieMap["trailer_img"])
        contentValues.put(FilmContract.MoviesEntry.MOVIE_CERTIFICATION, movieMap["certification"])
        contentValues.put(FilmContract.MoviesEntry.MOVIE_LANGUAGE, movieMap["language"])
        contentValues.put(FilmContract.MoviesEntry.MOVIE_RUNTIME, movieMap["runtime"])
        contentValues.put(FilmContract.MoviesEntry.MOVIE_RELEASED, movieMap["released"])
        contentValues.put(FilmContract.MoviesEntry.MOVIE_RATING, movieMap["rating"])

        when (type) {
            0 -> {
                val selection = FilmContract.MoviesEntry.TABLE_NAME +
                        "." + FilmContract.MoviesEntry.MOVIE_ID + " = ? "
                val selectionArgs = arrayOf(movieId)

                val id = context.contentResolver.update(
                    FilmContract.MoviesEntry.buildMovieByTag(movieId),
                    contentValues,
                    selection,
                    selectionArgs
                ).toLong()

                /* if (id != -1L) {
                     Log.d(LOG_TAG, "Movie row updated with new values.");
                 }*/
            }
            1 -> {
                val selection2 = FilmContract.InTheatersMoviesEntry.TABLE_NAME +
                        "." + FilmContract.MoviesEntry.MOVIE_ID + " = ? "
                val selectionArgs2 = arrayOf(movieId)

                val id2 = context.contentResolver.update(
                    FilmContract.InTheatersMoviesEntry.buildMovieByTag(movieId),
                    contentValues,
                    selection2,
                    selectionArgs2
                ).toLong()

                /* if (id2 != -1L) {
                     Log.d(LOG_TAG, "Movie row updated with new values.");
                 }*/
            }
            2 -> {

                val selection3 = FilmContract.UpComingMoviesEntry.TABLE_NAME +
                        "." + FilmContract.MoviesEntry.MOVIE_ID + " = ? "
                val selectionArgs3 = arrayOf(movieId)
                val id3 = context.contentResolver.update(
                    FilmContract.UpComingMoviesEntry.buildMovieByTag(movieId),
                    contentValues,
                    selection3,
                    selectionArgs3
                ).toLong()

                /*if (id3 != -1L) {
                     Log.d(LOG_TAG, "Movie row updated with new values.");
                }*/
            }
        }
    }
}