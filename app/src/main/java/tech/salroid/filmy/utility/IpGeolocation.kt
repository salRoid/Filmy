package tech.salroid.filmy.utility

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

/**
 * Best-effort IP-based country detection for onboarding's default country
 * guess. Deliberately uses its own bare [OkHttpClient] rather than the app's
 * TMDB-scoped Retrofit client, since that one's interceptors unconditionally
 * attach a TMDB bearer token to every request - not something to send to an
 * unrelated third-party service.
 *
 * Uses ipwho.is (keyless, no rate-limit wall observed) rather than ipapi.co -
 * the latter now sits behind Cloudflare bot-protection that blocks plain
 * HTTP clients (403) and separately rate-limits its JSON endpoint (429), so
 * it silently failed every call and this always fell back to
 * Locale.getDefault().country instead of an actual IP-based lookup.
 */
object IpGeolocation {
    private val client = OkHttpClient()

    suspend fun fetchCountryCode(): String? = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("https://ipwho.is/")
                .build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext null
                val body = response.body?.string() ?: return@withContext null
                val json = JSONObject(body)
                if (!json.optBoolean("success", false)) return@withContext null
                val code = json.optString("country_code").trim().uppercase()
                code.takeIf { it.length == 2 && it.all(Char::isLetter) }
            }
        } catch (e: Exception) {
            null
        }
    }
}
