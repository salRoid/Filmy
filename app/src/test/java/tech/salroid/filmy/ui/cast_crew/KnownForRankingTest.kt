package tech.salroid.filmy.ui.cast_crew

import org.junit.Assert.assertEquals
import org.junit.Test
import tech.salroid.filmy.data.local.model.CombinedCredit
import tech.salroid.filmy.data.local.model.CombinedCreditsResponse

class KnownForRankingTest {

    private val talkShowGuestSpot = CombinedCredit(
        id = 1, name = "The Tonight Show", mediaType = "tv", character = "Self - Guest",
        popularity = 900.0, voteCount = 300, episodeCount = 4, genreIds = listOf(10767)
    )

    @Test
    fun `a film role outranks a trending talk show guest spot`() {
        val film = CombinedCredit(
            id = 2, title = "Inception", mediaType = "movie", character = "Dom Cobb",
            popularity = 30.0, voteCount = 35000, order = 0
        )

        val ranked = rankKnownFor(CombinedCreditsResponse(cast = listOf(talkShowGuestSpot, film)), "Leonardo DiCaprio", "Acting")

        assertEquals(listOf("Inception", "The Tonight Show"), ranked.map { it.displayTitle })
    }

    @Test
    fun `a talk show the person hosts is not treated as a guest spot`() {
        val hosted = talkShowGuestSpot.copy(character = "Self - Host", episodeCount = 2000)
        val cameo = CombinedCredit(
            id = 3, title = "Jurassic World", mediaType = "movie", character = "Jimmy Fallon",
            voteCount = 20000, order = 30
        )

        val ranked = rankKnownFor(CombinedCreditsResponse(cast = listOf(cameo, hosted)), "Jimmy Fallon", "Acting")

        assertEquals(listOf("The Tonight Show", "Jurassic World"), ranked.map { it.displayTitle })
    }

    @Test
    fun `crew work in the known-for department leads and wins the de-dupe for the same title`() {
        val cameoInOwnFilm = CombinedCredit(
            id = 4, title = "Following", mediaType = "movie", character = "Man in crowd", voteCount = 1500, order = 12
        )
        val directed = CombinedCredit(
            id = 4, title = "Following", mediaType = "movie", job = "Director", department = "Directing", voteCount = 1500
        )
        val produced = CombinedCredit(
            id = 5, title = "Man of Steel", mediaType = "movie", job = "Producer", department = "Production", voteCount = 15000
        )

        val ranked = rankKnownFor(
            CombinedCreditsResponse(cast = listOf(cameoInOwnFilm), crew = listOf(produced, directed)),
            "Christopher Nolan",
            "Directing"
        )

        assertEquals(listOf("Following" to "Director", "Man of Steel" to "Producer"), ranked.map { it.displayTitle to it.role })
    }

    @Test
    fun `a one-episode guest role ranks below a series regular role`() {
        val guest = CombinedCredit(
            id = 6, name = "The Simpsons", mediaType = "tv", character = "Stradivarius Cain (voice)",
            voteCount = 10000, episodeCount = 1
        )
        val lead = CombinedCredit(
            id = 7, name = "Malcolm in the Middle", mediaType = "tv", character = "Hal",
            voteCount = 4000, episodeCount = 151
        )

        val ranked = rankKnownFor(CombinedCreditsResponse(cast = listOf(guest, lead)), "Bryan Cranston", "Acting")

        assertEquals(listOf("Malcolm in the Middle", "The Simpsons"), ranked.map { it.displayTitle })
    }
}
