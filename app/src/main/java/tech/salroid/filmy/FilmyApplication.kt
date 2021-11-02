package tech.salroid.filmy

import android.app.Application
import android.content.Context

class FilmyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        var instance: FilmyApplication? = null
            private set

        @JvmStatic
        val context: Context
            get() = instance!!.applicationContext
    }
}