package tech.salroid.filmy.Database;

/**
 * Created by R Ankit on 04-08-2016.
 */

public class MovieSelection {

    public static final int TRENDING_MOVIE_LOADER = 1;
    public static final int INTHEATERS_MOVIE_LOADER = 2;
    public static final int UPCOMING_MOVIE_LOADER = 3;


    public static final String[] MOVIE_COLUMNS = {

            FilmContract.MoviesEntry.MOVIE_ID,
            FilmContract.MoviesEntry.MOVIE_TITLE,
            FilmContract.MoviesEntry.MOVIE_YEAR,
            FilmContract.MoviesEntry.MOVIE_POSTER_LINK

    };


    public static final String[] GET_MOVIE_COLUMNS = {

            FilmContract.MoviesEntry.MOVIE_TITLE,
            FilmContract.MoviesEntry.MOVIE_BANNER,
            FilmContract.MoviesEntry.MOVIE_DESCRIPTION,
            FilmContract.MoviesEntry.MOVIE_TAGLINE,
            FilmContract.MoviesEntry.MOVIE_TRAILER,
            FilmContract.MoviesEntry.MOVIE_RATING,
            FilmContract.MoviesEntry.MOVIE_LANGUAGE,
            FilmContract.MoviesEntry.MOVIE_RELEASED,
            FilmContract.MoviesEntry.MOVIE_CERTIFICATION,
            FilmContract.MoviesEntry.MOVIE_RUNTIME,
    };


    public static final String[] GET_SAVE_COLUMNS = {

            FilmContract.SaveEntry.SAVE_ID,
            FilmContract.SaveEntry.SAVE_TITLE,
            FilmContract.SaveEntry.SAVE_BANNER,
            FilmContract.SaveEntry.SAVE_DESCRIPTION,
            FilmContract.SaveEntry.SAVE_TAGLINE,
            FilmContract.SaveEntry.SAVE_TRAILER,
            FilmContract.SaveEntry.SAVE_RATING,
            FilmContract.SaveEntry.SAVE_LANGUAGE,
            FilmContract.SaveEntry.SAVE_RELEASED,
            FilmContract.SaveEntry._ID,
            FilmContract.SaveEntry.SAVE_YEAR,
            FilmContract.SaveEntry.SAVE_CERTIFICATION,
            FilmContract.SaveEntry.SAVE_RUNTIME,
            FilmContract.SaveEntry.SAVE_POSTER_LINK,
    };


}
