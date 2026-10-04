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
}
