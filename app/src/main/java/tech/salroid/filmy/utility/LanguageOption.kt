package tech.salroid.filmy.utility

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

/**
 * appTag: passed to AppCompatDelegate.setApplicationLocales() / matches a values-XX resource
 *         folder. null = "System Default" (clears the override).
 * apiTag: full TMDB-ready BCP-47 tag used as the `language` query param.
 * nativeName: always shown in the language's own script, regardless of the current app locale.
 */
data class LanguageOption(
    val appTag: String?,
    val apiTag: String,
    val nativeName: String
)

val SUPPORTED_LANGUAGES = listOf(
    LanguageOption(appTag = null, apiTag = "en-US", nativeName = "System Default"),
    LanguageOption(appTag = "en", apiTag = "en-US", nativeName = "English"),
    LanguageOption(appTag = "de", apiTag = "de-DE", nativeName = "Deutsch"),
    LanguageOption(appTag = "es-ES", apiTag = "es-ES", nativeName = "Español"),
    LanguageOption(appTag = "hi-IN", apiTag = "hi-IN", nativeName = "हिन्दी"),
    LanguageOption(appTag = "nl-NL", apiTag = "nl-NL", nativeName = "Nederlands")
)

/**
 * Resolves [appLocales] (AppCompatDelegate's current per-app override, or an empty
 * list for "System Default") to the TMDB-ready `language` query param tag: the
 * matching [SUPPORTED_LANGUAGES] entry's apiTag when overridden, otherwise the
 * device's own default locale formatted as "xx-XX".
 */
fun resolveApiLanguageTag(appLocales: LocaleListCompat, deviceLocale: Locale = Locale.getDefault()): String {
    if (!appLocales.isEmpty) {
        val tag = appLocales.toLanguageTags()
        return SUPPORTED_LANGUAGES.firstOrNull { it.appTag == tag }?.apiTag ?: tag
    }
    return "${deviceLocale.language}-${deviceLocale.country.ifBlank { deviceLocale.language.uppercase() }}"
}

/**
 * Builds the TMDB `include_image_language` query param for [apiLanguageTag]. The
 * `language` param alone restricts image results to that language, which drops
 * nearly every backdrop (they carry no language) and all images for languages
 * with little artwork, so also allow English and untagged images.
 */
fun resolveImageLanguages(apiLanguageTag: String): String =
    listOf(apiLanguageTag.substringBefore('-'), "en", "null").distinct().joinToString(",")

/**
 * The TMDB `language` tag requests are currently being made with. A language
 * change recreates the activity but keeps its ViewModels (and their cached
 * results), so reactive pipelines (e.g. Movies/Shows paging) combine with
 * [tag] to refetch in the new language, the same way they do for region.
 */
object ApiLanguage {
    private val _tag = MutableStateFlow<String?>(null)
    val tag: StateFlow<String?> = _tag.asStateFlow()

    // Called on every activity creation, which is what a language change triggers.
    fun refresh() {
        update(resolveApiLanguageTag(AppCompatDelegate.getApplicationLocales()))
    }

    fun update(apiLanguageTag: String) {
        _tag.value = apiLanguageTag
    }
}
