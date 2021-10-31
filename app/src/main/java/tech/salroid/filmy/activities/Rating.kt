package tech.salroid.filmy.activities

import android.content.Context
import tech.salroid.filmy.network_stuff.VolleySingleton
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONException
import tech.salroid.filmy.BuildConfig

internal object Rating {

    private var imdbRating = "0"
    private var tomatoMeterRating = "0"
    private var audienceRating = "0"
    private var metaScoreRating = "0"
    private var image: String? = null
    private var rottenTomatoPage: String? = null
    private const val OMDB_API_KEY = BuildConfig.OMDB_API_KEY

    @JvmStatic
    fun getRating(context: Context, movie_id_final: String) {

        val volleySingleton = VolleySingleton.getInstance()
        val requestQueue = volleySingleton.requestQueue
        val baseRatingUrl = "http://www.omdbapi.com/?i=$movie_id_final&apikey=$OMDB_API_KEY&tomatoes=true&r=json"

        val jsonObjectRequestForMovieDetails = JsonObjectRequest(baseRatingUrl, null,
                { response ->
                    try {
                        val responseBool = response.getBoolean("Response")
                        if (responseBool) {
                            imdbRating = response.getString("imdbRating")
                            tomatoMeterRating = response.getString("tomatoRating")
                            audienceRating = response.getString("tomatoUserRating")
                            metaScoreRating = response.getString("Metascore")
                            image = response.getString("tomatoImage")
                            rottenTomatoPage = response.getString("tomatoURL")

                            // Above TomatoMeter does not work this does
                            val jsonArray = response.getJSONArray("Ratings")
                            for (i in 0 until jsonArray.length()) {
                                if (jsonArray.getJSONObject(i).getString("Source") == "Rotten Tomatoes") {
                                    tomatoMeterRating = jsonArray.getJSONObject(i).getString("Value")
                                }
                            }

                            setRatingCallback(context, imdbRating, tomatoMeterRating, audienceRating, metaScoreRating, rottenTomatoPage)

                        } else setRatingFailCallback(context)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        setRatingFailCallback(context)
                    }
                }
        ) {
            setRatingFailCallback(context)
        }
        requestQueue.add(jsonObjectRequestForMovieDetails)
    }

    private fun setRatingFailCallback(context: Context) {
        (context as MovieDetailsActivity).setRatingGone()
    }

    private fun setRatingCallback(context: Context, imdb_rating: String, tomatoMeterRating: String, audienceRating: String, metaScoreRating: String, rottenTomatoPage: String?) {
        (context as MovieDetailsActivity).setRating(imdb_rating, tomatoMeterRating, audienceRating, metaScoreRating, rottenTomatoPage)
    }
}