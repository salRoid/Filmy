package tech.salroid.filmy.utility

import org.junit.Assert.assertEquals
import org.junit.Test

class DateFormatterUtilTest {

    @Test
    fun `formatReleaseDate turns an ISO date into day, short month and year`() {
        assertEquals("15 Oct 1999", formatReleaseDate("1999-10-15"))
    }

    @Test
    fun `formatReleaseDate returns an empty string for anything it cannot parse`() {
        assertEquals("", formatReleaseDate(""))
        assertEquals("", formatReleaseDate("1999"))
        assertEquals("", formatReleaseDate("not a date"))
    }

    @Test
    fun `formatReleaseDate rejects dates that are not strictly yyyy-MM-dd`() {
        assertEquals("", formatReleaseDate("1999-1-5"))
        assertEquals("", formatReleaseDate("99-10-15"))
        assertEquals("", formatReleaseDate("1999-10-15T00:00:00"))
        assertEquals("", formatReleaseDate("1999-13-40"))
    }
}
