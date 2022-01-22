package tech.salroid.filmy

import android.content.ContentValues
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.runner.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import tech.salroid.filmy.data.local.database.FilmContract
import tech.salroid.filmy.data.local.database.FilmDbHelper
import java.util.*

@RunWith(AndroidJUnit4::class)
class DatabaseTest {

    private val mContext = ApplicationProvider.getApplicationContext<Context>()

    @Before
    fun setUp() {
        deleteTheDatabase()
    }

    private fun deleteTheDatabase() {
        mContext.deleteDatabase(FilmDbHelper.DB_NAME)
    }

    @Test
    fun testCreateDb() {

        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        val tableNameHashSet = HashSet<String>()
        tableNameHashSet.add(FilmContract.MoviesEntry.TABLE_NAME)
        tableNameHashSet.add(FilmContract.CastEntry.TABLE_NAME)
        mContext.deleteDatabase(FilmDbHelper.DB_NAME)
        val db = FilmDbHelper(
            this.mContext
        ).writableDatabase

        assertEquals(true, db.isOpen)

        // have we created the tables we want?
        var c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null)
        assertTrue(
            "Error: This means that the database has not been created correctly",
            c.moveToFirst()
        )

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0))
        } while (c.moveToNext())

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue(
            "Error: Your database was created without both the location entry and weather entry tables",
            tableNameHashSet.isEmpty()
        )

        // now, do our tables contain the correct columns?
        c = db.rawQuery(
            "PRAGMA table_info(" + FilmContract.MoviesEntry.TABLE_NAME + ")",
            null
        )
        assertTrue(
            "Error: This means that we were unable to query the database for table information.",
            c.moveToFirst()
        )

        // Build a HashSet of all of the column names we want to look for
        val locationColumnHashSet = HashSet<String>()
        locationColumnHashSet.add(FilmContract.MoviesEntry._ID)
        locationColumnHashSet.add(FilmContract.MoviesEntry.MOVIE_ID)
        locationColumnHashSet.add(FilmContract.MoviesEntry.MOVIE_TITLE)
        locationColumnHashSet.add(FilmContract.MoviesEntry.MOVIE_YEAR)
        locationColumnHashSet.add(FilmContract.MoviesEntry.MOVIE_POSTER_LINK)
        locationColumnHashSet.add(FilmContract.MoviesEntry.MOVIE_BANNER)
        locationColumnHashSet.add(FilmContract.MoviesEntry.MOVIE_DESCRIPTION)
        locationColumnHashSet.add(FilmContract.MoviesEntry.MOVIE_TRAILER)
        locationColumnHashSet.add(FilmContract.MoviesEntry.MOVIE_TAGLINE)
        locationColumnHashSet.add(FilmContract.MoviesEntry.MOVIE_RATING)
        val columnNameIndex = c.getColumnIndex("name")
        do {
            val columnName = c.getString(columnNameIndex)
            locationColumnHashSet.remove(columnName)
        } while (c.moveToNext())

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue(
            "Error: The database doesn't contain all of the required location entry columns",
            locationColumnHashSet.isEmpty()
        )
        c = db.rawQuery(
            "PRAGMA table_info(" + FilmContract.CastEntry.TABLE_NAME + ")",
            null
        )
        assertTrue(
            "Error: This means that we were unable to query the database for table information.",
            c.moveToFirst()
        )

        // Build a HashSet of all of the column names we want to look for
        val locationColumnHashSetCast = HashSet<String>()
        locationColumnHashSetCast.add(FilmContract.CastEntry._ID)
        locationColumnHashSetCast.add(FilmContract.CastEntry.CAST_ID)
        locationColumnHashSetCast.add(FilmContract.CastEntry.CAST_MOVIE_ID)
        locationColumnHashSetCast.add(FilmContract.CastEntry.CAST_ROLE)
        locationColumnHashSetCast.add(FilmContract.CastEntry.CAST_NAME)
        locationColumnHashSetCast.add(FilmContract.CastEntry.CAST_POSTER_LINK)
        val columnNameIndexCast = c.getColumnIndex("name")
        do {
            val columnName = c.getString(columnNameIndexCast)
            locationColumnHashSetCast.remove(columnName)
        } while (c.moveToNext())

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue(
            "Error: The database doesn't contain all of the required location entry columns",
            locationColumnHashSet.isEmpty()
        )
        db.close()
    }

    fun testMoviesTable() {

        // First step: Get reference to writable database
        val filmDbHelper = FilmDbHelper(mContext)
        val sqLiteDatabase = filmDbHelper.writableDatabase


        // Create ContentValues of what you want to insert
        // (you can use the createNorthPoleLocationValues if you wish)
        val contentValues = testValues()


        // Insert ContentValues into database and get a row ID back
        val id = sqLiteDatabase.insert(FilmContract.MoviesEntry.TABLE_NAME, null, contentValues)
        assertTrue("Insertion is unsuccessful", id != -1L)


        // Query the database and receive a Cursor back

        //String[] columns = {WeatherContract.LocationEntry.COLUMN_CITY_NAME};
        val cursor = sqLiteDatabase.query(
            FilmContract.MoviesEntry.TABLE_NAME,
            null, null, null, null, null, null
        )


        // Move the cursor to a valid database row
        assertTrue(cursor.moveToFirst())
        cursor.close()
        sqLiteDatabase.close()
    }

    fun testCastTable() {
        val filmDbHelper = FilmDbHelper(mContext)
        val sqLiteDatabase = filmDbHelper.writableDatabase
        val contentValuesCast = testValuesForCast()
        val id1 = sqLiteDatabase.insert(FilmContract.CastEntry.TABLE_NAME, null, contentValuesCast)
        assertTrue("Insertion is unsuccessful", id1 != -1L)


        // Query the database and receive a Cursor back

        //String[] columns = {WeatherContract.LocationEntry.COLUMN_CITY_NAME};
        val cursor = sqLiteDatabase.query(
            FilmContract.CastEntry.TABLE_NAME,
            null, null, null, null, null, null
        )
        assertTrue(cursor.moveToFirst())
        cursor.close()
        sqLiteDatabase.close()
    }

    private fun testValuesForCast(): ContentValues {
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

    private fun testValues(): ContentValues {
        val testMovieId = "t1234"
        val testMovieTitle = "XXX - RISING"
        val testMovieYear = 2013
        val testLink = "http://www.webianks.com/logo.png"
        val testMovieRating = 6.6
        val testMovieDescription = "This is Vin diesel best Upcoming"
        val testMovieTagline = "The return of xander cage"
        val testMovieBanner = "http://www.webianks.com/logo.png"
        val testMovieTrailer = "http://www.webianks.com/logo.png"

        val contentValues = ContentValues()
        contentValues.put(FilmContract.MoviesEntry.MOVIE_ID, testMovieId)
        contentValues.put(FilmContract.MoviesEntry.MOVIE_TITLE, testMovieTitle)
        contentValues.put(FilmContract.MoviesEntry.MOVIE_YEAR, testMovieYear)
        contentValues.put(FilmContract.MoviesEntry.MOVIE_POSTER_LINK, testLink)
        contentValues.put(FilmContract.MoviesEntry.MOVIE_BANNER, testMovieBanner)
        contentValues.put(FilmContract.MoviesEntry.MOVIE_DESCRIPTION, testMovieDescription)
        contentValues.put(FilmContract.MoviesEntry.MOVIE_TAGLINE, testMovieTagline)
        contentValues.put(FilmContract.MoviesEntry.MOVIE_TRAILER, testMovieTrailer)
        contentValues.put(FilmContract.MoviesEntry.MOVIE_RATING, testMovieRating.toString())
        return contentValues
    }

    companion object {
        val TAG: String = DatabaseTest::class.java.simpleName
    }
}