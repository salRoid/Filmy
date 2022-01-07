package tech.salroid.filmy.providers

import android.content.ContentProvider
import tech.salroid.filmy.database.FilmDbHelper
import tech.salroid.filmy.database.FilmContract
import android.content.ContentValues
import android.annotation.TargetApi
import android.content.UriMatcher
import android.database.Cursor
import android.database.SQLException
import android.net.Uri
import java.lang.UnsupportedOperationException

class FilmProvider : ContentProvider() {

    private var mOpenHelper: FilmDbHelper? = null

    override fun onCreate(): Boolean {
        mOpenHelper = FilmDbHelper(context)
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor {

        val retCursor: Cursor = when (sUriMatcher.match(uri)) {
            TRENDING_MOVIE_DETAILS_WITH_MOVIE_ID -> {
                getTrendingMovieDetailsByMovieId(uri, projection, sortOrder)
            }
            INTHEATERS_MOVIE_DETAILS_WITH_MOVIE_ID -> {
                getInTheatersMovieDetailsByMovieId(uri, projection, sortOrder)
            }
            UPCOMING_MOVIE_DETAILS_WITH_MOVIE_ID -> {
                getUpcomingMovieDetailsByMovieId(uri, projection, sortOrder)
            }
            CAST_WITH_MOVIE_ID -> {
                getCastByMovieID(uri, projection, sortOrder)
            }
            TRENDING_MOVIE -> {
                mOpenHelper!!.readableDatabase.query(
                    FilmContract.MoviesEntry.TABLE_NAME,
                    projection, selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
                )
            }
            INTHEATERS_MOVIE -> {
                mOpenHelper!!.readableDatabase.query(
                    FilmContract.InTheatersMoviesEntry.TABLE_NAME,
                    projection, selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
                )
            }
            UPCOMING_MOVIE -> {
                mOpenHelper!!.readableDatabase.query(
                    FilmContract.UpComingMoviesEntry.TABLE_NAME,
                    projection, selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
                )
            }
            CAST -> {
                mOpenHelper!!.readableDatabase.query(
                    FilmContract.CastEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
                )
            }
            SAVE -> {
                mOpenHelper!!.readableDatabase.query(
                    FilmContract.SaveEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
                )
            }
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }
        retCursor.setNotificationUri(context!!.contentResolver, uri)
        return retCursor
    }

    private fun getTrendingMovieDetailsByMovieId(
        uri: Uri,
        projection: Array<String>?,
        sortOrder: String?
    ): Cursor {
        val movieId = FilmContract.MoviesEntry.getMovieIdFromUri(uri)
        val selection = sTrendingMovieIdSelection
        val selectionArgs = arrayOf(movieId)
        return mOpenHelper!!.readableDatabase.query(
            FilmContract.MoviesEntry.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder
        )
    }

    private fun getInTheatersMovieDetailsByMovieId(
        uri: Uri,
        projection: Array<String>?,
        sortOrder: String?
    ): Cursor {
        val movieId = FilmContract.InTheatersMoviesEntry.getMovieIdFromUri(uri)
        val selection = sInTheatersMovieIdSelection
        val selectionArgs = arrayOf(movieId)
        return mOpenHelper!!.readableDatabase.query(
            FilmContract.InTheatersMoviesEntry.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder
        )
    }

    private fun getUpcomingMovieDetailsByMovieId(
        uri: Uri,
        projection: Array<String>?,
        sortOrder: String?
    ): Cursor {
        val movieId = FilmContract.UpComingMoviesEntry.getMovieIdFromUri(uri)
        val selection = sUpcomingMovieIdSelection
        val selectionArgs = arrayOf(movieId)
        return mOpenHelper!!.readableDatabase.query(
            FilmContract.UpComingMoviesEntry.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder
        )
    }

    private fun getCastByMovieID(uri: Uri, projection: Array<String>?, sortOrder: String?): Cursor {
        val movieId = FilmContract.CastEntry.getMovieIdFromUri(uri)
        val selection = sTrendingMovieIdSelection
        val selectionArgs = arrayOf(movieId)
        return mOpenHelper!!.readableDatabase.query(
            FilmContract.CastEntry.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder
        )
    }

    private fun getSaveByMovieID(uri: Uri, projection: Array<String>, sortOrder: String): Cursor {
        val movieId = FilmContract.SaveEntry.getMovieIdFromUri(uri)
        val selection = sTrendingMovieIdSelection
        val selectionArgs = arrayOf(movieId)
        return mOpenHelper!!.readableDatabase.query(
            FilmContract.SaveEntry.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder
        )
    }

    override fun getType(uri: Uri): String? {

        // Use the Uri Matcher to determine what kind of URI this is.
        return when (sUriMatcher.match(uri)) {
            TRENDING_MOVIE_DETAILS_WITH_MOVIE_ID -> FilmContract.MoviesEntry.CONTENT_ITEM_TYPE
            INTHEATERS_MOVIE_DETAILS_WITH_MOVIE_ID -> FilmContract.MoviesEntry.CONTENT_ITEM_TYPE
            UPCOMING_MOVIE_DETAILS_WITH_MOVIE_ID -> FilmContract.MoviesEntry.CONTENT_ITEM_TYPE
            CAST_WITH_MOVIE_ID -> FilmContract.CastEntry.CONTENT_TYPE
            TRENDING_MOVIE -> FilmContract.MoviesEntry.CONTENT_TYPE
            INTHEATERS_MOVIE -> FilmContract.MoviesEntry.CONTENT_TYPE
            UPCOMING_MOVIE -> FilmContract.MoviesEntry.CONTENT_TYPE
            CAST -> FilmContract.CastEntry.CONTENT_TYPE
            SAVE -> FilmContract.SaveEntry.CONTENT_TYPE
            SAVE_DETAILS_WITH_MOVIE_ID -> FilmContract.SaveEntry.CONTENT_ITEM_TYPE
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }
    }

    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? {
        val db = mOpenHelper!!.writableDatabase
        val returnUri: Uri = when (sUriMatcher.match(uri)) {
            TRENDING_MOVIE -> {
                val id = db.insert(FilmContract.MoviesEntry.TABLE_NAME, null, contentValues)
                if (id > 0) FilmContract.MoviesEntry.buildMovieUri(id) else throw SQLException(
                    "Failed to insert row into $uri"
                )
            }
            INTHEATERS_MOVIE -> {
                val id =
                    db.insert(FilmContract.InTheatersMoviesEntry.TABLE_NAME, null, contentValues)
                if (id > 0) FilmContract.InTheatersMoviesEntry.buildMovieUri(id) else throw SQLException(
                    "Failed to insert row into $uri"
                )
            }
            UPCOMING_MOVIE -> {
                val id =
                    db.insert(FilmContract.UpComingMoviesEntry.TABLE_NAME, null, contentValues)
                if (id > 0) FilmContract.UpComingMoviesEntry.buildMovieUri(id) else throw SQLException(
                    "Failed to insert row into $uri"
                )
            }
            CAST -> {
                val id = db.insert(FilmContract.CastEntry.TABLE_NAME, null, contentValues)
                if (id > 0) FilmContract.CastEntry.buildCastUri(id) else throw SQLException(
                    "Failed to insert row into $uri"
                )
            }
            SAVE -> {
                val id = db.insert(FilmContract.SaveEntry.TABLE_NAME, null, contentValues)
                if (id > 0) FilmContract.SaveEntry.buildMovieUri(id) else throw SQLException(
                    "Failed to insert row into $uri"
                )
            }
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }
        context!!.contentResolver.notifyChange(uri, null)
        return returnUri
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        var selection = selection
        val db = mOpenHelper!!.writableDatabase
        val match = sUriMatcher.match(uri)
        if (selection == null) selection = "1"
        val rowsDeleted: Int = when (match) {
            TRENDING_MOVIE -> db.delete(
                FilmContract.MoviesEntry.TABLE_NAME,
                selection,
                selectionArgs
            )
            INTHEATERS_MOVIE -> db.delete(
                FilmContract.InTheatersMoviesEntry.TABLE_NAME,
                selection,
                selectionArgs
            )
            UPCOMING_MOVIE -> db.delete(
                FilmContract.UpComingMoviesEntry.TABLE_NAME,
                selection,
                selectionArgs
            )
            CAST -> db.delete(FilmContract.CastEntry.TABLE_NAME, selection, selectionArgs)
            SAVE -> db.delete(FilmContract.SaveEntry.TABLE_NAME, selection, selectionArgs)
            else -> throw UnsupportedOperationException("Unknown uri = $uri")
        }
        if (rowsDeleted != 0) context!!.contentResolver.notifyChange(uri, null)
        return rowsDeleted
    }

    override fun update(
        uri: Uri,
        contentValues: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        val db = mOpenHelper!!.writableDatabase
        val rowsUpdated: Int = when (sUriMatcher.match(uri)) {
            TRENDING_MOVIE_DETAILS_WITH_MOVIE_ID -> db.update(
                FilmContract.MoviesEntry.TABLE_NAME,
                contentValues,
                selection,
                selectionArgs
            )
            INTHEATERS_MOVIE_DETAILS_WITH_MOVIE_ID -> db.update(
                FilmContract.InTheatersMoviesEntry.TABLE_NAME,
                contentValues,
                selection,
                selectionArgs
            )
            UPCOMING_MOVIE_DETAILS_WITH_MOVIE_ID -> db.update(
                FilmContract.UpComingMoviesEntry.TABLE_NAME,
                contentValues,
                selection,
                selectionArgs
            )
            SAVE_DETAILS_WITH_MOVIE_ID -> db.update(
                FilmContract.SaveEntry.TABLE_NAME,
                contentValues,
                selection,
                selectionArgs
            )
            else -> throw UnsupportedOperationException("Unknown uri = $uri")
        }
        if (rowsUpdated != 0) context!!.contentResolver.notifyChange(uri, null)
        return rowsUpdated
    }

    override fun bulkInsert(uri: Uri, values: Array<ContentValues>): Int {
        val db = mOpenHelper!!.writableDatabase
        return when (sUriMatcher.match(uri)) {
            TRENDING_MOVIE -> {
                db.beginTransaction()
                var returnCount = 0
                try {
                    for (value in values) {
                        val id = db.insert(FilmContract.MoviesEntry.TABLE_NAME, null, value)
                        if (id != -1L) {
                            returnCount++
                        }
                    }
                    db.setTransactionSuccessful()
                } finally {
                    db.endTransaction()
                }
                context!!.contentResolver.notifyChange(uri, null)
                returnCount
            }
            INTHEATERS_MOVIE -> {
                db.beginTransaction()
                var returnCount1 = 0
                try {
                    for (value in values) {
                        val id =
                            db.insert(FilmContract.InTheatersMoviesEntry.TABLE_NAME, null, value)
                        if (id != -1L) {
                            returnCount1++
                        }
                    }
                    db.setTransactionSuccessful()
                } finally {
                    db.endTransaction()
                }
                context!!.contentResolver.notifyChange(uri, null)
                returnCount1
            }
            UPCOMING_MOVIE -> {
                db.beginTransaction()
                var returnCount2 = 0
                try {
                    for (value in values) {
                        val _id =
                            db.insert(FilmContract.UpComingMoviesEntry.TABLE_NAME, null, value)
                        if (_id != -1L) {
                            returnCount2++
                        }
                    }
                    db.setTransactionSuccessful()
                } finally {
                    db.endTransaction()
                }
                context!!.contentResolver.notifyChange(uri, null)
                returnCount2
            }
            SAVE -> {
                db.beginTransaction()
                var saveReturnCount = 0
                try {
                    for (value in values) {
                        val id = db.insert(FilmContract.SaveEntry.TABLE_NAME, null, value)
                        if (id != -1L) {
                            saveReturnCount++
                        }
                    }
                    db.setTransactionSuccessful()
                } finally {
                    db.endTransaction()
                }
                context!!.contentResolver.notifyChange(uri, null)
                saveReturnCount
            }
            else -> super.bulkInsert(uri, values)
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @TargetApi(11)
    override fun shutdown() {
        mOpenHelper!!.close()
        super.shutdown()
    }

    companion object {
        const val TRENDING_MOVIE = 100
        const val INTHEATERS_MOVIE = 400
        const val UPCOMING_MOVIE = 500
        const val TRENDING_MOVIE_DETAILS_WITH_MOVIE_ID = 101
        const val INTHEATERS_MOVIE_DETAILS_WITH_MOVIE_ID = 401
        const val UPCOMING_MOVIE_DETAILS_WITH_MOVIE_ID = 501
        const val CAST_WITH_MOVIE_ID = 102
        const val CAST = 300
        const val SAVE = 200
        const val SAVE_DETAILS_WITH_MOVIE_ID = 201

        // The URI Matcher used by this content provider.
        private val sUriMatcher = buildUriMatcher()
        private const val sTrendingMovieIdSelection = FilmContract.MoviesEntry.TABLE_NAME +
                "." + FilmContract.MoviesEntry.MOVIE_ID + " = ? "
        private const val sInTheatersMovieIdSelection =
            FilmContract.InTheatersMoviesEntry.TABLE_NAME +
                    "." + FilmContract.MoviesEntry.MOVIE_ID + " = ? "
        private const val sUpcomingMovieIdSelection = FilmContract.UpComingMoviesEntry.TABLE_NAME +
                "." + FilmContract.MoviesEntry.MOVIE_ID + " = ? "
        private const val sMovieIdSelectionForCast = FilmContract.CastEntry.TABLE_NAME +
                "." + FilmContract.CastEntry.CAST_MOVIE_ID + " = ? "

        private fun buildUriMatcher(): UriMatcher {
            // 1) The code passed into the constructor represents the code to return for the root
            // URI.  It'FilmyAuthenticator common to use NO_MATCH as the code for this case. Add the constructor below.
            val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
            val authority = FilmContract.CONTENT_AUTHORITY

            // 2) Use the addURI function to match each of the types.  Use the constants from
            // WeatherContract to help define the types to the UriMatcher.
            uriMatcher.addURI(authority, FilmContract.TRENDING_PATH_MOVIE, TRENDING_MOVIE)
            uriMatcher.addURI(
                authority,
                FilmContract.TRENDING_PATH_MOVIE + "/*",
                TRENDING_MOVIE_DETAILS_WITH_MOVIE_ID
            )
            uriMatcher.addURI(authority, FilmContract.INTHEATERS_PATH_MOVIE, INTHEATERS_MOVIE)
            uriMatcher.addURI(
                authority,
                FilmContract.INTHEATERS_PATH_MOVIE + "/*",
                INTHEATERS_MOVIE_DETAILS_WITH_MOVIE_ID
            )
            uriMatcher.addURI(authority, FilmContract.UPCOMING_PATH_MOVIE, UPCOMING_MOVIE)
            uriMatcher.addURI(
                authority,
                FilmContract.UPCOMING_PATH_MOVIE + "/*",
                UPCOMING_MOVIE_DETAILS_WITH_MOVIE_ID
            )
            uriMatcher.addURI(authority, FilmContract.PATH_CAST + "/*", CAST_WITH_MOVIE_ID)
            uriMatcher.addURI(authority, FilmContract.PATH_CAST, CAST)
            uriMatcher.addURI(authority, FilmContract.PATH_SAVE, SAVE)
            uriMatcher.addURI(authority, FilmContract.PATH_SAVE + "/*", SAVE_DETAILS_WITH_MOVIE_ID)

            // 3) Return the new matcher!
            return uriMatcher
        }
    }
}