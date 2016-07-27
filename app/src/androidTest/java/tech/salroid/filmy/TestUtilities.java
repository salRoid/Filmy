package tech.salroid.filmy;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;
import java.util.Map;
import java.util.Set;
import tech.salroid.filmy.Database.FilmContract;
import tech.salroid.filmy.Database.FilmDbHelper;
import tech.salroid.filmy.utils.PollingCheck;

/**
 * Created by Home on 7/24/2016.
 */
public class TestUtilities extends AndroidTestCase{

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    public static long insertMovieValues(Context mContext) {

        // insert our test records into the database
        FilmDbHelper dbHelper = new FilmDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createMovieTestValues();

        long movieRowId;
        movieRowId = db.insert(FilmContract.MoviesEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert North Pole Location Values", movieRowId != -1);

        return movieRowId;
    }

    public static ContentValues createMovieTestValues() {

            String testMovieId = "t1234";
            String testMovieTitle = "XXX - RISING";
            int testMovieYear = 2013;
            String testLink = "http://www.webianks.com/logo.png";

            ContentValues contentValues = new ContentValues();
            contentValues.put(FilmContract.MoviesEntry.MOVIE_ID, testMovieId);
            contentValues.put(FilmContract.MoviesEntry.MOVIE_TITLE, testMovieTitle);
            contentValues.put(FilmContract.MoviesEntry.MOVIE_YEAR, testMovieYear);
            contentValues.put(FilmContract.MoviesEntry.MOVIE_POSTER_LINK, testLink);

            return contentValues;
    }

    public static ContentValues createMovieTestUpdateValues() {

        double testMovieRating=6.6;
        String testMovieDescription="this is Vin diesel best Upcoming";
        String testMovieTagline="The retun of xander cage";
        String testMovieBanner="http://www.webianks.com/logo.png";
        String testMovieTrailer="http://www.webianks.com/logo.png";

        ContentValues contentValues = new ContentValues();
        contentValues.put(FilmContract.MoviesEntry.MOVIE_BANNER,testMovieBanner);
        contentValues.put(FilmContract.MoviesEntry.MOVIE_DESCRIPTION,testMovieDescription);
        contentValues.put(FilmContract.MoviesEntry.MOVIE_TAGLINE,testMovieTagline);
        contentValues.put(FilmContract.MoviesEntry.MOVIE_TRAILER,testMovieTrailer);
        contentValues.put(FilmContract.MoviesEntry.MOVIE_RATING,String.valueOf(testMovieRating));

        return contentValues;
    }



    public static ContentValues createCastValues() {


        ContentValues contentValuesCast = new ContentValues();
        contentValuesCast.put(FilmContract.CastEntry.CAST_ROLE, "Superman");
        contentValuesCast.put(FilmContract.CastEntry.CAST_MOVIE_ID, "t1234");
        contentValuesCast.put(FilmContract.CastEntry.CAST_NAME, "clarke Kent");
        contentValuesCast.put(FilmContract.CastEntry.CAST_ID, "tt89231");
        contentValuesCast.put(FilmContract.CastEntry.CAST_POSTER_LINK, "http://www.webianks.com/logo.png");

        return contentValuesCast;
    }

    public static long insertCastValues(Context mContext) {


        // insert our test records into the database
        FilmDbHelper dbHelper = new FilmDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createCastValues();

        long castRowId;
        castRowId = db.insert(FilmContract.CastEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert North Pole Location Values", castRowId != -1);

        return castRowId;
    }

    // Make sure we can still delete after adding/updating stuff
    //
    // Student: Uncomment this test after you have completed writing the insert functionality
    // in your provider.  It relies on insertions with testInsertReadProvider, so insert and
    // query functionality must also be complete before this test can be used.
    public void testInsertReadProvider() {
        ContentValues testValues = TestUtilities.createMovieTestValues();

        // Register a content observer for our insert.  This time, directly with the content resolver
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(FilmContract.MoviesEntry.CONTENT_URI, true, tco);
        Uri movieUri = mContext.getContentResolver().insert(FilmContract.MoviesEntry.CONTENT_URI, testValues);

        // Did our content observer get called?  Students:  If this fails, your insert location
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long movieRowId = ContentUris.parseId(movieUri);

        // Verify we got a row back.
        assertTrue(movieRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                FilmContract.MoviesEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating MovieEntry.",
                cursor, testValues);

        ContentValues castValues = TestUtilities.createCastValues();
        // The TestContentObserver is a one-shot class
        tco = TestUtilities.getTestContentObserver();

        mContext.getContentResolver().registerContentObserver(FilmContract.CastEntry.CONTENT_URI, true, tco);

        Uri weatherInsertUri = mContext.getContentResolver()
                .insert(FilmContract.CastEntry.CONTENT_URI, castValues);
        assertTrue(weatherInsertUri != null);

        // Did our content observer get called?  Students:  If this fails, your insert weather
        // in your ContentProvider isn't calling
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

    /*
            Students: The functions we provide inside of TestProvider use this utility class to test
            the ContentObserver callbacks using the PollingCheck class that we grabbed from the Android
            CTS tests.

            Note that this only tests that the onChange function is called; it does not test that the
            correct Uri is returned.
         */
    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }




}
