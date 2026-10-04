package tech.salroid.filmy.utility

import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.Locale

// java.time needs API 26 (or core library desugaring) and minSdk is 24, so
// this sticks to SimpleDateFormat.
fun formatReleaseDate(raw: String): String {
    val position = ParsePosition(0)
    val date = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        .apply { isLenient = false }
        .parse(raw, position)
    if (date == null || position.index != raw.length) return ""
    return SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date)
}
