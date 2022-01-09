package tech.salroid.filmy.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class FilmDbHelper(context: Context?) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {

        val trendingMovieTable = ("CREATE TABLE " + FilmContract.MoviesEntry.TABLE_NAME
                + "(" + FilmContract.MoviesEntry.getID() + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + FilmContract.MoviesEntry.MOVIE_ID + " VARCHAR(255) NOT NULL ,"
                + FilmContract.MoviesEntry.MOVIE_TITLE + " VARCHAR(255) NOT NULL,"
                + FilmContract.MoviesEntry.MOVIE_YEAR + " INTEGER(4) NOT NULL ,"
                + FilmContract.MoviesEntry.MOVIE_POSTER_LINK + " VARCHAR(255) ,"
                + FilmContract.MoviesEntry.MOVIE_BANNER + " VARCHAR (255) ,"
                + FilmContract.MoviesEntry.MOVIE_CERTIFICATION + " VARCHAR (255) ,"
                + FilmContract.MoviesEntry.MOVIE_LANGUAGE + " VARCHAR (255) ,"
                + FilmContract.MoviesEntry.MOVIE_RELEASED + " VARCHAR (255) ,"
                + FilmContract.MoviesEntry.MOVIE_RUNTIME + " VARCHAR (255) ,"
                + FilmContract.MoviesEntry.MOVIE_DESCRIPTION + " VARCHAR (255) ,"
                + FilmContract.MoviesEntry.MOVIE_TAGLINE + " VARCHAR(255) ,"
                + FilmContract.MoviesEntry.MOVIE_TRAILER + " VARCHAR(255) ,"
                + FilmContract.MoviesEntry.MOVIE_RATING + " VARCHAR (255));")

        val castTable = ("CREATE TABLE " + FilmContract.CastEntry.TABLE_NAME
                + "(" + FilmContract.CastEntry.getID() + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + FilmContract.CastEntry.CAST_ID + " VARCHAR(255) NOT NULL ,"
                + FilmContract.CastEntry.CAST_MOVIE_ID + " VARCHAR(255) NOT NULL,"
                + FilmContract.CastEntry.CAST_NAME + " VARCHAR(255) NOT NULL,"
                + FilmContract.CastEntry.CAST_POSTER_LINK + " VARCHAR(255) ,"
                + FilmContract.CastEntry.CAST_ROLE + " VARCHAR(255));")

        val saveTable = ("CREATE TABLE " + FilmContract.SaveEntry.TABLE_NAME
                + "(" + FilmContract.SaveEntry.getID() + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + FilmContract.SaveEntry.SAVE_ID + " VARCHAR(255) NOT NULL ,"
                + FilmContract.SaveEntry.SAVE_TITLE + " VARCHAR(255) NOT NULL,"
                + FilmContract.SaveEntry.SAVE_YEAR + " INTEGER(4),"
                + FilmContract.SaveEntry.SAVE_POSTER_LINK + " VARCHAR(255) ,"
                + FilmContract.SaveEntry.SAVE_BANNER + " VARCHAR (255) ,"
                + FilmContract.SaveEntry.SAVE_CERTIFICATION + " VARCHAR (255) ,"
                + FilmContract.SaveEntry.SAVE_LANGUAGE + " VARCHAR (255) ,"
                + FilmContract.SaveEntry.SAVE_RELEASED + " VARCHAR (255) ,"
                + FilmContract.SaveEntry.SAVE_RUNTIME + " VARCHAR (255) ,"
                + FilmContract.SaveEntry.SAVE_DESCRIPTION + " VARCHAR (255) ,"
                + FilmContract.SaveEntry.SAVE_TAGLINE + " VARCHAR(255) ,"
                + FilmContract.SaveEntry.SAVE_TRAILER + " VARCHAR(255) ,"
                + FilmContract.SaveEntry.SAVE_RATING + " VARCHAR(255) ,"
                + FilmContract.SaveEntry.SAVE_FLAG + " int(2));")

        val inTheatersTable =
            ("CREATE TABLE " + FilmContract.InTheatersMoviesEntry.TABLE_NAME
                    + "(" + FilmContract.MoviesEntry.getID() + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                    + FilmContract.MoviesEntry.MOVIE_ID + " VARCHAR(255) NOT NULL ,"
                    + FilmContract.MoviesEntry.MOVIE_TITLE + " VARCHAR(255) NOT NULL,"
                    + FilmContract.MoviesEntry.MOVIE_YEAR + " INTEGER(4) NOT NULL ,"
                    + FilmContract.MoviesEntry.MOVIE_POSTER_LINK + " VARCHAR(255) ,"
                    + FilmContract.MoviesEntry.MOVIE_BANNER + " VARCHAR (255) ,"
                    + FilmContract.MoviesEntry.MOVIE_CERTIFICATION + " VARCHAR (255) ,"
                    + FilmContract.MoviesEntry.MOVIE_LANGUAGE + " VARCHAR (255) ,"
                    + FilmContract.MoviesEntry.MOVIE_RELEASED + " VARCHAR (255) ,"
                    + FilmContract.MoviesEntry.MOVIE_RUNTIME + " VARCHAR (255) ,"
                    + FilmContract.MoviesEntry.MOVIE_DESCRIPTION + " VARCHAR (255) ,"
                    + FilmContract.MoviesEntry.MOVIE_TAGLINE + " VARCHAR(255) ,"
                    + FilmContract.MoviesEntry.MOVIE_TRAILER + " VARCHAR(255) ,"
                    + FilmContract.MoviesEntry.MOVIE_RATING + " VARCHAR (255));")

        val upCompingMovieTable =
            ("CREATE TABLE " + FilmContract.UpComingMoviesEntry.TABLE_NAME
                    + "(" + FilmContract.MoviesEntry.getID() + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                    + FilmContract.MoviesEntry.MOVIE_ID + " VARCHAR(255) NOT NULL ,"
                    + FilmContract.MoviesEntry.MOVIE_TITLE + " VARCHAR(255) NOT NULL,"
                    + FilmContract.MoviesEntry.MOVIE_YEAR + " INTEGER(4) NOT NULL ,"
                    + FilmContract.MoviesEntry.MOVIE_POSTER_LINK + " VARCHAR(255) ,"
                    + FilmContract.MoviesEntry.MOVIE_BANNER + " VARCHAR (255) ,"
                    + FilmContract.MoviesEntry.MOVIE_CERTIFICATION + " VARCHAR (255) ,"
                    + FilmContract.MoviesEntry.MOVIE_LANGUAGE + " VARCHAR (255) ,"
                    + FilmContract.MoviesEntry.MOVIE_RELEASED + " VARCHAR (255) ,"
                    + FilmContract.MoviesEntry.MOVIE_RUNTIME + " VARCHAR (255) ,"
                    + FilmContract.MoviesEntry.MOVIE_DESCRIPTION + " VARCHAR (255) ,"
                    + FilmContract.MoviesEntry.MOVIE_TAGLINE + " VARCHAR(255) ,"
                    + FilmContract.MoviesEntry.MOVIE_TRAILER + " VARCHAR(255) ,"
                    + FilmContract.MoviesEntry.MOVIE_RATING + " VARCHAR (255));")

        sqLiteDatabase.execSQL(trendingMovieTable)
        sqLiteDatabase.execSQL(inTheatersTable)
        sqLiteDatabase.execSQL(upCompingMovieTable)
        sqLiteDatabase.execSQL(castTable)
        sqLiteDatabase.execSQL(saveTable)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, old_version: Int, new_version: Int) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FilmContract.MoviesEntry.TABLE_NAME)
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FilmContract.CastEntry.TABLE_NAME)
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FilmContract.SaveEntry.TABLE_NAME)
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FilmContract.InTheatersMoviesEntry.TABLE_NAME)
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FilmContract.UpComingMoviesEntry.TABLE_NAME)

        onCreate(sqLiteDatabase)
    }

    companion object {
        const val DB_NAME = "filmy.db"
        private const val DB_VERSION = 792
    }
}