package tech.salroid.filmy.data.local.db

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import tech.salroid.filmy.data.local.model.Collection
import tech.salroid.filmy.data.local.model.Genre
import tech.salroid.filmy.data.local.model.Trailers

class ConvertersTest {

    private val converters = Converters()

    // Regression for a real crash: rows written by the pre-migration Gson
    // converters stored the literal string "null" (Gson's behavior for
    // encoding a null reference) rather than an actual NULL column. The
    // new kotlinx.serialization-based decoder must treat that the same as
    // a real null/empty value instead of throwing JsonDecodingException.

    @Test
    fun `fromStringOfCollection returns null for the legacy literal string null`() {
        assertNull(converters.fromStringOfCollection("null"))
    }

    @Test
    fun `fromString returns null for the legacy literal string null`() {
        assertNull(converters.fromString("null"))
    }

    @Test
    fun `fromStringOfArrayListOfGenres returns an empty list for the legacy literal string null`() {
        assertEquals(emptyList<Genre>(), converters.fromStringOfArrayListOfGenres("null"))
    }

    @Test
    fun `fromStringOfTrailers returns null for arbitrary malformed JSON`() {
        assertNull(converters.fromStringOfTrailers("{not valid json"))
    }

    @Test
    fun `fromStringOfCollection still returns null for an actually null or empty value`() {
        assertNull(converters.fromStringOfCollection(null))
        assertNull(converters.fromStringOfCollection(""))
    }

    @Test
    fun `fromCollection then fromStringOfCollection round-trips a real value`() {
        val collection = Collection(id = 1, name = "The Matrix Collection", posterPath = "/poster.jpg")

        val encoded = converters.fromCollection(collection)
        val decoded = converters.fromStringOfCollection(encoded)

        assertEquals(collection, decoded)
    }
}
