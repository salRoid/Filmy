package tech.salroid.filmy.database

object MovieProjection {

    const val TRENDING_MOVIE_LOADER = 1
    const val INTHEATERS_MOVIE_LOADER = 2
    const val UPCOMING_MOVIE_LOADER = 3

    val MOVIE_COLUMNS = arrayOf(
        FilmContract.MoviesEntry.MOVIE_ID,
        FilmContract.MoviesEntry.MOVIE_TITLE,
        FilmContract.MoviesEntry.MOVIE_YEAR,
        FilmContract.MoviesEntry.MOVIE_POSTER_LINK
    )

    val GET_MOVIE_COLUMNS = arrayOf(
        FilmContract.MoviesEntry.MOVIE_TITLE,
        FilmContract.MoviesEntry.MOVIE_BANNER,
        FilmContract.MoviesEntry.MOVIE_DESCRIPTION,
        FilmContract.MoviesEntry.MOVIE_TAGLINE,
        FilmContract.MoviesEntry.MOVIE_TRAILER,
        FilmContract.MoviesEntry.MOVIE_RATING,
        FilmContract.MoviesEntry.MOVIE_LANGUAGE,
        FilmContract.MoviesEntry.MOVIE_RELEASED,
        FilmContract.MoviesEntry.MOVIE_CERTIFICATION,
        FilmContract.MoviesEntry.MOVIE_RUNTIME
    )

    val GET_SAVE_COLUMNS = arrayOf(
        FilmContract.SaveEntry.SAVE_ID,
        FilmContract.SaveEntry.SAVE_TITLE,
        FilmContract.SaveEntry.SAVE_BANNER,
        FilmContract.SaveEntry.SAVE_DESCRIPTION,
        FilmContract.SaveEntry.SAVE_TAGLINE,
        FilmContract.SaveEntry.SAVE_TRAILER,
        FilmContract.SaveEntry.SAVE_RATING,
        FilmContract.SaveEntry.SAVE_LANGUAGE,
        FilmContract.SaveEntry.SAVE_RELEASED,
        FilmContract.SaveEntry.getID(),
        FilmContract.SaveEntry.SAVE_YEAR,
        FilmContract.SaveEntry.SAVE_CERTIFICATION,
        FilmContract.SaveEntry.SAVE_RUNTIME,
        FilmContract.SaveEntry.SAVE_POSTER_LINK
    )
}