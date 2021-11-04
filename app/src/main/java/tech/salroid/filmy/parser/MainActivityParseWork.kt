package tech.salroid.filmy.parser

import android.content.ContentValues
import android.content.Context
import org.json.JSONException
import org.json.JSONObject
import tech.salroid.filmy.database.FilmContract
import java.util.*

class MainActivityParseWork(private val context: Context, private val result: String) {

    fun parse() {
        try {
            val jsonObject = JSONObject(result)
            val jsonArray = jsonObject.getJSONArray("results")
            val cVVector = Vector<ContentValues>(jsonArray.length())

            for (i in 0 until jsonArray.length()) {
                val title: String = jsonArray.getJSONObject(i).getString("title")
                val poster: String = jsonArray.getJSONObject(i).getString("poster_path")
                val id: String? = jsonArray.getJSONObject(i).getString("id")

                val tempYear =
                    jsonArray.getJSONObject(i).getString("release_date").split("-").toTypedArray()
                val year = tempYear[0]

                val trimmedQuery = title.toLowerCase().trim { it <= ' ' }
                var finalQuery = trimmedQuery.replace(" ", "-")
                finalQuery = finalQuery.replace("'", "-")
                val slug = finalQuery.replace(":", "") + "-" + year

                // Insert the new weather information into the database
                val movieValues = ContentValues()
                if (poster != "null") {
                    movieValues.put(FilmContract.MoviesEntry.MOVIE_ID, id)
                    movieValues.put(FilmContract.MoviesEntry.MOVIE_TITLE, title)
                    movieValues.put(FilmContract.MoviesEntry.MOVIE_YEAR, year)
                    movieValues.put(
                        FilmContract.MoviesEntry.MOVIE_POSTER_LINK,
                        "http://image.tmdb.org/t/p/w185$poster"
                    )
                    cVVector.add(movieValues)
                }
            }

            // Add to database
            if (cVVector.size > 0) {
                val cvArray = arrayOfNulls<ContentValues>(cVVector.size)
                cVVector.toArray(cvArray)
                context.contentResolver.delete(FilmContract.MoviesEntry.CONTENT_URI, null, null)
                val inserted = context.contentResolver.bulkInsert(
                    FilmContract.MoviesEntry.CONTENT_URI,
                    cvArray
                )
            }

        } catch (e1: JSONException) {
            e1.printStackTrace()
        }
    }

    fun parseUpcoming() {
        try {
            val jsonObject = JSONObject(result)
            val jsonArray = jsonObject.getJSONArray("results")
            val cVVector = Vector<ContentValues>(jsonArray.length())

            for (i in 0 until jsonArray.length()) {
                val title: String = jsonArray.getJSONObject(i).getString("title")
                val poster: String = jsonArray.getJSONObject(i).getString("poster_path")
                val id: String? = jsonArray.getJSONObject(i).getString("id")
                val tempYear =
                    jsonArray.getJSONObject(i).getString("release_date").split("-").toTypedArray()
                val year = tempYear[0]

                val trimmedQuery = title.toLowerCase().trim { it <= ' ' }
                var finalQuery = trimmedQuery.replace(" ", "-")
                finalQuery = finalQuery.replace("'", "-")
                val slug = finalQuery.replace(":", "") + "-" + year

                // Insert the new weather information into the database
                val movieValues = ContentValues()

                if (poster != "null") {
                    movieValues.put(FilmContract.MoviesEntry.MOVIE_ID, id)
                    movieValues.put(FilmContract.MoviesEntry.MOVIE_TITLE, title)
                    movieValues.put(FilmContract.MoviesEntry.MOVIE_YEAR, year)
                    movieValues.put(
                        FilmContract.MoviesEntry.MOVIE_POSTER_LINK,
                        "http://image.tmdb.org/t/p/w185$poster"
                    )
                    cVVector.add(movieValues)
                }
            }

            var inserted = 0
            if (cVVector.size > 0) {
                val cvArray = arrayOfNulls<ContentValues>(cVVector.size)
                cVVector.toArray(cvArray)
                context.contentResolver.delete(
                    FilmContract.UpComingMoviesEntry.CONTENT_URI,
                    null,
                    null
                )
                inserted = context.contentResolver.bulkInsert(
                    FilmContract.UpComingMoviesEntry.CONTENT_URI,
                    cvArray
                )
            }
        } catch (e1: JSONException) {
            e1.printStackTrace()
        }
    }

    fun inTheatres() {
        try {
            val jsonObject = JSONObject(result)
            val jsonArray = jsonObject.getJSONArray("results")
            val cVVector = Vector<ContentValues>(jsonArray.length())

            for (i in 0 until jsonArray.length()) {

                val title: String = jsonArray.getJSONObject(i).getString("title")
                val poster: String = jsonArray.getJSONObject(i).getString("poster_path")
                val id: String? = jsonArray.getJSONObject(i).getString("id")

                val tempYear =
                    jsonArray.getJSONObject(i).getString("release_date").split("-").toTypedArray()
                val year = tempYear[0]

                val trimmedQuery = title.toLowerCase().trim { it <= ' ' }
                var finalQuery = trimmedQuery.replace(" ", "-")
                finalQuery = finalQuery.replace("'", "-")
                val slug = finalQuery.replace(":", "") + "-" + year

                if (poster != "null") {
                    val movieValues = ContentValues()
                    movieValues.put(FilmContract.MoviesEntry.MOVIE_ID, id)
                    movieValues.put(FilmContract.MoviesEntry.MOVIE_TITLE, title)
                    movieValues.put(FilmContract.MoviesEntry.MOVIE_YEAR, year)
                    movieValues.put(
                        FilmContract.MoviesEntry.MOVIE_POSTER_LINK,
                        "http://image.tmdb.org/t/p/w185$poster"
                    )
                    cVVector.add(movieValues)
                }
            }

            // Add to database
            var inserted = 0
            if (cVVector.size > 0) {
                val cvArray = arrayOfNulls<ContentValues>(cVVector.size)
                cVVector.toArray(cvArray)
                context.contentResolver.delete(
                    FilmContract.InTheatersMoviesEntry.CONTENT_URI,
                    null,
                    null
                )
                inserted = context.contentResolver.bulkInsert(
                    FilmContract.InTheatersMoviesEntry.CONTENT_URI,
                    cvArray
                )
            }
        } catch (e1: JSONException) {
            e1.printStackTrace()
        }
    }
}