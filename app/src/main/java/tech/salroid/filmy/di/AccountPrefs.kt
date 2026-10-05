package tech.salroid.filmy.di

import javax.inject.Qualifier

/**
 * The SharedPreferences holding the TMDB session and access token, kept out
 * of backups. The unqualified SharedPreferences is the default settings file.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AccountPrefs
