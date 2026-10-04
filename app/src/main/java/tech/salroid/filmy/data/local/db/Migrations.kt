package tech.salroid.filmy.data.local.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Renames `movie_details.favorite` to `watched` and adds the `videos` column.
 * SQLite's ALTER TABLE RENAME COLUMN needs SQLite 3.25+, which isn't guaranteed
 * on this app's minSdk 24 devices, so the rename is done via the portable
 * rebuild-table pattern instead. Column list/types are copied verbatim from
 * the actual Room-generated v5 schema (app/schemas/.../5.json) minus the
 * userRating column that MIGRATION_4_5 adds afterwards.
 */
val MIGRATION_2_4 = object : Migration(2, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `movie_details_new` (
                `id` INTEGER NOT NULL, `adult` INTEGER, `backdropPath` TEXT,
                `belongsToCollection` TEXT, `budget` INTEGER, `genres` TEXT NOT NULL,
                `homepage` TEXT, `imdbId` TEXT, `originalLanguage` TEXT,
                `originalTitle` TEXT, `overview` TEXT, `popularity` REAL,
                `posterPath` TEXT, `productionCompanies` TEXT NOT NULL,
                `productionCountries` TEXT NOT NULL, `releaseDate` TEXT,
                `revenue` INTEGER, `runtime` INTEGER, `spokenLanguages` TEXT NOT NULL,
                `status` TEXT, `tagline` TEXT, `title` TEXT, `video` INTEGER,
                `voteAverage` REAL, `voteCount` INTEGER, `trailers` TEXT, `videos` TEXT,
                `type` INTEGER NOT NULL, `watched` INTEGER NOT NULL, `watchlist` INTEGER NOT NULL,
                PRIMARY KEY(`id`, `type`)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            INSERT INTO movie_details_new (
                id, adult, backdropPath, belongsToCollection, budget, genres, homepage,
                imdbId, originalLanguage, originalTitle, overview, popularity, posterPath,
                productionCompanies, productionCountries, releaseDate, revenue, runtime,
                spokenLanguages, status, tagline, title, video, voteAverage, voteCount,
                trailers, videos, type, watched, watchlist
            )
            SELECT
                id, adult, backdropPath, belongsToCollection, budget, genres, homepage,
                imdbId, originalLanguage, originalTitle, overview, popularity, posterPath,
                productionCompanies, productionCountries, releaseDate, revenue, runtime,
                spokenLanguages, status, tagline, title, video, voteAverage, voteCount,
                trailers, NULL, type, favorite, watchlist
            FROM movie_details
            """.trimIndent()
        )
        db.execSQL("DROP TABLE movie_details")
        db.execSQL("ALTER TABLE movie_details_new RENAME TO movie_details")
    }
}

/** Adds the nullable `userRating` column - a plain additive ALTER TABLE, safe on all SQLite versions. */
val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE movie_details ADD COLUMN userRating REAL")
    }
}
