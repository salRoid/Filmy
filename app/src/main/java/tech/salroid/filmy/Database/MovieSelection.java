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


}
