package tech.salroid.filmy.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import tech.salroid.filmy.data.local.database.dao.MovieDao
import tech.salroid.filmy.data.local.database.dao.MovieDetailsDao
import tech.salroid.filmy.data.local.database.entity.Movie
import tech.salroid.filmy.data.local.database.entity.MovieDetails

@Database(entities = [Movie::class, MovieDetails::class], version = 2)
@TypeConverters(Converters::class)
abstract class FilmyDatabase : RoomDatabase() {

    abstract fun movieDao(): MovieDao

    abstract fun movieDetailsDao(): MovieDetailsDao
}