package tech.salroid.filmy.parser

import org.json.JSONException
import org.json.JSONObject
import tech.salroid.filmy.data.SearchData
import java.util.*

class SearchResultParseWork(private val result: String) {

    fun parseSearchData(): List<SearchData> {
        val searchArray = ArrayList<SearchData>()

        var searchData: SearchData

        try {
            val jsonObject = JSONObject(result)
            val jsonArray = jsonObject.getJSONArray("results")

            for (i in 0 until jsonArray.length()) {
                val id = jsonArray.getJSONObject(i).getString("id")
                val date = jsonArray.getJSONObject(i).getString("release_date")
                val moviePoster = "http://image.tmdb.org/t/p/w185" + jsonArray.getJSONObject(i)
                    .getString("poster_path")
                val title = jsonArray.getJSONObject(i).getString("original_title")
                val type = "movie"

                searchData = SearchData(id)
                if (!(moviePoster == "null" && date == "null")) {
                    searchData.date = date
                    searchData.type = type
                    searchData.movie = title
                    searchData.poster = moviePoster
                    searchArray.add(searchData)
                }
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return searchArray
    }
}