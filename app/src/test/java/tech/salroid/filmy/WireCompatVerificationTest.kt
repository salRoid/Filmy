package tech.salroid.filmy

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import tech.salroid.filmy.data.local.db.entity.Avatar
import tech.salroid.filmy.data.local.model.Genre
import tech.salroid.filmy.data.local.model.Trailers

/**
 * Verifies that kotlinx.serialization's on-disk / on-wire JSON format is compatible
 * with what Gson previously produced/consumed for the shapes stored via Room
 * TypeConverters and returned by the TMDB API - added as part of the Gson ->
 * kotlinx.serialization migration to confirm no Room migration is required.
 */
class WireCompatVerificationTest {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    @Test
    fun `Genre list encodes with same key names and no pretty-printing as Gson did`() {
        val genres = listOf(Genre(id = 28, name = "Action"), Genre(id = 12, name = "Adventure"))
        val encoded = json.encodeToString(genres)
        // Gson's Gson().toJson(genres) for these @SerializedName-mapped fields produced exactly this.
        assertEquals("""[{"id":28,"name":"Action"},{"id":12,"name":"Adventure"}]""", encoded)
    }

    @Test
    fun `Trailers with empty lists round-trips through kotlinx encode-decode`() {
        // kotlinx omits properties that equal their declared default (encodeDefaults=false),
        // so this is more compact than Gson's `{"quicktime":[],"youtube":[]}` - that's fine,
        // it's still exactly what re-produces the original object on decode.
        val trailers = Trailers()
        val encoded = json.encodeToString(trailers)
        assertEquals("{}", encoded)

        val decoded = json.decodeFromString<Trailers>(encoded)
        assertEquals(trailers, decoded)
    }

    @Test
    fun `decoding old verbose Gson-written Trailers JSON still works`() {
        // This is exactly the form Gson always wrote (it never omits non-null empty
        // collections, only null fields), so this is what's actually sitting in
        // production Room databases today.
        val oldGsonJson = """{"quicktime":[],"youtube":[{"name":"Trailer","size":"1080","source":"abc123","type":"Trailer"}]}"""
        val decoded = json.decodeFromString<Trailers>(oldGsonJson)
        assertEquals(0, decoded.quicktime.size)
        assertEquals(1, decoded.youtube.size)
        assertEquals("abc123", decoded.youtube[0].source)
    }

    @Test
    fun `decoding old Gson-written Avatar JSON missing a key yields null, not a default instance`() {
        // This is exactly what Gson would have persisted: it omits null fields on encode,
        // and because Gson uses Unsafe allocation for Kotlin data classes (bypassing the
        // constructor's default-value expressions) it always decodes an absent key back to
        // null, never to a constructor default like Gravatar() or Tmdb().
        val oldGsonJson = """{"tmdb":{"avatar_path":"/abc.jpg"}}"""

        val decoded = json.decodeFromString<Avatar>(oldGsonJson)

        assertNull(
            "gravatar must decode to null (matching Gson's behavior) since the key is absent, " +
                "not to a Gravatar() default instance",
            decoded.gravatar
        )
        assertEquals("/abc.jpg", decoded.tmdb?.avatarPath)
    }

    @Test
    fun `decoding a fully empty old Avatar JSON object yields both fields null`() {
        val oldGsonJson = """{}"""
        val decoded = json.decodeFromString<Avatar>(oldGsonJson)
        assertNull(decoded.gravatar)
        assertNull(decoded.tmdb)
    }
}
