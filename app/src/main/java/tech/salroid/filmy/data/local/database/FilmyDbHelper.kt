package tech.salroid.filmy.data.local.database

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
