package tech.salroid.filmy.ui.common

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ColorContrastTest {

    @Test
    fun `a colour that already satisfies the condition is returned untouched`() {
        val navy = Color(0xFF0A1A2F)

        assertEquals(navy, navy.shiftedUntil(Color.Black) { it.luminance() <= 0.16f })
    }

    @Test
    fun `a pale colour is darkened until it is readable on a light page`() {
        val paleYellow = Color(0xFFFFF3A0)

        val shifted = paleYellow.shiftedUntil(Color.Black) { it.luminance() <= 0.16f }

        assertTrue(shifted.luminance() <= 0.16f)
        // Still recognisably the same hue: more red and green than blue.
        assertTrue(shifted.red > shifted.blue && shifted.green > shifted.blue)
    }

    @Test
    fun `a dark colour is lightened until it is readable on a dark page`() {
        val shifted = Color(0xFF2A0A0A).shiftedUntil(Color.White) { it.luminance() >= 0.35f }

        assertTrue(shifted.luminance() >= 0.35f)
    }

    @Test
    fun `it gives up rather than looping forever on an impossible condition`() {
        val result = Color.Red.shiftedUntil(Color.Black) { false }

        assertTrue(result.luminance() < Color.Red.luminance())
    }
}
