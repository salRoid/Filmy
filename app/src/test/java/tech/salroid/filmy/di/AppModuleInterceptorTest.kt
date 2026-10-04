package tech.salroid.filmy.di

import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import tech.salroid.filmy.FakeSharedPreferences
import tech.salroid.filmy.utility.PreferenceHelper
import tech.salroid.filmy.utility.resolveImageLanguages

/**
 * Exercises AppModule.provideOkhttpClient's interceptor chain end-to-end without
 * a live server: a capturing interceptor is appended after the real chain and
 * short-circuits with a synthetic response instead of calling chain.proceed(),
 * so no network dispatch ever happens.
 */
class AppModuleInterceptorTest {

    private lateinit var capturedRequest: Request

    private fun executeAndCapture(
        url: String,
        prefs: FakeSharedPreferences = FakeSharedPreferences(),
        userAuthorization: String? = null
    ) {
        val client = AppModule.provideOkhttpClient(prefs)
            .newBuilder()
            .addInterceptor { chain ->
                capturedRequest = chain.request()
                Response.Builder()
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_1)
                    .code(200)
                    .message("OK")
                    .build()
            }
            .build()

        val request = Request.Builder().url(url)
        userAuthorization?.let { request.header("Authorization", it) }
        client.newCall(request.build()).execute().close()
    }

    @Test
    fun `every request gets json content-type and a bearer auth header`() {
        executeAndCapture("https://api.themoviedb.org/3/movie/popular")

        assertEquals("application/json", capturedRequest.header("content-type"))
        assertTrue(capturedRequest.header("authorization")?.startsWith("Bearer ") == true)
    }

    @Test
    fun `a non-auth path is left on the v3 API`() {
        executeAndCapture("https://api.themoviedb.org/3/movie/popular")

        assertTrue(capturedRequest.url.encodedPath.startsWith("/3/"))
    }

    @Test
    fun `an auth path is rewritten from v3 to v4`() {
        executeAndCapture("https://api.themoviedb.org/3/auth/request_token")

        assertTrue(capturedRequest.url.encodedPath.startsWith("/4/"))
        assertEquals("/4/auth/request_token", capturedRequest.url.encodedPath)
    }

    @Test
    fun `region and language are appended for TMDB requests`() {
        executeAndCapture("https://api.themoviedb.org/3/movie/popular")

        assertTrue(capturedRequest.url.queryParameter("region") != null)
        assertTrue(capturedRequest.url.queryParameter("language") != null)
    }

    @Test
    fun `the stored region preference is used when set`() {
        val prefs = FakeSharedPreferences()
        prefs.edit().putString(PreferenceHelper.COUNTRY_KEY, "IN").apply()

        executeAndCapture("https://api.themoviedb.org/3/movie/popular", prefs)

        assertEquals("IN", capturedRequest.url.queryParameter("region"))
    }

    @Test
    fun `a call made with the user's own token keeps it and gets no app token`() {
        executeAndCapture("https://api.themoviedb.org/4/list/7", userAuthorization = "Bearer user-token")

        assertEquals(listOf("Bearer user-token"), capturedRequest.headers("Authorization"))
    }

    @Test
    fun `a region already on the request is replaced, not duplicated`() {
        val prefs = FakeSharedPreferences()
        prefs.edit().putString(PreferenceHelper.COUNTRY_KEY, "IN").apply()

        executeAndCapture("https://api.themoviedb.org/3/movie/popular?region=US&language=xx", prefs)

        assertEquals(listOf("IN"), capturedRequest.url.queryParameterValues("region"))
        assertEquals(1, capturedRequest.url.queryParameterValues("language").size)
    }

    @Test
    fun `image requests also ask for English and untagged images`() {
        executeAndCapture("https://api.themoviedb.org/3/movie/550/images")

        val language = capturedRequest.url.queryParameter("language")!!
        assertEquals(
            resolveImageLanguages(language),
            capturedRequest.url.queryParameter("include_image_language")
        )
    }

    @Test
    fun `other requests carry no image language filter`() {
        executeAndCapture("https://api.themoviedb.org/3/movie/550")

        assertNull(capturedRequest.url.queryParameter("include_image_language"))
    }

    @Test
    fun `region and language are not appended for a non-TMDB host`() {
        executeAndCapture("https://www.omdbapi.com/?i=tt1")

        assertNull(capturedRequest.url.queryParameter("region"))
        assertNull(capturedRequest.url.queryParameter("language"))
    }
}
