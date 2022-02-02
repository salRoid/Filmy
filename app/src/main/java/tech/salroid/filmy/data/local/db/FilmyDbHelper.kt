package tech.salroid.filmy.data.local.db

import android.content.Context
import androidx.room.Room

object FilmyDbHelper {
    fun getDb(applicationContext: Context): FilmyDatabase {
        return Room.databaseBuilder(
            applicationContext,
            FilmyDatabase::class.java, "filmy"
        ).build()
    }
}
