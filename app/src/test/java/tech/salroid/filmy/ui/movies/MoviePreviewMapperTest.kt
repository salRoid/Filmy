package tech.salroid.filmy.ui.movies

import org.junit.Assert.assertEquals
import org.junit.Test
import tech.salroid.filmy.data.local.db.entity.Movie

class MoviePreviewMapperTest {

    private val mapper = MoviePreviewMapper()

    @Test
    fun `map builds the full poster URL and formats the release date`() {
        val movie = Movie(id = 1, title = "Inception", posterPath = "/poster.jpg", releaseDate = "2010-07-16")

        val preview = mapper.map(movie)

        assertEquals(1, preview.id)
        assertEquals("Inception", preview.title)
        assertEquals("https://image.tmdb.org/t/p/w500/poster.jpg", preview.posterUrl)
        assertEquals("16 Jul 2010", preview.readableReleaseDate)
    }

    @Test
    fun `map defaults to empty strings when title, poster path and release date are absent`() {
        val movie = Movie(id = 2)

        val preview = mapper.map(movie)

        assertEquals("", preview.title)
        assertEquals("https://image.tmdb.org/t/p/w500", preview.posterUrl)
        assertEquals("", preview.readableReleaseDate)
    }

    @Test
    fun `map returns an empty date string for an unparseable release date`() {
        val movie = Movie(id = 3, releaseDate = "not-a-date")

        val preview = mapper.map(movie)

        assertEquals("", preview.readableReleaseDate)
    }
}
