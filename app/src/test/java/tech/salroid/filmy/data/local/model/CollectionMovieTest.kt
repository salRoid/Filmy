package tech.salroid.filmy.data.local.model

import org.junit.Assert.assertEquals
import org.junit.Test
import tech.salroid.filmy.data.local.model.collection.CollectionMovie
import tech.salroid.filmy.di.AppModule

class CollectionMovieTest {

    private val json = AppModule.provideJson()

    @Test
    fun `a collection part's title is read from the title field TMDB sends`() {
        val movie = json.decodeFromString<CollectionMovie>(
            """{"id":11,"title":"Star Wars","poster_path":"/p.jpg","release_date":"1977-05-25","media_type":"movie"}"""
        )

        assertEquals("Star Wars", movie.title)
        assertEquals("1977-05-25", movie.releaseDate)
    }

    @Test
    fun `name is accepted as a fallback for the title`() {
        val movie = json.decodeFromString<CollectionMovie>("""{"id":11,"name":"Star Wars"}""")

        assertEquals("Star Wars", movie.title)
    }
}
