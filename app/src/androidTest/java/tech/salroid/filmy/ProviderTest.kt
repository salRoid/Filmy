/*
package tech.salroid.filmy

import android.content.ComponentName
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.runner.AndroidJUnit4
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import tech.salroid.filmy.TestUtilities.Companion.createCastValues
import tech.salroid.filmy.TestUtilities.Companion.createMovieTestValues
import tech.salroid.filmy.TestUtilities.Companion.insertCastValues
import tech.salroid.filmy.TestUtilities.Companion.insertMovieValues
import tech.salroid.filmy.TestUtilities.Companion.testContentObserver
import tech.salroid.filmy.TestUtilities.Companion.validateCurrentRecord
import tech.salroid.filmy.TestUtilities.Companion.validateCursor
import tech.salroid.filmy.data.local.database.FilmContract
import tech.salroid.filmy.data.local.database.FilmDbHelper

@RunWith(AndroidJUnit4::class)
class ProviderTest {

    private val mContext = ApplicationProvider.getApplicationContext<Context>()

    @Before
    fun setUp() {
        deleteAllRecords()
    }

    private fun deleteAllRecords() {
        deleteAllRecordsFromProvider()
    }

    @Test
    private fun deleteAllRecordsFromProvider() {
        mContext.contentResolver.delete(
            FilmContract.MoviesEntry.CONTENT_URI,
            null,
            null
        )
        mContext.contentResolver.delete(
            FilmContract.CastEntry.CONTENT_URI,
            null,
            null
        )
        var cursor: Cursor? = mContext.contentResolver.query(
            FilmContract.MoviesEntry.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        assertEquals(
            "Error: Records not deleted from Weather table during delete",
            0,
            cursor?.count
        )
        cursor?.close()
        cursor = mContext.contentResolver.query(
            FilmContract.CastEntry.CONTENT_URI,
            null,
            null,
            null,
            null
        )

        assertEquals(
            "Error: Records not deleted from Location table during delete",
            0,
            cursor?.count
        )
        cursor?.close()
    }

    @Test
    fun testProviderRegistry() {
        val pm: PackageManager = mContext.packageManager
        val componentName = ComponentName(
            mContext.packageName,
            FilmProvider::class.java.name
        )
        try {
            val providerInfo = pm.getProviderInfo(componentName, 0)
            assertEquals(
                "Error: WeatherProvider registered with authority: " + providerInfo.authority +
                        " instead of authority: " + FilmContract.CONTENT_AUTHORITY,
                providerInfo.authority, FilmContract.CONTENT_AUTHORITY
            )
        } catch (e: PackageManager.NameNotFoundException) {
            assertTrue(
                "Error: WeatherProvider not registered at " + mContext.getPackageName(),
                false
            )
        }
    }

    @Test
    fun testGetType() {
        var type: String? =
            mContext.contentResolver.getType(FilmContract.MoviesEntry.CONTENT_URI)
        assertEquals(
            "Error: type mismatch",
            FilmContract.MoviesEntry.CONTENT_TYPE, type
        )
        val testMovieId = "t1234"
        type = mContext.contentResolver.getType(
            FilmContract.MoviesEntry.buildMovieWithMovieId(testMovieId)
        )
        assertEquals(
            "Error: type mismatch ",
            FilmContract.MoviesEntry.CONTENT_ITEM_TYPE, type
        )
        type = mContext.contentResolver.getType(
            FilmContract.CastEntry.buildCastUriByMovieId(testMovieId)
        )
        assertEquals(
            "Error: type mismatch",
            FilmContract.CastEntry.CONTENT_TYPE, type
        )
        type = mContext.contentResolver.getType(FilmContract.CastEntry.CONTENT_URI)
        assertEquals(
            "Error: type mismatch",
            FilmContract.CastEntry.CONTENT_TYPE, type
        )
    }

    fun testBasicMoviesQuery() {

        // insert our test records into the database
        val dbHelper = FilmDbHelper(mContext)
        val db = dbHelper.writableDatabase
        val movieValues: ContentValues = createMovieTestValues()
        val movieRowId = insertMovieValues(mContext)
        val castValues: ContentValues = createCastValues()
        val castRowId = db.insert(FilmContract.CastEntry.TABLE_NAME, null, castValues)
        assertTrue("Unable to Insert MovieEntry into the Database", movieRowId != -1L)
        db.close()

        // Test the basic content provider query
        val movieCursor: Cursor? = mContext.contentResolver.query(
            FilmContract.MoviesEntry.CONTENT_URI,
            null,
            null,
            null,
            null
        )

        // Make sure we get the correct cursor out of the database
        movieCursor?.let { validateCursor("testBasicMovieQuery", it, movieValues) }
    }

    fun testBasicCastQueries() {
        // insert our test records into the database
        val dbHelper = FilmDbHelper(mContext)
        val db = dbHelper.writableDatabase
        val testValues: ContentValues = createCastValues()
        val castRowId = insertCastValues(mContext)

        // Test the basic content provider query
        val castCursor: Cursor? = mContext.contentResolver.query(
            FilmContract.CastEntry.CONTENT_URI,
            null,
            null,
            null,
            null
        )

        // Make sure we get the correct cursor out of the database
        castCursor?.let { validateCursor("testBasicCastQueries, cast query", it, testValues) }

        // Has the NotificationUri been set correctly? --- we can only test this easily against API
        // level 19 or greater because getNotificationUri was added in API level 19.
        if (Build.VERSION.SDK_INT >= 19) {
            assertEquals(
                "Error: Cast Query did not properly set NotificationUri",
                castCursor?.notificationUri, FilmContract.CastEntry.CONTENT_URI
            )
        }
    }

    private fun testInsertReadProvider() {
        val movieValues: ContentValues = createMovieTestValues()

        // Register a content observer for our insert.  This time, directly with the content resolver
        var tco = testContentObserver
        mContext.contentResolver
            .registerContentObserver(FilmContract.MoviesEntry.CONTENT_URI, true, tco)
        val locationUri: Uri? =
            mContext.contentResolver.insert(FilmContract.MoviesEntry.CONTENT_URI, movieValues)

        // Did our content observer get called?  Students:  If this fails, your insert location
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail()
        mContext.contentResolver.unregisterContentObserver(tco)
        val movieRowId = locationUri?.let { ContentUris.parseId(it) }

        // Verify we got a row back.
        assertTrue(movieRowId != -1L)

        // Data'FilmyAuthenticator inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
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
                "testInsertReadProvider. Error validating LocationEntry.",
                it, movieValues
            )
        }
        val castValues: ContentValues = createCastValues()
        // The TestContentObserver is a one-shot class
        tco = testContentObserver
        mContext.contentResolver
            .registerContentObserver(FilmContract.CastEntry.CONTENT_URI, true, tco)
        val castInsertUri: Uri? = mContext.contentResolver
            .insert(FilmContract.CastEntry.CONTENT_URI, castValues)
        assertTrue(castInsertUri != null)

        // Did our content observer get called? If this means
        // ContentProvider isn't calling
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
        castCursor?.let {
            validateCursor(
                "testInsertReadProvider. Error validating CastEntry insert.",
                it, castValues
            )
        }
    }

    fun testDeleteRecords() {
        testInsertReadProvider()

        // Register a content observer for our movie delete.
        val locationObserver = testContentObserver
        mContext.getContentResolver()
            .registerContentObserver(FilmContract.MoviesEntry.CONTENT_URI, true, locationObserver)

        // Register a content observer for our cast delete.
        val weatherObserver = testContentObserver
        mContext.getContentResolver()
            .registerContentObserver(FilmContract.CastEntry.CONTENT_URI, true, weatherObserver)
        deleteAllRecordsFromProvider()

        // If either of these fail, you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in the ContentProvider
        // delete.  (only if the insertReadProvider is succeeding)
        locationObserver.waitForNotificationOrFail()
        weatherObserver.waitForNotificationOrFail()
        mContext.getContentResolver().unregisterContentObserver(locationObserver)
        mContext.getContentResolver().unregisterContentObserver(weatherObserver)
    }

    fun testBulkInsertInMovie() {
        */
/* // first, let'FilmyAuthenticator create a location value
        ContentValues testValues = TestUtilities.createMovieTestValues();
        Uri locationUri = mContext.getContentResolver().insert(LocationEntry.CONTENT_URI, testValues);
        long locationRowId = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);

        // Data'FilmyAuthenticator inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                LocationEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testBulkInsert. Error validating LocationEntry.",
                cursor, testValues);

        // Now we can bulkInsert some weather.  In fact, we only implement BulkInsert for weather
        // entries.  With ContentProviders, you really only have to implement the features you
        // use, after all.*//*

        val bulkInsertContentValues = createBulkInsertMovieValues()

        // Register a content observer for our bulk insert.
        val weatherObserver = testContentObserver
        mContext.contentResolver
            .registerContentObserver(FilmContract.MoviesEntry.CONTENT_URI, true, weatherObserver)
        val insertCount: Int = mContext.contentResolver
            .bulkInsert(FilmContract.MoviesEntry.CONTENT_URI, bulkInsertContentValues)

        // Students:  If this fails, it means that you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in your BulkInsert
        // ContentProvider method.
        weatherObserver.waitForNotificationOrFail()
        mContext.contentResolver.unregisterContentObserver(weatherObserver)
        assertEquals(insertCount, BULK_INSERT_RECORDS_TO_INSERT)

        // A cursor is your primary interface to the query results.
        val cursor: Cursor? = mContext.contentResolver.query(
            FilmContract.MoviesEntry.CONTENT_URI,
            null,  // leaving "columns" null just returns all the columns.
            null,  // cols for "where" clause
            null,  // values for "where" clause
            FilmContract.MoviesEntry.MOVIE_YEAR + " ASC" // sort order == by DATE ASCENDING
        )

        // we should have as many records in the database as we've inserted
        cursor?.let { assertEquals(it.count, BULK_INSERT_RECORDS_TO_INSERT) }

        // and let'FilmyAuthenticator make sure they match the ones we created
        cursor?.moveToFirst()
        var i = 0
        while (i < BULK_INSERT_RECORDS_TO_INSERT) {
            bulkInsertContentValues[i]?.let {
                cursor?.let { it1 ->
                    validateCurrentRecord(
                        "testBulkInsert.  Error validating WeatherEntry $i",
                        it1, it
                    )
                }
            }
            i++
            cursor?.moveToNext()
        }
        cursor?.close()
    }

    fun testUpdateMovie() {
        //TODO
    }

    companion object {
        val TAG = ProviderTest::class.java.simpleName
        private const val BULK_INSERT_RECORDS_TO_INSERT = 10
        fun createBulkInsertMovieValues(): Array<ContentValues?> {
            val returnContentValues = arrayOfNulls<ContentValues>(BULK_INSERT_RECORDS_TO_INSERT)
            for (i in 0 until BULK_INSERT_RECORDS_TO_INSERT) {
                val contentValues = ContentValues()
                contentValues.put(FilmContract.MoviesEntry.MOVIE_ID, "t1234$i")
                contentValues.put(FilmContract.MoviesEntry.MOVIE_TITLE, "Superman $i")
                contentValues.put(FilmContract.MoviesEntry.MOVIE_YEAR, 2000 + i)
                contentValues.put(
                    FilmContract.MoviesEntry.MOVIE_BANNER,
                    "http://www.webianks.com/logo$i.png"
                )
                contentValues.put(FilmContract.MoviesEntry.MOVIE_RATING, 7.8 + i)
                contentValues.put(FilmContract.MoviesEntry.MOVIE_DESCRIPTION, "this is superman$i")
                contentValues.put(FilmContract.MoviesEntry.MOVIE_TAGLINE, "this is jaan$i")
                contentValues.put(
                    FilmContract.MoviesEntry.MOVIE_TRAILER,
                    "http://www.webianks.com/logo$i.png"
                )
                returnContentValues[i] = contentValues
            }
            return returnContentValues
        }
    }
}*/
