package tech.salroid.filmy

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.ContentObserver
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Handler
import android.os.HandlerThread
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import tech.salroid.filmy.data.local.db.FilmContract
import tech.salroid.filmy.data.local.db.FilmDbHelper
import tech.salroid.filmy.utility.PollingCheck

class TestUtilities {

    private val mContext = ApplicationProvider.getApplicationContext<Context>()

    @Before
    fun setUp() {
        deleteAllRecords()
    }

    private fun deleteAllRecords() {
        //deleteAllRecordsFromProvider()
    }

    @Test
    fun testInsertReadProvider() {
        val testValues: ContentValues = createMovieTestValues()

        // Register a content observer for our insert.  This time, directly with the content resolver
        var tco = testContentObserver
        mContext.contentResolver
            .registerContentObserver(FilmContract.MoviesEntry.CONTENT_URI, true, tco)
        val movieUri: Uri? =
            mContext.contentResolver.insert(FilmContract.MoviesEntry.CONTENT_URI, testValues)

        // Did our content observer get called?  Students:  If this fails, your insert location
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail()
        mContext.contentResolver.unregisterContentObserver(tco)
        val movieRowId: Long? = movieUri?.let { ContentUris.parseId(it) }

        // Verify we got a row back.
        assertTrue(movieRowId != -1L)

        // Data 'FilmyAuthenticator inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        val cursor: Cursor? = mContext.contentResolver.query(
            FilmContract.MoviesEntry.CONTENT_URI,
            null,  // leaving "columns" null just returns all the columns.
            null,  // cols for "where" clause
            null,  // values for "where" clause
            null // sort order
        )
        cursor?.let {
            validateCursor(
                "testInsertReadProvider. Error validating MovieEntry.",
                it, testValues
            )
        }
        val castValues: ContentValues = createCastValues()
        // The TestContentObserver is a one-shot class
        tco = testContentObserver
        mContext.contentResolver
            .registerContentObserver(FilmContract.CastEntry.CONTENT_URI, true, tco)
        val weatherInsertUri: Uri? = mContext.contentResolver
            .insert(FilmContract.CastEntry.CONTENT_URI, castValues)
        assertTrue(weatherInsertUri != null)

        // Did our content observer get called?  Students:  If this fails, your insert weather
        // in your ContentProvider isn't calling
        // getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail()
        mContext.contentResolver.unregisterContentObserver(tco)

        // A cursor is your primary interface to the query results.
        val castCursor: Cursor? = mContext.contentResolver.query(
            FilmContract.CastEntry.CONTENT_URI,  // Table to Query
            null,  // leaving "columns" null just returns all the columns.
            null,  // cols for "where" clause
            null,  // values for "where" clause
            null // columns to group by
        )
        if (castCursor != null) {
            validateCursor(
                "testInsertReadProvider. Error validating CastEntry insert.",
                castCursor, castValues
            )
        }
    }

    open class TestContentObserver private constructor(ht: HandlerThread) :
        ContentObserver(Handler(ht.looper)) {
        private val mHT: HandlerThread = ht
        var mContentChanged = false

        // On earlier versions of Android, this onChange method is called
        override fun onChange(selfChange: Boolean) {
            onChange(selfChange, null)
        }

        override fun onChange(selfChange: Boolean, uri: Uri?) {
            mContentChanged = true
        }

        fun waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It'FilmyAuthenticator useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            object : PollingCheck(5000) {
                override fun check(): Boolean {
                    return mContentChanged
                }
            }.run()
            mHT.quit()
        }

        companion object {
            val testContentObserver: TestContentObserver
                get() {
                    val ht = HandlerThread("ContentObserverThread")
                    ht.start()
                    return TestContentObserver(ht)
                }
        }

    }

    companion object {
        @JvmStatic
        fun validateCursor(error: String, valueCursor: Cursor, expectedValues: ContentValues) {
            assertTrue("Empty cursor returned. $error", valueCursor.moveToFirst())
            validateCurrentRecord(error, valueCursor, expectedValues)
            valueCursor.close()
        }

        @JvmStatic
        fun validateCurrentRecord(
            error: String,
            valueCursor: Cursor,
            expectedValues: ContentValues
        ) {
            val valueSet: Set<Map.Entry<String, Any>> = expectedValues.valueSet()
            for ((columnName, value) in valueSet) {
                val idx = valueCursor.getColumnIndex(columnName)
                assertFalse("Column '$columnName' not found. $error", idx == -1)
                val expectedValue = value.toString()
                assertEquals(
                    "Value '" + value.toString() +
                            "' did not match the expected value '" +
                            expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx)
                )
            }
        }

        @JvmStatic
        fun insertMovieValues(mContext: Context?): Long {

            // insert our test records into the database
            val dbHelper = FilmDbHelper(mContext)
            val db: SQLiteDatabase = dbHelper.getWritableDatabase()
            val testValues: ContentValues = createMovieTestValues()
            val movieRowId: Long
            movieRowId = db.insert(FilmContract.MoviesEntry.TABLE_NAME, null, testValues)

            // Verify we got a row back.
            assertTrue("Error: Failure to insert North Pole Location Values", movieRowId != -1L)
            return movieRowId
        }

        @JvmStatic
        fun createMovieTestValues(): ContentValues {
            val testMovieId = "t1234"
            val testMovieTitle = "XXX - RISING"
            val testMovieYear = 2013
            val testLink = "http://www.webianks.com/logo.png"
            val contentValues = ContentValues()
            contentValues.put(FilmContract.MoviesEntry.MOVIE_ID, testMovieId)
            contentValues.put(FilmContract.MoviesEntry.MOVIE_TITLE, testMovieTitle)
            contentValues.put(FilmContract.MoviesEntry.MOVIE_YEAR, testMovieYear)
            contentValues.put(FilmContract.MoviesEntry.MOVIE_POSTER_LINK, testLink)
            return contentValues
        }

        fun createMovieTestUpdateValues(): ContentValues {
            val testMovieRating = 6.6
            val testMovieDescription = "this is Vin diesel best Upcoming"
            val testMovieTagline = "The retun of xander cage"
            val testMovieBanner = "http://www.webianks.com/logo.png"
            val testMovieTrailer = "http://www.webianks.com/logo.png"
            val contentValues = ContentValues()
            contentValues.put(FilmContract.MoviesEntry.MOVIE_BANNER, testMovieBanner)
            contentValues.put(FilmContract.MoviesEntry.MOVIE_DESCRIPTION, testMovieDescription)
            contentValues.put(FilmContract.MoviesEntry.MOVIE_TAGLINE, testMovieTagline)
            contentValues.put(FilmContract.MoviesEntry.MOVIE_TRAILER, testMovieTrailer)
            contentValues.put(FilmContract.MoviesEntry.MOVIE_RATING, testMovieRating.toString())
            return contentValues
        }

        @JvmStatic
        fun createCastValues(): ContentValues {
            val contentValuesCast = ContentValues()
            contentValuesCast.put(FilmContract.CastEntry.CAST_ROLE, "Superman")
            contentValuesCast.put(FilmContract.CastEntry.CAST_MOVIE_ID, "t1234")
            contentValuesCast.put(FilmContract.CastEntry.CAST_NAME, "clarke Kent")
            contentValuesCast.put(FilmContract.CastEntry.CAST_ID, "tt89231")
            contentValuesCast.put(
                FilmContract.CastEntry.CAST_POSTER_LINK,
                "http://www.webianks.com/logo.png"
            )
            return contentValuesCast
        }

        @JvmStatic
        fun insertCastValues(mContext: Context?): Long {

            // insert our test records into the database
            val dbHelper = FilmDbHelper(mContext)
            val db: SQLiteDatabase = dbHelper.writableDatabase
            val testValues: ContentValues = createCastValues()
            val castRowId: Long = db.insert(FilmContract.CastEntry.TABLE_NAME, null, testValues)

            // Verify we got a row back.
            assertTrue("Error: Failure to insert North Pole Location Values", castRowId != -1L)
            return castRowId
        }

        @JvmStatic
        val testContentObserver: TestContentObserver
            get() = TestContentObserver.testContentObserver
    }
}