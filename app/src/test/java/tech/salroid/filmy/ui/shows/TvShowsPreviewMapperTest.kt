package tech.salroid.filmy.ui.shows

import org.junit.Assert.assertEquals
import org.junit.Test
import tech.salroid.filmy.data.local.model.TvShow

class TvShowsPreviewMapperTest {

    private val mapper = TvShowsPreviewMapper()

    @Test
    fun `map builds the full poster URL and formats the first air date`() {
        val tvShow = TvShow(id = 1, name = "Breaking Bad", posterPath = "/poster.jpg", firstAirDate = "2008-01-20")

        val preview = mapper.map(tvShow)

        assertEquals(1, preview.id)
        assertEquals("Breaking Bad", preview.title)
        assertEquals("https://image.tmdb.org/t/p/w500/poster.jpg", preview.posterUrl)
        assertEquals("20 Jan 2008", preview.firstAirReadableDate)
    }

    @Test
    fun `map defaults to empty strings when name, poster path and first air date are absent`() {
        val tvShow = TvShow(id = 2)

        val preview = mapper.map(tvShow)

        assertEquals("", preview.title)
        assertEquals("https://image.tmdb.org/t/p/w500", preview.posterUrl)
        assertEquals("", preview.firstAirReadableDate)
    }

    @Test
    fun `map returns an empty date string for an unparseable first air date`() {
        val tvShow = TvShow(id = 3, firstAirDate = "not-a-date")

        val preview = mapper.map(tvShow)

        assertEquals("", preview.firstAirReadableDate)
    }
}
