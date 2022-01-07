package tech.salroid.filmy.networking

import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import tech.salroid.filmy.FilmyApplication.Companion.context

object TmdbVolleySingleton {
    val requestQueue: RequestQueue = Volley.newRequestQueue(context)
}