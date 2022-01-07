package tech.salroid.filmy.tmdb_account

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONException
import org.json.JSONObject
import tech.salroid.filmy.BuildConfig
import tech.salroid.filmy.R
import tech.salroid.filmy.networking.TmdbVolleySingleton
import tech.salroid.filmy.views.CustomToast.show
import java.io.UnsupportedEncodingException
import java.util.*

class AddRating {

    private val apiKey = BuildConfig.TMDB_API_KEY
    private val pref = "SESSION_PREFERENCE"
    private val tmdbVolleySingleton = TmdbVolleySingleton.getInstance()
    private val tmdbrequestQueue = tmdbVolleySingleton.requestQueue
    private var context: Context? = null
    private var notifId = 0
    private lateinit var mNotifyManager: NotificationManager
    private lateinit var mBuilder: NotificationCompat.Builder

    fun addRating(context: Context, media_id: String, rating: Int) {
        this.context = context
        val sp = context.getSharedPreferences(pref, Context.MODE_PRIVATE)
        val sessionId = sp.getString("session", " ")

        //issue notification to show indeterminate progress
        val random = Random()
        notifId = random.nextInt(10000)
        mNotifyManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        mBuilder = NotificationCompat.Builder(context)
        mBuilder.setContentTitle("Filmy")
            .setContentText("Adding to ratings..")
            .setSmallIcon(R.drawable.ic_stat_status)
        mBuilder.setProgress(0, 0, true)
        mNotifyManager.notify(notifId, mBuilder.build())

        getProfile(sessionId, media_id, rating)
    }

    private fun getProfile(session_id: String?, media_id: String, rating: Int) {
        val url = "https://api.themoviedb.org/3/account?api_key=$apiKey&session_id=$session_id"
        val jsonObjectRequest = JsonObjectRequest(url, null,
            { response ->
                parseOutput(
                    response,
                    session_id,
                    Integer.valueOf(media_id),
                    rating
                )
            }) { error -> Log.e("webi", "Volley Error: " + error.cause) }
        tmdbrequestQueue.add(jsonObjectRequest)
    }

    private fun parseOutput(response: JSONObject, sessionId: String?, mediaId: Int, rating: Int) {
        try {
            val id = response.getString("id")
            val query =
                "https://api.themoviedb.org/3/movie/$mediaId/rating?api_key=$apiKey&session_id=$sessionId"
            add(query, rating)
        } catch (e: JSONException) {
            e.printStackTrace()
            show(context, "You are not logged in. Please login from account.", false)
        }
    }

    private fun add(query: String, rating: Int) {
        val jsonBody = JSONObject()
        try {
            jsonBody.put("value", rating)
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
        tmdbrequestQueue.add(jsonObjectRequest)
    }

    private fun parseMarkedResponse(response: JSONObject) {
        try {
            val statusCode = response.getInt("status_code")
            if (statusCode == 1) {
                show(context, "Your rating has been added.", false)
                mBuilder.setContentText("Movie rating done.") // Removes the progress bar
                    .setProgress(0, 0, false)
                mNotifyManager.notify(notifId, mBuilder.build())
                mNotifyManager.cancel(notifId)
            } else if (statusCode == 12) {
                show(context, "Rating updated.", false)
                mBuilder.setContentText("Rating updated.") // Removes the progress bar
                    .setProgress(0, 0, false)
                mNotifyManager.notify(notifId, mBuilder.build())
                mNotifyManager.cancel(notifId)
            }
        } catch (e: JSONException) {
            Log.d("webi", e.cause.toString())
            mBuilder.setContentText("Can't add rating.") // Removes the progress bar
                .setProgress(0, 0, false)
            mNotifyManager.notify(notifId, mBuilder.build())
            mNotifyManager.cancel(notifId)
            show(context, "Can't add rating.", false)
        }
    }
}