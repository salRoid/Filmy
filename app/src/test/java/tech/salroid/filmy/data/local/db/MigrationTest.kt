package tech.salroid.filmy.data.local.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import tech.salroid.filmy.data.local.db.entity.Profile

/**
 * Upgrades a database shaped like the one 3.1.0 shipped (Room version 2) with
 * the app's real migrations. The v2 schema was never exported, so its tables
 * are recreated here from the 3.1.0 entities.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class MigrationTest {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private var db: FilmyDatabase? = null

    @Before
    fun createVersion2Database() {
        context.deleteDatabase(DB_NAME)
        SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath(DB_NAME).apply {
            parentFile?.mkdirs()
        }, null).use { v2 ->
            v2.execSQL(
                """
                CREATE TABLE `movies` (
                    `id` INTEGER NOT NULL, `adult` INTEGER, `backdropPath` TEXT,
                    `genreIds` TEXT NOT NULL, `originalLanguage` TEXT, `originalTitle` TEXT,
                    `overview` TEXT, `popularity` REAL, `posterPath` TEXT, `releaseDate` TEXT,
                    `title` TEXT, `video` INTEGER, `voteAverage` REAL, `voteCount` INTEGER,
                    `type` INTEGER NOT NULL, PRIMARY KEY(`id`, `type`)
                )
                """.trimIndent()
            )
            v2.execSQL(
                """
                CREATE TABLE `movie_details` (
                    `id` INTEGER NOT NULL, `adult` INTEGER, `backdropPath` TEXT,
                    `belongsToCollection` TEXT, `budget` INTEGER, `genres` TEXT NOT NULL,
                    `homepage` TEXT, `imdbId` TEXT, `originalLanguage` TEXT,
                    `originalTitle` TEXT, `overview` TEXT, `popularity` REAL,
                    `posterPath` TEXT, `productionCompanies` TEXT NOT NULL,
                    `productionCountries` TEXT NOT NULL, `releaseDate` TEXT,
                    `revenue` INTEGER, `runtime` INTEGER, `spokenLanguages` TEXT NOT NULL,
                    `status` TEXT, `tagline` TEXT, `title` TEXT, `video` INTEGER,
                    `voteAverage` REAL, `voteCount` INTEGER, `trailers` TEXT,
                    `type` INTEGER NOT NULL, `favorite` INTEGER NOT NULL,
                    `watchlist` INTEGER NOT NULL, PRIMARY KEY(`id`, `type`)
                )
                """.trimIndent()
            )
            v2.execSQL(
                """
                INSERT INTO movie_details (
                    id, genres, productionCompanies, productionCountries, spokenLanguages,
                    title, type, favorite, watchlist
                ) VALUES (550, '[]', '[]', '[]', '[]', 'Fight Club', 0, 1, 1)
                """.trimIndent()
            )
            v2.version = 2
        }
    }

    @After
    fun tearDown() {
        db?.close()
        context.deleteDatabase(DB_NAME)
    }

    @Test
    fun `a 3_1_0 database upgrades to the current version and keeps saved titles`() {
        val migrated = Room.databaseBuilder(context, FilmyDatabase::class.java, DB_NAME)
            .addMigrations(MIGRATION_2_4, MIGRATION_4_5)
            .allowMainThreadQueries()
            .build()
            .also { db = it }

        // Opening the database runs the migrations and Room's schema validation.
        migrated.openHelper.writableDatabase
            .query("SELECT watched, watchlist FROM movie_details WHERE id = 550")
            .use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertEquals(1, cursor.getInt(0))
                assertEquals(1, cursor.getInt(1))
            }

        migrated.accountDao().insert(Profile(id = 1, name = "Someone"))
        assertEquals(1, migrated.accountDao().getProfile().size)
    }

    private companion object {
        const val DB_NAME = "migration-test"
    }
}
