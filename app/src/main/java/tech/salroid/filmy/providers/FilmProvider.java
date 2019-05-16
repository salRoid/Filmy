package tech.salroid.filmy.providers;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import tech.salroid.filmy.database.FilmContract;
import tech.salroid.filmy.database.FilmDbHelper;

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

public class FilmProvider extends ContentProvider {

    static final int TRENDING_MOVIE = 100;
    static final int INTHEATERS_MOVIE = 400;
    static final int UPCOMING_MOVIE = 500;
    static final int TRENDING_MOVIE_DETAILS_WITH_MOVIE_ID = 101;
    static final int INTHEATERS_MOVIE_DETAILS_WITH_MOVIE_ID = 401;
    static final int UPCOMING_MOVIE_DETAILS_WITH_MOVIE_ID = 501;
    static final int CAST_WITH_MOVIE_ID = 102;
    static final int CAST = 300;
    static final int SAVE = 200;
    static final int SAVE_DETAILS_WITH_MOVIE_ID = 201;

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static final String sTrendingMovieIdSelection =
            FilmContract.MoviesEntry.TABLE_NAME +
                    "." + FilmContract.MoviesEntry.MOVIE_ID + " = ? ";
    private static final String sInTheatersMovieIdSelection =
            FilmContract.InTheatersMoviesEntry.TABLE_NAME +
                    "." + FilmContract.MoviesEntry.MOVIE_ID + " = ? ";
    private static final String sUpcomingMovieIdSelection =
            FilmContract.UpComingMoviesEntry.TABLE_NAME +
                    "." + FilmContract.MoviesEntry.MOVIE_ID + " = ? ";
    private static final String sMovieIdSelectionForCast =
            FilmContract.CastEntry.TABLE_NAME +
                    "." + FilmContract.CastEntry.CAST_MOVIE_ID + " = ? ";
    private FilmDbHelper mOpenHelper;

    static UriMatcher buildUriMatcher() {
        // 1) The code passed into the constructor represents the code to return for the root
        // URI.  It'FilmyAuthenticator common to use NO_MATCH as the code for this case. Add the constructor below.

        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FilmContract.CONTENT_AUTHORITY;

        // 2) Use the addURI function to match each of the types.  Use the constants from
        // WeatherContract to help define the types to the UriMatcher.

        uriMatcher.addURI(authority, FilmContract.TRENDING_PATH_MOVIE, TRENDING_MOVIE);
        uriMatcher.addURI(authority, FilmContract.TRENDING_PATH_MOVIE + "/*", TRENDING_MOVIE_DETAILS_WITH_MOVIE_ID);

        uriMatcher.addURI(authority, FilmContract.INTHEATERS_PATH_MOVIE, INTHEATERS_MOVIE);
        uriMatcher.addURI(authority, FilmContract.INTHEATERS_PATH_MOVIE + "/*", INTHEATERS_MOVIE_DETAILS_WITH_MOVIE_ID);

        uriMatcher.addURI(authority, FilmContract.UPCOMING_PATH_MOVIE, UPCOMING_MOVIE);
        uriMatcher.addURI(authority, FilmContract.UPCOMING_PATH_MOVIE + "/*", UPCOMING_MOVIE_DETAILS_WITH_MOVIE_ID);

        uriMatcher.addURI(authority, FilmContract.PATH_CAST + "/*", CAST_WITH_MOVIE_ID);
        uriMatcher.addURI(authority, FilmContract.PATH_CAST, CAST);
        uriMatcher.addURI(authority, FilmContract.PATH_SAVE, SAVE);
        uriMatcher.addURI(authority, FilmContract.PATH_SAVE + "/*", SAVE_DETAILS_WITH_MOVIE_ID);

        // 3) Return the new matcher!


        return uriMatcher;
    }

    @Override
    public boolean onCreate() {

        mOpenHelper = new FilmDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;

        switch (sUriMatcher.match(uri)) {

            case TRENDING_MOVIE_DETAILS_WITH_MOVIE_ID: {
                retCursor = getTrendingMovieDetailsByMovieId(uri, projection, sortOrder);
                break;
            }


            case INTHEATERS_MOVIE_DETAILS_WITH_MOVIE_ID: {
                retCursor = getInTheatersMovieDetailsByMovieId(uri, projection, sortOrder);
                break;
            }

            case UPCOMING_MOVIE_DETAILS_WITH_MOVIE_ID: {
                retCursor = getUpcomingMovieDetailsByMovieId(uri, projection, sortOrder);
                break;
            }


            case CAST_WITH_MOVIE_ID: {
                retCursor = getCastByMovieID(uri, projection, sortOrder);
                break;
            }

            case TRENDING_MOVIE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        FilmContract.MoviesEntry.TABLE_NAME,
                        projection, selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case INTHEATERS_MOVIE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        FilmContract.InTheatersMoviesEntry.TABLE_NAME,
                        projection, selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case UPCOMING_MOVIE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        FilmContract.UpComingMoviesEntry.TABLE_NAME,
                        projection, selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case CAST: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        FilmContract.CastEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case SAVE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        FilmContract.SaveEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    private Cursor getTrendingMovieDetailsByMovieId(Uri uri, String[] projection, String sortOrder) {

        String movieId = FilmContract.MoviesEntry.getMovieIdFromUri(uri);

        String selection = sTrendingMovieIdSelection;
        String[] selectionArgs = new String[]{movieId};

        return mOpenHelper.getReadableDatabase().query(
                FilmContract.MoviesEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

    }

    private Cursor getInTheatersMovieDetailsByMovieId(Uri uri, String[] projection, String sortOrder) {

        String movieId = FilmContract.InTheatersMoviesEntry.getMovieIdFromUri(uri);

        String selection = sInTheatersMovieIdSelection;
        String[] selectionArgs = new String[]{movieId};

        return mOpenHelper.getReadableDatabase().query(
                FilmContract.InTheatersMoviesEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

    }

    private Cursor getUpcomingMovieDetailsByMovieId(Uri uri, String[] projection, String sortOrder) {

        String movieId = FilmContract.UpComingMoviesEntry.getMovieIdFromUri(uri);

        String selection = sUpcomingMovieIdSelection;
        String[] selectionArgs = new String[]{movieId};

        return mOpenHelper.getReadableDatabase().query(
                FilmContract.UpComingMoviesEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

    }

    private Cursor getCastByMovieID(Uri uri, String[] projection, String sortOrder) {

        String movieId = FilmContract.CastEntry.getMovieIdFromUri(uri);

        String selection = sTrendingMovieIdSelection;
        String[] selectionArgs = new String[]{movieId};

        return mOpenHelper.getReadableDatabase().query(
                FilmContract.CastEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

    }

    private Cursor getSaveByMovieID(Uri uri, String[] projection, String sortOrder) {

        String movieId = FilmContract.SaveEntry.getMovieIdFromUri(uri);

        String selection = sTrendingMovieIdSelection;
        String[] selectionArgs = new String[]{movieId};

        return mOpenHelper.getReadableDatabase().query(
                FilmContract.SaveEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {

            case TRENDING_MOVIE_DETAILS_WITH_MOVIE_ID:
                return FilmContract.MoviesEntry.CONTENT_ITEM_TYPE;

            case INTHEATERS_MOVIE_DETAILS_WITH_MOVIE_ID:
                return FilmContract.MoviesEntry.CONTENT_ITEM_TYPE;

            case UPCOMING_MOVIE_DETAILS_WITH_MOVIE_ID:
                return FilmContract.MoviesEntry.CONTENT_ITEM_TYPE;


            case CAST_WITH_MOVIE_ID:
                return FilmContract.CastEntry.CONTENT_TYPE;

            case TRENDING_MOVIE:
                return FilmContract.MoviesEntry.CONTENT_TYPE;

            case INTHEATERS_MOVIE:
                return FilmContract.MoviesEntry.CONTENT_TYPE;

            case UPCOMING_MOVIE:
                return FilmContract.MoviesEntry.CONTENT_TYPE;

            case CAST:
                return FilmContract.CastEntry.CONTENT_TYPE;
            case SAVE:
                return FilmContract.SaveEntry.CONTENT_TYPE;
            case SAVE_DETAILS_WITH_MOVIE_ID:
                return FilmContract.SaveEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case TRENDING_MOVIE: {

                long _id = db.insert(FilmContract.MoviesEntry.TABLE_NAME, null, contentValues);
                if (_id > 0)
                    returnUri = FilmContract.MoviesEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;


            }

            case INTHEATERS_MOVIE: {

                long _id = db.insert(FilmContract.InTheatersMoviesEntry.TABLE_NAME, null, contentValues);
                if (_id > 0)
                    returnUri = FilmContract.InTheatersMoviesEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;


            }

            case UPCOMING_MOVIE: {

                long _id = db.insert(FilmContract.UpComingMoviesEntry.TABLE_NAME, null, contentValues);
                if (_id > 0)
                    returnUri = FilmContract.UpComingMoviesEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;


            }

            case CAST: {

                long _id = db.insert(FilmContract.CastEntry.TABLE_NAME, null, contentValues);
                if (_id > 0)
                    returnUri = FilmContract.CastEntry.buildCastUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            case SAVE: {

                long _id = db.insert(FilmContract.SaveEntry.TABLE_NAME, null, contentValues);
                if (_id > 0)
                    returnUri = FilmContract.SaveEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;


            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        if (selection == null) selection = "1";

        switch (match) {

            case TRENDING_MOVIE:

                rowsDeleted = db.delete(FilmContract.MoviesEntry.TABLE_NAME, selection, selectionArgs);

                break;

            case INTHEATERS_MOVIE:

                rowsDeleted = db.delete(FilmContract.InTheatersMoviesEntry.TABLE_NAME, selection, selectionArgs);

                break;

            case UPCOMING_MOVIE:

                rowsDeleted = db.delete(FilmContract.UpComingMoviesEntry.TABLE_NAME, selection, selectionArgs);

                break;

            case CAST:

                rowsDeleted = db.delete(FilmContract.CastEntry.TABLE_NAME, selection, selectionArgs);

                break;
            case SAVE:

                rowsDeleted = db.delete(FilmContract.SaveEntry.TABLE_NAME, selection, selectionArgs);

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri = " + uri);
        }

        if (rowsDeleted != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {

            case TRENDING_MOVIE_DETAILS_WITH_MOVIE_ID:
                rowsUpdated = db.update(FilmContract.MoviesEntry.TABLE_NAME, contentValues, selection, selectionArgs);

                break;

            case INTHEATERS_MOVIE_DETAILS_WITH_MOVIE_ID:
                rowsUpdated = db.update(FilmContract.InTheatersMoviesEntry.TABLE_NAME, contentValues, selection, selectionArgs);

                break;
            case UPCOMING_MOVIE_DETAILS_WITH_MOVIE_ID:
                rowsUpdated = db.update(FilmContract.UpComingMoviesEntry.TABLE_NAME, contentValues, selection, selectionArgs);

                break;

            case SAVE_DETAILS_WITH_MOVIE_ID:
                rowsUpdated = db.update(FilmContract.SaveEntry.TABLE_NAME, contentValues, selection, selectionArgs);

                break;
            default:
                throw new UnsupportedOperationException("Unknown uri = " + uri);
        }

        if (rowsUpdated != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TRENDING_MOVIE:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(FilmContract.MoviesEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;


            case INTHEATERS_MOVIE:
                db.beginTransaction();
                int returnCount1 = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(FilmContract.InTheatersMoviesEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount1++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount1;


            case UPCOMING_MOVIE:
                db.beginTransaction();
                int returnCount2 = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(FilmContract.UpComingMoviesEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount2++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount2;

            case SAVE:
                db.beginTransaction();
                int SaveReturnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(FilmContract.SaveEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            SaveReturnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return SaveReturnCount;

            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }


}