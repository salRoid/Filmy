package tech.salroid.filmy.parser

import org.json.JSONException
import org.json.JSONObject
import tech.salroid.filmy.data.PersonMovieDetailsData
import java.util.*

class CharacterDetailsActivityParseWork(private val moviesResponse: String) {

    fun parsePersonMovies(): List<PersonMovieDetailsData> {

        val allMovies: MutableList<PersonMovieDetailsData> = ArrayList()
        var movie: PersonMovieDetailsData?

        try {
            val jsonObject = JSONObject(moviesResponse)
            val jsonArray = jsonObject.getJSONArray("cast")

            for (i in 0 until jsonArray.length()) {
                val playedRole = jsonArray.getJSONObject(i).getString("character")
                val movieTitle = jsonArray.getJSONObject(i).getString("original_title")
                val movieId = jsonArray.getJSONObject(i).getString("id")
                val moviePoster = ("http://image.tmdb.org/t/p/w45" + jsonArray.getJSONObject(i)
                    .getString("poster_path"))

                movie = PersonMovieDetailsData(movieId)
                movie.movieTitle = movieTitle
                movie.rolePlayed = playedRole
                movie.moviePoster = moviePoster

                allMovies.add(movie)
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return allMovies
    }
}