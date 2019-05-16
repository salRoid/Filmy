package tech.salroid.filmy;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;

import tech.salroid.filmy.database.FilmContract;
import tech.salroid.filmy.database.FilmDbHelper;
import tech.salroid.filmy.providers.FilmProvider;

/**
 * Created by Home on 7/24/2016.
 */
    public class ProviderTest extends AndroidTestCase {

    static final String TAG = ProviderTest.class.getSimpleName();
    static private final int BULK_INSERT_RECORDS_TO_INSERT = 10;

    static ContentValues[] createBulkInsertMovieValues() {

        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        for (int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++) {

            ContentValues contentValues = new ContentValues();

            contentValues.put(FilmContract.MoviesEntry.MOVIE_ID, "t1234" + i);
            contentValues.put(FilmContract.MoviesEntry.MOVIE_TITLE, "Superman " + i);
            contentValues.put(FilmContract.MoviesEntry.MOVIE_YEAR, 2000 + i);
            contentValues.put(FilmContract.MoviesEntry.MOVIE_BANNER, "http://www.webianks.com/logo" + i + ".png");
            contentValues.put(FilmContract.MoviesEntry.MOVIE_RATING, 7.8 + i);
            contentValues.put(FilmContract.MoviesEntry.MOVIE_DESCRIPTION, "this is superman" + i);
            contentValues.put(FilmContract.MoviesEntry.MOVIE_TAGLINE, "this is jaan" + i);
            contentValues.put(FilmContract.MoviesEntry.MOVIE_TRAILER, "http://www.webianks.com/logo" + i + ".png");

            returnContentValues[i] = contentValues;
        }
        return returnContentValues;
    }

    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }

    public void deleteAllRecordsFromProvider() {

        mContext.getContentResolver().delete(
                FilmContract.MoviesEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                FilmContract.CastEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                FilmContract.MoviesEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertEquals("Error: Records not deleted from Weather table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                FilmContract.CastEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Location table during delete", 0, cursor.getCount());
        cursor.close();
    }

    public void testProviderRegistry() {

        PackageManager pm = mContext.getPackageManager();

        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                FilmProvider.class.getName());
        try {

            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            assertEquals("Error: WeatherProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + FilmContract.CONTENT_AUTHORITY,
                    providerInfo.authority, FilmContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {

            assertTrue("Error: WeatherProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    public void testGetType() {

        String type = mContext.getContentResolver().getType(FilmContract.MoviesEntry.CONTENT_URI);

        assertEquals("Error: type mismatch",
                FilmContract.MoviesEntry.CONTENT_TYPE, type);

        String testMovieId = "t1234";

        type = mContext.getContentResolver().getType(
                FilmContract.MoviesEntry.buildMovieWithMovieId(testMovieId));

        assertEquals("Error: type mismatch ",
                FilmContract.MoviesEntry.CONTENT_ITEM_TYPE, type);

        type = mContext.getContentResolver().getType(
                FilmContract.CastEntry.buildCastUriByMovieId(testMovieId));

        assertEquals("Error: type mismatch",
                FilmContract.CastEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(FilmContract.CastEntry.CONTENT_URI);

        assertEquals("Error: type mismatch",
                FilmContract.CastEntry.CONTENT_TYPE, type);
    }

    public void testBasicMoviesQuery() {

        // insert our test records into the database
        FilmDbHelper dbHelper = new FilmDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues movieValues = TestUtilities.createMovieTestValues();
        long movieRowId = TestUtilities.insertMovieValues(mContext);

        ContentValues castValues = TestUtilities.createCastValues();

        long castRowId = db.insert(FilmContract.CastEntry.TABLE_NAME, null, castValues);
        assertTrue("Unable to Insert MovieEntry into the Database", movieRowId != -1);

        db.close();

        // Test the basic content provider query
        Cursor movieCursor = mContext.getContentResolver().query(
                FilmContract.MoviesEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicMovieQuery", movieCursor, movieValues);
    }

    public void testBasicCastQueries() {
        // insert our test records into the database
        FilmDbHelper dbHelper = new FilmDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createCastValues();
        long castRowId = TestUtilities.insertCastValues(mContext);

        // Test the basic content provider query
        Cursor castCursor = mContext.getContentResolver().query(
                FilmContract.CastEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicCastQueries, cast query", castCursor, testValues);

        // Has the NotificationUri been set correctly? --- we can only test this easily against API
        // level 19 or greater because getNotificationUri was added in API level 19.
        if (Build.VERSION.SDK_INT >= 19) {
            assertEquals("Error: Cast Query did not properly set NotificationUri",
                    castCursor.getNotificationUri(), FilmContract.CastEntry.CONTENT_URI);
        }
    }

    public void testInsertReadProvider() {

        ContentValues movieValues = TestUtilities.createMovieTestValues();

        // Register a content observer for our insert.  This time, directly with the content resolver
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(FilmContract.MoviesEntry.CONTENT_URI, true, tco);
        Uri locationUri = mContext.getContentResolver().insert(FilmContract.MoviesEntry.CONTENT_URI, movieValues);

        // Did our content observer get called?  Students:  If this fails, your insert location
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long movieRowId = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        assertTrue(movieRowId != -1);

        // Data'FilmyAuthenticator inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                FilmContract.MoviesEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating LocationEntry.",
                cursor, movieValues);


        ContentValues castValues = TestUtilities.createCastValues();
        // The TestContentObserver is a one-shot class
        tco = TestUtilities.getTestContentObserver();

        mContext.getContentResolver().registerContentObserver(FilmContract.CastEntry.CONTENT_URI, true, tco);

        Uri castInsertUri = mContext.getContentResolver()
                .insert(FilmContract.CastEntry.CONTENT_URI, castValues);
        assertTrue(castInsertUri != null);

        // Did our content observer get called? If this means
        // ContentProvider isn't calling
        // getContext().getContentResolver().notifyChange(uri, null);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        // A cursor is your primary interface to the query results.
        Cursor castCursor = mContext.getContentResolver().query(
                FilmContract.CastEntry.CONTENT_URI,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating CastEntry insert.",
                castCursor, castValues);

    }

    public void testDeleteRecords() {

        testInsertReadProvider();

        // Register a content observer for our movie delete.
        TestUtilities.TestContentObserver locationObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(FilmContract.MoviesEntry.CONTENT_URI, true, locationObserver);

        // Register a content observer for our cast delete.
        TestUtilities.TestContentObserver weatherObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(FilmContract.CastEntry.CONTENT_URI, true, weatherObserver);

        deleteAllRecordsFromProvider();

        // If either of these fail, you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in the ContentProvider
        // delete.  (only if the insertReadProvider is succeeding)
        locationObserver.waitForNotificationOrFail();
        weatherObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(locationObserver);
        mContext.getContentResolver().unregisterContentObserver(weatherObserver);
    }

    public void testBulkInsertInMovie() {
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
        // use, after all.*/
        ContentValues[] bulkInsertContentValues = createBulkInsertMovieValues();

        // Register a content observer for our bulk insert.
        TestUtilities.TestContentObserver weatherObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(FilmContract.MoviesEntry.CONTENT_URI, true, weatherObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(FilmContract.MoviesEntry.CONTENT_URI, bulkInsertContentValues);

        // Students:  If this fails, it means that you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in your BulkInsert
        // ContentProvider method.
        weatherObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(weatherObserver);

        assertEquals(insertCount, BULK_INSERT_RECORDS_TO_INSERT);

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                FilmContract.MoviesEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                FilmContract.MoviesEntry.MOVIE_YEAR + " ASC"  // sort order == by DATE ASCENDING
        );

        // we should have as many records in the database as we've inserted
        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

        // and let'FilmyAuthenticator make sure they match the ones we created
        cursor.moveToFirst();
        for (int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext()) {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating WeatherEntry " + i,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.close();
    }

    public void testUpdateMovie() {
        //TODO
    }


}
