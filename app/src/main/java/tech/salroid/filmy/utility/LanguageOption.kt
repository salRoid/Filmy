package tech.salroid.filmy.utility

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
