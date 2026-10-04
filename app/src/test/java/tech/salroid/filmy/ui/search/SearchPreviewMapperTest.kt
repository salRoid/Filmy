package tech.salroid.filmy.ui.search

import org.junit.Assert.assertEquals
import org.junit.Test
import tech.salroid.filmy.data.local.model.SearchResult

class SearchPreviewMapperTest {

    private val mapper = SearchPreviewMapper()

    @Test
    fun `map uses the movie title and release date when present`() {
        val result = SearchResult(id = 1, title = "A Movie", releaseDate = "2024-01-15")

        val preview = mapper.map(result)

        assertEquals(1, preview.id)
        assertEquals("A Movie", preview.title)
        assertEquals("15 Jan 2024", preview.readableReleaseDate)
    }

    @Test
    fun `map falls back to the tv name and first air date when title-release date are absent`() {
        val result = SearchResult(id = 2, name = "A Show", firstAirDate = "2023-06-01", mediaType = "tv")

        val preview = mapper.map(result)

        assertEquals("A Show", preview.title)
        assertEquals("01 Jun 2023", preview.readableReleaseDate)
        assertEquals("tv", preview.mediaType)
    }

    @Test
    fun `map falls back to the profile path for a person result`() {
        val result = SearchResult(id = 3, name = "Someone", profilePath = "/person.jpg", mediaType = "person")

        val preview = mapper.map(result)

        assertEquals("https://image.tmdb.org/t/p/w500/person.jpg", preview.posterUrl)
    }

    @Test
    fun `map prefers the movie poster path over the person profile path when both are set`() {
        val result = SearchResult(id = 4, posterPath = "/poster.jpg", profilePath = "/profile.jpg")

        val preview = mapper.map(result)

        assertEquals("https://image.tmdb.org/t/p/w500/poster.jpg", preview.posterUrl)
    }

    @Test
    fun `map defaults to empty strings when nothing is available`() {
        val result = SearchResult(id = 5)

        val preview = mapper.map(result)

        assertEquals("", preview.title)
        assertEquals("https://image.tmdb.org/t/p/w500", preview.posterUrl)
        assertEquals("", preview.readableReleaseDate)
    }

    @Test
    fun `map returns an empty date string for an unparseable release date`() {
        val result = SearchResult(id = 6, title = "A Movie", releaseDate = "not-a-date")

        val preview = mapper.map(result)

        assertEquals("", preview.readableReleaseDate)
    }
}
