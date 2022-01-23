/*
package tech.salroid.filmy.parser

import org.json.JSONException
import org.json.JSONObject
import tech.salroid.filmy.data.FavouriteData
import java.util.*

class FavouriteMovieParseWork(private val result: String) {

    fun parseFavourite(): List<FavouriteData> {
        val favouriteArray: MutableList<FavouriteData> = ArrayList()
        var favouriteData: FavouriteData

        try {
            val jsonObject = JSONObject(result)
            val jsonArray = jsonObject.getJSONArray("results")

            for (i in 0 until jsonArray.length()) {
                val id: String = jsonArray.getJSONObject(i).getString("id")
                val poster: String = "http://image.tmdb.org/t/p/w185" + jsonArray.getJSONObject(i)
                    .getString("poster_path")
                val title: String? = jsonArray.getJSONObject(i).getString("original_title")
                favouriteData = FavouriteData(id)

                if (poster != "null") {
                    favouriteData.title = title
                    favouriteData.poster = poster
                    favouriteArray.add(favouriteData)
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return favouriteArray
    }
}*/
