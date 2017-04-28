package tech.salroid.filmy.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

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


public class FilmContract {


    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device
    public static final String CONTENT_AUTHORITY = "tech.salroid.filmy";


    // Use CONTENT_AUTHORITY to create the base of all URI'FilmyAuthenticator which apps will use to contact
    // the content provider.


    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final String TRENDING_PATH_MOVIE = "trending_movie";
    public static final String INTHEATERS_PATH_MOVIE = "intheaters_movie";
    public static final String UPCOMING_PATH_MOVIE = "upcoming_movie";
    public static final String PATH_CAST = "cast";
    public static final String PATH_SAVE = "save";


    public static final class MoviesEntry implements BaseColumns {


        public static final String TABLE_NAME = "trending";
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


        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TRENDING_PATH_MOVIE).build();


        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TRENDING_PATH_MOVIE;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TRENDING_PATH_MOVIE;


        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieByTag(String movieTag) {
            return CONTENT_URI.buildUpon().appendPath(movieTag).build();
        }

        public static Uri buildMovieUriWithMovieId(String movieId) {
            return CONTENT_URI.buildUpon().appendQueryParameter(MOVIE_ID, movieId).build();
        }

        public static Uri buildMovieWithMovieId(String movieId) {
            return CONTENT_URI.buildUpon().appendPath(movieId).build();
        }

        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }


    public static final class InTheatersMoviesEntry implements BaseColumns {

        public static final String TABLE_NAME = "intheaters";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(INTHEATERS_PATH_MOVIE).build();

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieByTag(String movieTag) {
            return CONTENT_URI.buildUpon().appendPath(movieTag).build();
        }

        public static Uri buildMovieWithMovieId(String movieId) {
            return CONTENT_URI.buildUpon().appendPath(movieId).build();
        }

        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }


    public static final class UpComingMoviesEntry implements BaseColumns {

        public static final String TABLE_NAME = "upcoming";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(UPCOMING_PATH_MOVIE).build();

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieByTag(String movieTag) {
            return CONTENT_URI.buildUpon().appendPath(movieTag).build();
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
            return CONTENT_URI.buildUpon().appendQueryParameter(CAST_MOVIE_ID, movieId).build();
        }

        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }


    public static final class SaveEntry implements BaseColumns {

        public static final String TABLE_NAME = "save";
        public static final String SAVE_ID = "save_id";
        public static final String SAVE_YEAR = "save_year";
        public static final String SAVE_POSTER_LINK = "save_poster";
        public static final String SAVE_TITLE = "save_title";
        public static final String SAVE_BANNER = "save_banner";
        public static final String SAVE_DESCRIPTION = "save_description";
        public static final String SAVE_TAGLINE = "save_tagline";
        public static final String SAVE_TRAILER = "save_trailer";
        public static final String SAVE_RATING = "save_rating";
        public static final String SAVE_RUNTIME = "save_runtime";
        public static final String SAVE_RELEASED = "save_release";
        public static final String SAVE_CERTIFICATION = "save_certification";
        public static final String SAVE_LANGUAGE = "save_language";
        public static final String SAVE_FLAG = "save_flag";


        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SAVE).build();


        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SAVE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SAVE;


        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieByTag(String movieTag) {
            return CONTENT_URI.buildUpon().appendPath(movieTag).build();
        }

        public static Uri buildMovieUriWithMovieId(String movieId) {
            return CONTENT_URI.buildUpon().appendQueryParameter(SAVE_ID, movieId).build();
        }

        public static Uri buildMovieWithMovieId(String movieId) {
            return CONTENT_URI.buildUpon().appendPath(movieId).build();
        }

        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }
}