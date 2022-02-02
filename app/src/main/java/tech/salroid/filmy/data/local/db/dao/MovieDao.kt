package tech.salroid.filmy.data.local.db.dao

import androidx.room.*
import tech.salroid.filmy.data.local.db.entity.Movie

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(movies: List<Movie>)

    @Delete
    fun delete(movie: Movie)

    @Query("SELECT * FROM movies")
    fun getAll(): List<Movie>

    @Query("SELECT * FROM movies WHERE type = :type")
    fun getAllTrending(type: Int = 0): List<Movie>

    @Query("SELECT * FROM movies WHERE type = :type")
    fun getAllUpcoming(type: Int = 1): List<Movie>

    @Query("SELECT * FROM movies WHERE type = :type")
    fun getAllInTheaters(type: Int = 2): List<Movie>

    @Update
    fun updateMovie(movie: Movie): Int
}