package tech.salroid.filmy.ui.cast_crew

import tech.salroid.filmy.data.local.model.CombinedCredit
import tech.salroid.filmy.data.local.model.CombinedCreditsResponse

private const val ACTING_DEPARTMENT = "Acting"
private const val GENRE_NEWS = 10763
private const val GENRE_TALK = 10767
private const val REGULAR_EPISODE_COUNT = 10
private const val SUPPORTING_BILLING_ORDER = 10
private val SELF_CHARACTER = Regex("^(self|himself|herself|themselves)\\b", RegexOption.IGNORE_CASE)

/**
 * Orders a person's combined cast + crew credits by what they are best known
 * for, de-duped by title + media type.
 *
 * TMDB's `popularity` tracks what is trending right now, so sorting by it puts
 * daily talk shows a person merely guested on ahead of their actual work.
 * Instead credits are ranked by how widely seen the title is (vote count),
 * weighted by how much of it the person was in, with three tiers:
 *  1. work in the department they're known for (acting -> cast roles,
 *     otherwise crew jobs in that department),
 *  2. their other work,
 *  3. appearances as themselves and talk/news shows, unless they were a
 *     regular (e.g. the host).
 */
fun rankKnownFor(
    credits: CombinedCreditsResponse?,
    personName: String?,
    knownForDepartment: String?
): List<CombinedCredit> {
    val cast = credits?.cast.orEmpty().map { RankedCredit(it, isCast = true) }
    val crew = credits?.crew.orEmpty().map { RankedCredit(it, isCast = false) }

    return (cast + crew)
        .sortedWith(
            compareBy<RankedCredit> { it.tier(personName, knownForDepartment) }
                .thenByDescending { it.score() }
                .thenByDescending { it.credit.popularity ?: 0.0 }
        )
        .map { it.credit }
        .distinctBy { "${it.id}-${it.mediaType}" }
}

private class RankedCredit(val credit: CombinedCredit, val isCast: Boolean) {

    fun tier(personName: String?, knownForDepartment: String?): Int = when {
        isIncidentalAppearance(personName) -> 2
        matchesDepartment(knownForDepartment) -> 0
        else -> 1
    }

    fun score(): Double {
        var score = (credit.voteCount ?: 0).toDouble()
        if (isCast && !credit.isTv && (credit.order ?: 0) >= SUPPORTING_BILLING_ORDER) {
            score *= 0.25
        }
        if (credit.isTv) {
            score *= ((credit.episodeCount ?: 0) / 10.0).coerceIn(0.1, 3.0)
        }
        return score
    }

    private fun matchesDepartment(knownForDepartment: String?): Boolean =
        if (knownForDepartment == null || knownForDepartment == ACTING_DEPARTMENT) {
            isCast
        } else {
            !isCast && credit.department == knownForDepartment
        }

    private fun isIncidentalAppearance(personName: String?): Boolean {
        val isRegular = credit.isTv && (credit.episodeCount ?: 0) >= REGULAR_EPISODE_COUNT
        if (isRegular) return false

        val character = credit.character.orEmpty().trim()
        val playsSelf = isCast && (
            SELF_CHARACTER.containsMatchIn(character) ||
                (!personName.isNullOrBlank() && character.startsWith(personName, ignoreCase = true))
            )
        val isTalkShow = credit.isTv && credit.genreIds.any { it == GENRE_TALK || it == GENRE_NEWS }
        return playsSelf || isTalkShow
    }
}
