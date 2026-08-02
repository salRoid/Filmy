package tech.salroid.filmy.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import tech.salroid.filmy.data.local.db.dao.AccountDao
import tech.salroid.filmy.data.local.db.dao.MovieDao
import tech.salroid.filmy.data.local.db.dao.MovieDetailsDao
import tech.salroid.filmy.data.local.db.entity.Movie
import tech.salroid.filmy.data.local.db.entity.MovieDetails
import tech.salroid.filmy.data.local.db.entity.Profile

@Database(
    entities = [Movie::class, MovieDetails::class, Profile::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FilmyDatabase : RoomDatabase() {

    abstract fun movieDao(): MovieDao

    abstract fun movieDetailsDao(): MovieDetailsDao

    abstract fun accountDao(): AccountDao
}