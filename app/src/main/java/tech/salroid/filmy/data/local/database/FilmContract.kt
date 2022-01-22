/*
package tech.salroid.filmy.data.local.database

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.provider.BaseColumns
import android.provider.BaseColumns._ID

object FilmContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website. A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device
    const val CONTENT_AUTHORITY = "tech.salroid.filmy"

    // Use CONTENT_AUTHORITY to create the base of all URI' FilmyAuthenticator which apps will use to contact
    // the content provider.
    private val BASE_CONTENT_URI = Uri.parse("content://$CONTENT_AUTHORITY")
    const val TRENDING_PATH_MOVIE = "trending_movie"
    const val INTHEATERS_PATH_MOVIE = "intheaters_movie"
    const val UPCOMING_PATH_MOVIE = "upcoming_movie"
    const val PATH_CAST = "cast"
    const val PATH_SAVE = "save"

    object MoviesEntry : BaseColumns {

        const val TABLE_NAME = "trending"
        const val MOVIE_ID = "movie_id"
        const val MOVIE_YEAR = "movie_year"
        const val MOVIE_POSTER_LINK = "movie_poster"
        const val MOVIE_TITLE = "movie_title"
        const val MOVIE_BANNER = "movie_banner"
        const val MOVIE_DESCRIPTION = "movie_description"
        const val MOVIE_TAGLINE = "movie_tagline"
        const val MOVIE_TRAILER = "movie_trailer"
        const val MOVIE_RATING = "movie_rating"
        const val MOVIE_RUNTIME = "movie_runtime"
        const val MOVIE_RELEASED = "movie_release"
        const val MOVIE_CERTIFICATION = "movie_certification"
        const val MOVIE_LANGUAGE = "movie_language"

        @JvmField
        val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TRENDING_PATH_MOVIE).build()
        const val CONTENT_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TRENDING_PATH_MOVIE
        const val CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TRENDING_PATH_MOVIE

        fun buildMovieUri(id: Long): Uri {
            return ContentUris.withAppendedId(CONTENT_URI, id)
        }

        fun buildMovieByTag(movieTag: String?): Uri {
            return CONTENT_URI.buildUpon().appendPath(movieTag).build()
        }

        fun buildMovieUriWithMovieId(movieId: String?): Uri {
            return CONTENT_URI.buildUpon().appendQueryParameter(MOVIE_ID, movieId).build()
        }

        @JvmStatic
        fun buildMovieWithMovieId(movieId: String?): Uri {
            return CONTENT_URI.buildUpon().appendPath(movieId).build()
        }

        fun getMovieIdFromUri(uri: Uri): String {
            return uri.pathSegments[1]
        }

        fun getID() = _ID
    }

    object InTheatersMoviesEntry : BaseColumns {

        const val TABLE_NAME = "intheaters"
        val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(INTHEATERS_PATH_MOVIE).build()

        fun buildMovieUri(id: Long): Uri {
            return ContentUris.withAppendedId(CONTENT_URI, id)
        }

        fun buildMovieByTag(movieTag: String?): Uri {
            return CONTENT_URI.buildUpon().appendPath(movieTag).build()
        }

        fun buildMovieWithMovieId(movieId: String?): Uri {
            return CONTENT_URI.buildUpon().appendPath(movieId).build()
        }

        fun getMovieIdFromUri(uri: Uri): String {
            return uri.pathSegments[1]
        }

        fun getID() = _ID
    }

    object UpComingMoviesEntry : BaseColumns {

        const val TABLE_NAME = "upcoming"
        val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(UPCOMING_PATH_MOVIE).build()

        fun buildMovieUri(id: Long): Uri {
            return ContentUris.withAppendedId(CONTENT_URI, id)
        }

        fun buildMovieByTag(movieTag: String?): Uri {
            return CONTENT_URI.buildUpon().appendPath(movieTag).build()
        }

        fun buildMovieWithMovieId(movieId: String?): Uri {
            return CONTENT_URI.buildUpon().appendPath(movieId).build()
        }

        fun getMovieIdFromUri(uri: Uri): String {
            return uri.pathSegments[1]
        }

        fun getID() = _ID
    }

    object CastEntry : BaseColumns {

        const val TABLE_NAME = "cast"
        const val CAST_MOVIE_ID = "cast_movie_id"
        const val CAST_ID = "cast_id"
        const val CAST_ROLE = "cast_role"
        const val CAST_POSTER_LINK = "cast_poster"
        const val CAST_NAME = "cast_name"

        @JvmField
        val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CAST).build()
        const val CONTENT_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CAST
        const val CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CAST

        fun buildCastUri(id: Long): Uri {
            return ContentUris.withAppendedId(CONTENT_URI, id)
        }

        @JvmStatic
        fun buildCastUriByMovieId(movieId: String?): Uri {
            return CONTENT_URI.buildUpon().appendQueryParameter(CAST_MOVIE_ID, movieId).build()
        }

        fun getMovieIdFromUri(uri: Uri): String {
            return uri.pathSegments[1]
        }

        fun getID() = _ID
    }

    object SaveEntry : BaseColumns {

        const val TABLE_NAME = "save"
        const val SAVE_ID = "save_id"
        const val SAVE_YEAR = "save_year"
        const val SAVE_POSTER_LINK = "save_poster"
        const val SAVE_TITLE = "save_title"
        const val SAVE_BANNER = "save_banner"
        const val SAVE_DESCRIPTION = "save_description"
        const val SAVE_TAGLINE = "save_tagline"
        const val SAVE_TRAILER = "save_trailer"
        const val SAVE_RATING = "save_rating"
        const val SAVE_RUNTIME = "save_runtime"
        const val SAVE_RELEASED = "save_release"
        const val SAVE_CERTIFICATION = "save_certification"
        const val SAVE_LANGUAGE = "save_language"
        const val SAVE_FLAG = "save_flag"

        val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SAVE).build()
        const val CONTENT_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SAVE
        const val CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SAVE

        fun buildMovieUri(id: Long): Uri {
            return ContentUris.withAppendedId(CONTENT_URI, id)
        }

        fun buildMovieByTag(movieTag: String?): Uri {
            return CONTENT_URI.buildUpon().appendPath(movieTag).build()
        }

        fun buildMovieUriWithMovieId(movieId: String?): Uri {
            return CONTENT_URI.buildUpon().appendQueryParameter(SAVE_ID, movieId).build()
        }

        fun buildMovieWithMovieId(movieId: String?): Uri {
            return CONTENT_URI.buildUpon().appendPath(movieId).build()
        }

        fun getMovieIdFromUri(uri: Uri): String {
            return uri.pathSegments[1]
        }

        fun getID() = _ID
    }
}*/
