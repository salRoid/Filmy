package tech.salroid.filmy.utility

import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun formateReleaseDate(raw: String): String {
    return runCatching {
        LocalDate.parse(raw)
            .format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
    }.getOrElse { "" }
}