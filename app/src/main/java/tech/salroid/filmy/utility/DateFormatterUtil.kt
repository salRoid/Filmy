package tech.salroid.filmy.utility

import java.text.SimpleDateFormat
import java.util.Locale

private val ISO_DATE = Regex("""\d{4}-\d{2}-\d{2}""")

fun formatReleaseDate(raw: String): String {
    if (!ISO_DATE.matches(raw)) return ""
    val date = runCatching {
        SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { isLenient = false }.parse(raw)
    }.getOrNull() ?: return ""
    return SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date)
}
