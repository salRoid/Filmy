/*
package tech.salroid.filmy.parser

import org.json.JSONException
import org.json.JSONObject
import tech.salroid.filmy.data.WatchlistData
import java.util.*

class WatchListMovieParseWork(private val result: String) {

    fun parseWatchList(): List<WatchlistData> {
        val favouriteArray: MutableList<WatchlistData> = ArrayList()
        var favouriteData: WatchlistData

        try {
            val jsonObject = JSONObject(result)
            val jsonArray = jsonObject.getJSONArray("results")

            for (i in 0 until jsonArray.length()) {
                val id = jsonArray.getJSONObject(i).getString("id")
                val poster = "http://image.tmdb.org/t/p/w185" + jsonArray.getJSONObject(i)
                    .getString("poster_path")
                val title = jsonArray.getJSONObject(i).getString("original_title")

                favouriteData = WatchlistData(id)
                if (poster != "null") {
                    favouriteData.id = id
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
