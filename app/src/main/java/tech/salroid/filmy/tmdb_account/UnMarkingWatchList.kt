/*
package tech.salroid.filmy.tmdb_account

import android.content.Context
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONException
import org.json.JSONObject
import tech.salroid.filmy.BuildConfig
import tech.salroid.filmy.networking.TmdbVolleySingleton
import tech.salroid.filmy.views.CustomToast.show
import java.io.UnsupportedEncodingException
import java.util.*

class UnMarkingWatchList {

    private val apiKey = BuildConfig.TMDB_API_KEY
    private val SESSION_PREF = "SESSION_PREFERENCE"
    private val tmdbRequestQueue = TmdbVolleySingleton.requestQueue
    private var context: Context? = null
    private var listener: UnmarkedListener? = null
    private var position = 0

    fun removeFromWatchList(context: Context, media_id: String, position: Int) {
        this.context = context
        this.position = position
        val sp = context.getSharedPreferences(SESSION_PREF, Context.MODE_PRIVATE)
        val sessionId = sp.getString("session", " ")
        getProfile(sessionId, media_id)
    }

    private fun getProfile(session_id: String?, media_id: String) {
        val uri = "https://api.themoviedb.org/3/account?api_key=$apiKey&session_id=$session_id"
        val jsonObjectRequest = JsonObjectRequest(uri, null,
            { response ->
                parseOutput(
                    response,
                    session_id,
                    Integer.valueOf(media_id)
                )
            }) { error -> Log.e("webi", "Volley Error: " + error.cause) }
        tmdbRequestQueue.add(jsonObjectRequest)
    }

    private fun parseOutput(response: JSONObject, session_id: String?, media_id: Int) {
        try {
            val accountId = response.getString("id")
            val query =
                "https://api.themoviedb.org/3/account/" + accountId + "/watchlist?api_key=" +
                        apiKey + "&session_id=" + session_id
            removeWatchFinal(query, media_id)
        } catch (e: JSONException) {
            e.printStackTrace()
            show(context, "You are not logged in. Please login from account.", false)
        }
    }

    private fun removeWatchFinal(query: String, media_id: Int) {
        val jsonBody = JSONObject()
        try {
            jsonBody.put("media_type", "movie")
            jsonBody.put("media_id", media_id)
            jsonBody.put("watchlist", false)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val mRequestBody = jsonBody.toString()
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(query,
            null,
            Response.Listener { response -> parseMarkedResponse(response) },
            Response.ErrorListener { error -> Log.e("webi", "Volley Error: " + error.cause) }) {
            override fun getBody(): ByteArray? {
                return try {
                    mRequestBody.toByteArray(charset("utf-8"))
                } catch (uee: UnsupportedEncodingException) {
                    VolleyLog.wtf(
                        "Unsupported Encoding while trying to get the bytes of %s using %s",
                        mRequestBody,
                        "utf-8"
                    )
                    null
                }
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers: MutableMap<String, String> = HashMap()
                headers["content-type"] = "application/json;charset=utf-8"
                return headers
            }
        }
        tmdbRequestQueue.add(jsonObjectRequest)
    }

    private fun parseMarkedResponse(response: JSONObject) {
        if (listener != null) listener!!.unmarked(position)
        try {
            val statusCode = response.getInt("status_code")
            if (statusCode == 13) {
                show(context, "Movie removed from watchlist.", false)
            } else {
                show(context, "Can't remove from watchlist.", false)
            }
        } catch (e: JSONException) {
            Log.d("webi", e.cause.toString())
            show(context, "Can't remove from watchlist.", false)
        }
    }

    fun setUnmarkedListener(listener: UnmarkedListener?) {
        this.listener = listener
    }

    interface UnmarkedListener {
        fun unmarked(position: Int)
    }
}*/
