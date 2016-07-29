package tech.salroid.filmy.Database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Home on 7/24/2016.
 */
public class FilmContract {


    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device
    public static final String CONTENT_AUTHORITY = "tech.salroid.filmy";


    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.


    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final String PATH_MOVIE = "movie";
    public static final String PATH_CAST = "cast";


    public static final class MoviesEntry implements BaseColumns {


        public static final String TABLE_NAME = "movies";
        public static final String MOVIE_ID = "movie_id";
        public static final String MOVIE_YEAR = "movie_year";
        public static final String MOVIE_POSTER_LINK = "movie_poster";
        public static final String MOVIE_TITLE = "movie_title";
        public static final String MOVIE_BANNER = "movie_banner";
        public static final String MOVIE_DESCRIPTION = "movie_description";
        public static final String MOVIE_TAGLINE = "movie_tagline";
        public static final String MOVIE_TRAILER = "movie_trailer";
        public static final String MOVIE_RATING = "movie_rating";
        public static final String MOVIE_RUNTIME = "movie_runtime";
        public static final String MOVIE_RELEASED = "movie_release";
        public static final String MOVIE_CERTIFICATION = "movie_certification";
        public static final String MOVIE_LANGUAGE = "movie_language";






        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();


        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;


        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieByTag(String movieTag) {
            return CONTENT_URI.buildUpon().appendPath(movieTag).build();
        }

        public static Uri buildMovieUriWithMovieId(String movieId) {
            return CONTENT_URI.buildUpon().appendQueryParameter(MOVIE_ID,movieId).build();
        }
        public static Uri buildMovieWithMovieId(String movieId) {
            return CONTENT_URI.buildUpon().appendPath(movieId).build();
        }

        public static String getMovieIdFromUri(Uri uri) {
                return uri.getPathSegments().get(1);
        }
    }

    public static final class CastEntry implements BaseColumns {


        public static final String TABLE_NAME = "cast";
        public static final String CAST_MOVIE_ID = "cast_movie_id";
        public static final String CAST_ID = "cast_id";
        public static final String CAST_ROLE = "cast_role";
        public static final String CAST_POSTER_LINK = "cast_poster";
        public static final String CAST_NAME = "cast_name";


        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CAST).build();


        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CAST;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CAST;



        public static Uri buildCastUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


        public static Uri buildCastUriByMovieId(String movieId) {
            return CONTENT_URI.buildUpon().appendQueryParameter(CAST_MOVIE_ID,movieId).build();
        }

        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }

}
