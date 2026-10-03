package tech.salroid.filmy.ui.cast_crew

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.palette.graphics.Palette
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Calendar

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class PersonTitleCardTest {

    private fun paletteOf(vararg argb: Long): Palette =
        Palette.from(argb.map { Palette.Swatch(Color(it).toArgb(), 100) })

    @Test
    fun `a bright portrait still gives a dark card with light billing`() {
        val colors = paletteOf(0xFFFFE28A, 0xFFF2B8A0).titleCardColors()!!

        assertTrue(colors.background.luminance() <= 0.08f)
        assertTrue(colors.accent.luminance() >= 0.45f)
        assertTrue(colors.text.luminance() > colors.accent.luminance())
    }

    @Test
    fun `a dark portrait keeps its own hue for the backdrop and lifts the accent`() {
        val colors = paletteOf(0xFF1B2A41, 0xFF3A1F1F).titleCardColors()!!

        assertTrue(colors.background.luminance() <= 0.08f)
        assertTrue(colors.accent.luminance() >= 0.45f)
    }

    private fun day(year: Int, month: Int, dayOfMonth: Int): Calendar =
        Calendar.getInstance().apply { set(year, month - 1, dayOfMonth) }

    @Test
    fun `age counts whole years up to today`() {
        assertEquals(62, ageInYears("1963-12-18", null, today = day(2026, 10, 1)))
    }

    @Test
    fun `age turns over on the birthday itself`() {
        assertEquals(62, ageInYears("1963-12-18", null, today = day(2026, 12, 17)))
        assertEquals(63, ageInYears("1963-12-18", null, today = day(2026, 12, 18)))
    }

    @Test
    fun `age stops at the day of death`() {
        assertEquals(28, ageInYears("1979-04-04", "2008-01-22", today = day(2026, 10, 1)))
    }

    @Test
    fun `age is null without a usable birthday`() {
        assertNull(ageInYears(null, null))
        assertNull(ageInYears("1963", null))
    }
}
