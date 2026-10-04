package tech.salroid.filmy.ui.common

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

/**
 * Nudges this colour step by step [towards] another (usually black or white)
 * until [isEnough] accepts it, keeping its hue. Used to make image-palette
 * colours dark or light enough to read against their background.
 */
internal inline fun Color.shiftedUntil(towards: Color, isEnough: (Color) -> Boolean): Color {
    var color = this
    repeat(12) {
        if (isEnough(color)) return color
        color = lerp(color, towards, 0.2f)
    }
    return color
}
