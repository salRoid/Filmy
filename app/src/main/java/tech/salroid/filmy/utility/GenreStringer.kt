package tech.salroid.filmy.utility

object GenreStringer {

    fun getEmoji(genreString: String?): String = when (genreString?.uppercase()) {
        "DRAMA" -> "\uD83D\uDE28"
        "FANTASY" -> "\uD83E\uDDD9"
        "SCIENCE FICTION" -> "\uD83D\uDE80️"
        "ACTION" -> "\uD83E\uDD20"
        "ADVENTURE" -> "\uD83C\uDFDE️"
        "CRIME" -> "\uD83D\uDC6E"
        "THRILLER" -> "\uD83D\uDDE1️"
        "COMEDY" -> "\uD83E\uDD23"
        "HORROR" -> "\uD83E\uDDDF"
        "MYSTERY" -> "\uD83D\uDD75️"
        else -> ""
    }
}