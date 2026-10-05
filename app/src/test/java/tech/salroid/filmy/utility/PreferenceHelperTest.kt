package tech.salroid.filmy.utility

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import tech.salroid.filmy.FakeSharedPreferences
import tech.salroid.filmy.utility.PreferenceHelper.addRecentSearch
import tech.salroid.filmy.utility.PreferenceHelper.clearRecentSearches
import tech.salroid.filmy.utility.PreferenceHelper.recentSearches
import tech.salroid.filmy.utility.PreferenceHelper.removeRecentSearch
import tech.salroid.filmy.utility.PreferenceHelper.selectedCountryFlow
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class PreferenceHelperTest {

    private lateinit var context: Context
    private lateinit var prefs: SharedPreferences

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
    }

    // --- selected country ---

    @Test
    fun `getSelectedCountry falls back to the device locale when nothing is stored`() {
        // Locale.getDefault() is global, mutable JVM state shared with every other
        // test class in the process - read it rather than mutating it, so this
        // can't race a concurrently-running test class that does the same.
        val expected = Locale.getDefault().country.takeIf { it.isNotBlank() } ?: "US"

        assertEquals(expected, PreferenceHelper.getSelectedCountry(context))
    }

    @Test
    fun `setSelectedCountry then getSelectedCountry round-trips`() {
        PreferenceHelper.setSelectedCountry(context, "IN")

        assertEquals("IN", PreferenceHelper.getSelectedCountry(context))
    }

    @Test
    fun `selectedCountryFlow emits the current value immediately on subscription`() = runTest {
        PreferenceHelper.setSelectedCountry(context, "IN")

        prefs.selectedCountryFlow().test {
            assertEquals("IN", awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `selectedCountryFlow emits again when the country changes`() = runTest {
        prefs.selectedCountryFlow().test {
            awaitItem() // initial

            PreferenceHelper.setSelectedCountry(context, "GB")

            assertEquals("GB", awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `selectedCountryFlow does not re-emit for an unrelated preference change`() = runTest {
        prefs.selectedCountryFlow().test {
            awaitItem() // initial

            prefs.edit().putString("some_other_key", "value").apply()

            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    // --- cold start ---

    @Test
    fun `isColdStart defaults to true`() {
        assertTrue(PreferenceHelper.isColdStart(context))
    }

    @Test
    fun `setColdStartDone flips isColdStart to false`() {
        PreferenceHelper.setColdStartDone(context)

        assertFalse(PreferenceHelper.isColdStart(context))
    }

    // --- recent searches ---

    @Test
    fun `recentSearches is empty before anything is added`() {
        assertEquals(emptyList<String>(), prefs.recentSearches())
    }

    @Test
    fun `addRecentSearch prepends the newest query first`() {
        prefs.addRecentSearch("batman")
        prefs.addRecentSearch("inception")

        assertEquals(listOf("inception", "batman"), prefs.recentSearches())
    }

    @Test
    fun `addRecentSearch trims whitespace and ignores a blank query`() {
        prefs.addRecentSearch("  dune  ")
        prefs.addRecentSearch("   ")

        assertEquals(listOf("dune"), prefs.recentSearches())
    }

    @Test
    fun `addRecentSearch moves a re-searched query to the front instead of duplicating it`() {
        prefs.addRecentSearch("batman")
        prefs.addRecentSearch("inception")
        prefs.addRecentSearch("BATMAN")

        assertEquals(listOf("BATMAN", "inception"), prefs.recentSearches())
    }

    @Test
    fun `addRecentSearch caps the list at 8 entries, dropping the oldest`() {
        (1..9).forEach { prefs.addRecentSearch("query$it") }

        val searches = prefs.recentSearches()

        assertEquals(8, searches.size)
        assertEquals("query9", searches.first())
        assertTrue("query1" !in searches)
    }

    @Test
    fun `removeRecentSearch drops just the matching entry, case-insensitively`() {
        prefs.addRecentSearch("batman")
        prefs.addRecentSearch("inception")

        prefs.removeRecentSearch("BATMAN")

        assertEquals(listOf("inception"), prefs.recentSearches())
    }

    @Test
    fun `clearRecentSearches empties the list`() {
        prefs.addRecentSearch("batman")

        prefs.clearRecentSearches()

        assertEquals(emptyList<String>(), prefs.recentSearches())
    }

    // --- theme mode ---

    @Test
    fun `getCurrentThemeMode defaults to follow-system`() {
        assertEquals(context.getString(tech.salroid.filmy.R.string.mode_night_follow_system), PreferenceHelper.getCurrentThemeMode(context))
    }

    @Test
    fun `setThemeMode then getCurrentThemeMode round-trips`() {
        val nightMode = context.getString(tech.salroid.filmy.R.string.mode_night_yes)

        PreferenceHelper.setThemeMode(context, nightMode)

        assertEquals(nightMode, PreferenceHelper.getCurrentThemeMode(context))
    }

    // --- account credentials ---

    @Test
    fun `accountPreferences moves credentials stored by older versions out of the default file`() {
        prefs.edit()
            .putString(PreferenceHelper.SESSION_ID, "session")
            .putString(PreferenceHelper.USER_ACCESS_TOKEN, "token")
            .putString(PreferenceHelper.ACCOUNT_OBJECT_ID, "account-object-id")
            .putString(PreferenceHelper.COUNTRY_KEY, "IN")
            .commit()

        val accountPrefs = PreferenceHelper.accountPreferences(context)

        assertEquals("session", accountPrefs.getString(PreferenceHelper.SESSION_ID, null))
        assertEquals("token", accountPrefs.getString(PreferenceHelper.USER_ACCESS_TOKEN, null))
        assertEquals("account-object-id", accountPrefs.getString(PreferenceHelper.ACCOUNT_OBJECT_ID, null))
        assertFalse(prefs.contains(PreferenceHelper.SESSION_ID))
        assertFalse(prefs.contains(PreferenceHelper.USER_ACCESS_TOKEN))
        assertFalse(prefs.contains(PreferenceHelper.ACCOUNT_OBJECT_ID))
        // Settings stay where they are.
        assertEquals("IN", prefs.getString(PreferenceHelper.COUNTRY_KEY, null))
    }

    @Test
    fun `migrateAccountKeys never overwrites a newer login with a stale legacy one`() {
        val defaultPrefs = FakeSharedPreferences(mapOf(PreferenceHelper.SESSION_ID to "stale"))
        val accountPrefs = FakeSharedPreferences(mapOf(PreferenceHelper.SESSION_ID to "current"))

        PreferenceHelper.migrateAccountKeys(defaultPrefs, accountPrefs)

        assertEquals("current", accountPrefs.getString(PreferenceHelper.SESSION_ID, null))
        assertFalse(defaultPrefs.contains(PreferenceHelper.SESSION_ID))
    }
}
