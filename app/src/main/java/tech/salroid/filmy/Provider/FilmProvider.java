package tech.salroid.filmy.Provider;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import tech.salroid.filmy.Database.FilmContract;
import tech.salroid.filmy.Database.FilmDbHelper;

/**
 * Created by Home on 7/24/2016.
 */
public class FilmProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private FilmDbHelper mOpenHelper;

    static final int MOVIE = 100;
    static final int MOVIE_DETAILS_WITH_MOVIE_ID = 101;
    static final int CAST_WITH_MOVIE_ID = 102;
    static final int CAST = 300;

    private static final String sMovieIdSelection =
            FilmContract.MoviesEntry.TABLE_NAME +
                    "." + FilmContract.MoviesEntry.MOVIE_ID + " = ? ";

    private static final String sMovieIdSelectionForCast =
            FilmContract.CastEntry.TABLE_NAME +
                    "." + FilmContract.CastEntry.CAST_MOVIE_ID + " = ? ";


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
            // "weather/*/*"
            case MOVIE_DETAILS_WITH_MOVIE_ID: {
                retCursor = getMovieDetailsByMovieId(uri, projection, sortOrder);
                break;
            }
            // "weather*//*"
            case CAST_WITH_MOVIE_ID: {
                retCursor = getCastByMovieID(uri, projection, sortOrder);
                break;
            }
            // "weather"
            case MOVIE: {
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
            // "location"
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

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }


    private Cursor getMovieDetailsByMovieId(Uri uri, String[] projection, String sortOrder) {

        String movieId = FilmContract.MoviesEntry.getMovieIdFromUri(uri);

        String selection = sMovieIdSelection;
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

    private Cursor getCastByMovieID(Uri uri, String[] projection, String sortOrder) {

        String movieId = FilmContract.CastEntry.getMovieIdFromUri(uri);

        String selection = sMovieIdSelection;
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


    @Nullable
    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {

            case MOVIE_DETAILS_WITH_MOVIE_ID:
                return FilmContract.MoviesEntry.CONTENT_ITEM_TYPE;
            case CAST_WITH_MOVIE_ID:
                return FilmContract.CastEntry.CONTENT_TYPE;
            case MOVIE:
                return FilmContract.MoviesEntry.CONTENT_TYPE;
            case CAST:
                return FilmContract.CastEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIE: {

                long _id = db.insert(FilmContract.MoviesEntry.TABLE_NAME, null, contentValues);
                if (_id > 0)
                    returnUri = FilmContract.MoviesEntry.buildMovieUri(_id);
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

            case MOVIE:

                rowsDeleted = db.delete(FilmContract.MoviesEntry.TABLE_NAME, selection, selectionArgs);

                break;

            case CAST:

                rowsDeleted = db.delete(FilmContract.CastEntry.TABLE_NAME, selection, selectionArgs);

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

            case MOVIE_DETAILS_WITH_MOVIE_ID:
                rowsUpdated = db.update(FilmContract.MoviesEntry.TABLE_NAME, contentValues, selection, selectionArgs);

                break;
            default:
                throw new UnsupportedOperationException("Unknown uri = " + uri);
        }

        if (rowsUpdated != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }


    static UriMatcher buildUriMatcher() {
        // 1) The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case. Add the constructor below.

        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FilmContract.CONTENT_AUTHORITY;

        // 2) Use the addURI function to match each of the types.  Use the constants from
        // WeatherContract to help define the types to the UriMatcher.

        uriMatcher.addURI(authority, FilmContract.PATH_MOVIE, MOVIE);
        uriMatcher.addURI(authority, FilmContract.PATH_MOVIE + "/*", MOVIE_DETAILS_WITH_MOVIE_ID);
        uriMatcher.addURI(authority, FilmContract.PATH_CAST + "/*", CAST_WITH_MOVIE_ID);
        uriMatcher.addURI(authority, FilmContract.PATH_CAST, CAST);

        // 3) Return the new matcher!


        return uriMatcher;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE:
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
