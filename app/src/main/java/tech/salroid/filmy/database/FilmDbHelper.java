package tech.salroid.filmy.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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


public class FilmDbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "filmy.db";
    private static final int DB_VERSION = 790;


    public FilmDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_TRENDING_MOVIE_TABLE = "CREATE TABLE " + FilmContract.MoviesEntry.TABLE_NAME
                + "(" + FilmContract.MoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
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
                + FilmContract.MoviesEntry.MOVIE_RATING + " VARCHAR (255));";


        final String SQL_CREATE_CAST_TABLE = "CREATE TABLE " + FilmContract.CastEntry.TABLE_NAME
                + "(" + FilmContract.CastEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + FilmContract.CastEntry.CAST_ID + " VARCHAR(255) NOT NULL ,"
                + FilmContract.CastEntry.CAST_MOVIE_ID + " VARCHAR(255) NOT NULL,"
                + FilmContract.CastEntry.CAST_NAME + " VARCHAR(255) NOT NULL,"
                + FilmContract.CastEntry.CAST_POSTER_LINK + " VARCHAR(255) ,"
                + FilmContract.CastEntry.CAST_ROLE + " VARCHAR(255));";


        final String SQL_CREATE_SAVE_TABLE = "CREATE TABLE " + FilmContract.SaveEntry.TABLE_NAME
                + "(" + FilmContract.SaveEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
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
                + FilmContract.SaveEntry.SAVE_FLAG + " int(2));";


        final String SQL_CREATE_INTHEATERS_MOVIE_TABLE = "CREATE TABLE " + FilmContract.InTheatersMoviesEntry.TABLE_NAME
                + "(" + FilmContract.MoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
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
                + FilmContract.MoviesEntry.MOVIE_RATING + " VARCHAR (255));";


        final String SQL_CREATE_UPCOMING_MOVIE_TABLE = "CREATE TABLE " + FilmContract.UpComingMoviesEntry.TABLE_NAME
                + "(" + FilmContract.MoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
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
                + FilmContract.MoviesEntry.MOVIE_RATING + " VARCHAR (255));";


        sqLiteDatabase.execSQL(SQL_CREATE_TRENDING_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_INTHEATERS_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_UPCOMING_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CAST_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_SAVE_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int old_version, int new_version) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FilmContract.MoviesEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FilmContract.CastEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FilmContract.SaveEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FilmContract.InTheatersMoviesEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FilmContract.UpComingMoviesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }

}
