package tech.salroid.filmy;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashSet;

import tech.salroid.filmy.database.FilmContract;
import tech.salroid.filmy.database.FilmDbHelper;

/**
 * Created by Home on 7/24/2016.
 */
public class DatabaseTest extends AndroidTestCase {

    static final String TAG = DatabaseTest.class.getSimpleName();

    public void setUp() {
        deleteTheDatabase();
    }

    void deleteTheDatabase() {
        mContext.deleteDatabase(FilmDbHelper.DB_NAME);
    }

    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(FilmContract.MoviesEntry.TABLE_NAME);
        tableNameHashSet.add(FilmContract.CastEntry.TABLE_NAME);

        mContext.deleteDatabase(FilmDbHelper.DB_NAME);

        SQLiteDatabase db = new FilmDbHelper(
                this.mContext).getWritableDatabase();

        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while (c.moveToNext());

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without both the location entry and weather entry tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + FilmContract.MoviesEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> locationColumnHashSet = new HashSet<String>();
        locationColumnHashSet.add(FilmContract.MoviesEntry._ID);
        locationColumnHashSet.add(FilmContract.MoviesEntry.MOVIE_ID);
        locationColumnHashSet.add(FilmContract.MoviesEntry.MOVIE_TITLE);
        locationColumnHashSet.add(FilmContract.MoviesEntry.MOVIE_YEAR);
        locationColumnHashSet.add(FilmContract.MoviesEntry.MOVIE_POSTER_LINK);
        locationColumnHashSet.add(FilmContract.MoviesEntry.MOVIE_BANNER);
        locationColumnHashSet.add(FilmContract.MoviesEntry.MOVIE_DESCRIPTION);
        locationColumnHashSet.add(FilmContract.MoviesEntry.MOVIE_TRAILER);
        locationColumnHashSet.add(FilmContract.MoviesEntry.MOVIE_TAGLINE);
        locationColumnHashSet.add(FilmContract.MoviesEntry.MOVIE_RATING);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            locationColumnHashSet.remove(columnName);
        } while (c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                locationColumnHashSet.isEmpty());

        c = db.rawQuery("PRAGMA table_info(" + FilmContract.CastEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> locationColumnHashSetCast = new HashSet<String>();
        locationColumnHashSetCast.add(FilmContract.CastEntry._ID);
        locationColumnHashSetCast.add(FilmContract.CastEntry.CAST_ID);
        locationColumnHashSetCast.add(FilmContract.CastEntry.CAST_MOVIE_ID);
        locationColumnHashSetCast.add(FilmContract.CastEntry.CAST_ROLE);
        locationColumnHashSetCast.add(FilmContract.CastEntry.CAST_NAME);
        locationColumnHashSetCast.add(FilmContract.CastEntry.CAST_POSTER_LINK);


        int columnNameIndexCast = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndexCast);
            locationColumnHashSetCast.remove(columnName);
        } while (c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                locationColumnHashSet.isEmpty());


        db.close();


    }


    public void testMoviesTable() {
        // First step: Get reference to writable database

        FilmDbHelper filmDbHelper = new FilmDbHelper(getContext());
        SQLiteDatabase sqLiteDatabase = filmDbHelper.getWritableDatabase();


        // Create ContentValues of what you want to insert
        // (you can use the createNorthPoleLocationValues if you wish)

        ContentValues contentValues = testValues();


        // Insert ContentValues into database and get a row ID back

        long id = sqLiteDatabase.insert(FilmContract.MoviesEntry.TABLE_NAME, null, contentValues);

        assertTrue("Insertion is unsuccessful", (id != -1));


        // Query the database and receive a Cursor back

        //String[] columns = {WeatherContract.LocationEntry.COLUMN_CITY_NAME};

        Cursor cursor = sqLiteDatabase.query(FilmContract.MoviesEntry.TABLE_NAME,
                null
                , null, null, null, null, null
        );


        // Move the cursor to a valid database row

        assertTrue(cursor.moveToFirst());


        cursor.close();
        sqLiteDatabase.close();

    }


    public void testCastTable() {

        FilmDbHelper filmDbHelper = new FilmDbHelper(getContext());
        SQLiteDatabase sqLiteDatabase = filmDbHelper.getWritableDatabase();


        ContentValues contentValuesCast = testValuesForCast();

        long id1 = sqLiteDatabase.insert(FilmContract.CastEntry.TABLE_NAME, null, contentValuesCast);

        assertTrue("Insertion is unsuccessful", (id1 != -1));


        // Query the database and receive a Cursor back

        //String[] columns = {WeatherContract.LocationEntry.COLUMN_CITY_NAME};

        Cursor cursor = sqLiteDatabase.query(FilmContract.CastEntry.TABLE_NAME,
                null
                , null, null, null, null, null
        );


        assertTrue(cursor.moveToFirst());

        cursor.close();
        sqLiteDatabase.close();

    }


    private ContentValues testValuesForCast() {


        ContentValues contentValuesCast = new ContentValues();
        contentValuesCast.put(FilmContract.CastEntry.CAST_ROLE, "Superman");
        contentValuesCast.put(FilmContract.CastEntry.CAST_MOVIE_ID, "t1234");
        contentValuesCast.put(FilmContract.CastEntry.CAST_NAME, "clarke Kent");
        contentValuesCast.put(FilmContract.CastEntry.CAST_ID, "tt89231");
        contentValuesCast.put(FilmContract.CastEntry.CAST_POSTER_LINK, "http://www.webianks.com/logo.png");

        return contentValuesCast;
    }


    private ContentValues testValues() {

        String testMovieId = "t1234";
        String testMovieTitle = "XXX - RISING";
        int testMovieYear = 2013;
        String testLink = "http://www.webianks.com/logo.png";
        double testMovieRating = 6.6;
        String testMovieDescription = "This is Vin diesel best Upcoming";
        String testMovieTagline = "The return of xander cage";
        String testMovieBanner = "http://www.webianks.com/logo.png";
        String testMovieTrailer = "http://www.webianks.com/logo.png";

        ContentValues contentValues = new ContentValues();
        contentValues.put(FilmContract.MoviesEntry.MOVIE_ID, testMovieId);
        contentValues.put(FilmContract.MoviesEntry.MOVIE_TITLE, testMovieTitle);
        contentValues.put(FilmContract.MoviesEntry.MOVIE_YEAR, testMovieYear);
        contentValues.put(FilmContract.MoviesEntry.MOVIE_POSTER_LINK, testLink);
        contentValues.put(FilmContract.MoviesEntry.MOVIE_BANNER, testMovieBanner);
        contentValues.put(FilmContract.MoviesEntry.MOVIE_DESCRIPTION, testMovieDescription);
        contentValues.put(FilmContract.MoviesEntry.MOVIE_TAGLINE, testMovieTagline);
        contentValues.put(FilmContract.MoviesEntry.MOVIE_TRAILER, testMovieTrailer);
        contentValues.put(FilmContract.MoviesEntry.MOVIE_RATING, String.valueOf(testMovieRating));

        return contentValues;
    }


}
