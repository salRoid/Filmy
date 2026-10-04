package tech.salroid.filmy.data.local.db

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider

/** A fresh in-memory Room database for a single test - callers are responsible for closing it. */
fun createTestDatabase(): FilmyDatabase =
    Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), FilmyDatabase::class.java)
        .allowMainThreadQueries()
        .build()
