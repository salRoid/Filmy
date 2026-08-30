package tech.salroid.filmy.data.local.db.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import tech.salroid.filmy.data.local.db.entity.Profile

@Dao
interface AccountDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(profile: Profile)

    @Delete
    fun delete(profile: Profile): Int

    @Query("DELETE FROM profile")
    fun deleteAll(): Int

    @Query("SELECT * FROM profile")
    fun getProfile(): List<Profile>

    @Query("SELECT * FROM profile")
    fun getProfileFlow(): Flow<List<Profile>>
}