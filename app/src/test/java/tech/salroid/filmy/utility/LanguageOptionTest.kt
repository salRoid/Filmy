package tech.salroid.filmy.utility

import androidx.core.os.LocaleListCompat
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class LanguageOptionTest {

    @Test
    fun `resolves to the matching SUPPORTED_LANGUAGES apiTag when the app locale is overridden`() {
        val tag = resolveApiLanguageTag(LocaleListCompat.forLanguageTags("de"))

        assertEquals("de-DE", tag)
    }

    @Test
    fun `resolves a region-qualified app tag directly`() {
        val tag = resolveApiLanguageTag(LocaleListCompat.forLanguageTags("hi-IN"))

        assertEquals("hi-IN", tag)
    }

    @Test
    fun `falls back to the raw tag when the app locale isn't one of the supported languages`() {
        val tag = resolveApiLanguageTag(LocaleListCompat.forLanguageTags("fr-FR"))

        assertEquals("fr-FR", tag)
    }

    @Test
    fun `falls back to the device locale formatted as xx-XX when there is no app override`() {
        val tag = resolveApiLanguageTag(LocaleListCompat.getEmptyLocaleList(), deviceLocale = Locale.CANADA_FRENCH)

        assertEquals("fr-CA", tag)
    }

    @Test
    fun `falls back to an uppercased language code when the device locale has no country`() {
        val tag = resolveApiLanguageTag(LocaleListCompat.getEmptyLocaleList(), deviceLocale = Locale.forLanguageTag("eo"))

        assertEquals("eo-EO", tag)
    }
}
