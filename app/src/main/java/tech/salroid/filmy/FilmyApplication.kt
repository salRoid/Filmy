package tech.salroid.filmy

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FilmyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        var instance: FilmyApplication? = null
            private set

        @JvmStatic
        val appContext: Context
            get() = instance!!.applicationContext
    }
}