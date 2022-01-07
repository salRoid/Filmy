package tech.salroid.filmy.networking

import tech.salroid.filmy.FilmyApplication.Companion.context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

object VolleySingleton {
    val requestQueue: RequestQueue = Volley.newRequestQueue(context, MyHurlStack())
}