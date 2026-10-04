package tech.salroid.filmy.data.network

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Pins the TMDB v4 list calls to the request shapes and response fields the
 * API actually uses, without touching the network.
 */
class AccountListsApiTest {

    private var lastRequest: Request? = null
    private var nextCode = 200
    private var nextBody = "{}"

    private val helper: AccountApiHelper = AccountApiHelperImpl(
        Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .client(
                OkHttpClient.Builder().addInterceptor(Interceptor { chain ->
                    lastRequest = chain.request()
                    Response.Builder()
                        .request(chain.request())
                        .protocol(Protocol.HTTP_1_1)
                        .code(nextCode)
                        .message("")
                        .body(nextBody.toResponseBody("application/json".toMediaType()))
                        .build()
                }).build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AccountApiService::class.java)
    )

    private fun respond(body: String, code: Int = 200) {
        nextBody = body
        nextCode = code
    }

    private fun Request.bodyText(): String = Buffer().also { body!!.writeTo(it) }.readUtf8()

    @Test
    fun `adding a show posts its media type to the v4 list with the user's token`() = runBlocking {
        respond("""{"success":true,"results":[{"media_id":1396,"media_type":"tv","success":true}]}""")

        val response = helper.addToList("user-token", listId = 7, mediaId = 1396, isTv = true).first()

        val request = lastRequest!!
        assertEquals("POST", request.method)
        assertEquals("https://api.themoviedb.org/4/list/7/items", request.url.toString())
        assertEquals("Bearer user-token", request.header("Authorization"))
        assertEquals("""{"items":[{"media_type":"tv","media_id":1396}]}""", request.bodyText())
        assertTrue(response.allSucceeded)
    }

    @Test
    fun `removing a movie sends a DELETE with a body`() = runBlocking {
        respond("""{"success":true,"results":[{"media_id":550,"media_type":"movie","success":true}]}""")

        helper.removeFromList("user-token", listId = 7, mediaId = 550, isTv = false).first()

        val request = lastRequest!!
        assertEquals("DELETE", request.method)
        assertEquals("https://api.themoviedb.org/4/list/7/items", request.url.toString())
        assertEquals("""{"items":[{"media_type":"movie","media_id":550}]}""", request.bodyText())
    }

    @Test
    fun `an item TMDB rejects is reported as not succeeded despite the 200`() = runBlocking {
        respond("""{"success":true,"results":[{"media_id":550,"media_type":"movie","success":false}]}""")

        assertFalse(helper.addToList("user-token", 7, 550, isTv = false).first().allSucceeded)
    }

    @Test
    fun `membership is true on success and false on TMDB's 404`() = runBlocking {
        respond("""{"success":true,"status_code":1,"media_id":1396,"media_type":"tv"}""")
        assertTrue(helper.isInList("user-token", 7, 1396, isTv = true).first())
        assertEquals(
            "https://api.themoviedb.org/4/list/7/item_status?media_id=1396&media_type=tv",
            lastRequest!!.url.toString()
        )

        respond("""{"success":false,"status_code":34}""", code = 404)
        assertFalse(helper.isInList("user-token", 7, 1396, isTv = true).first())
    }

    @Test
    fun `list details parse mixed movie and show entries`() = runBlocking {
        respond(
            """{"id":7,"name":"Mix","total_pages":3,"results":[
                {"id":550,"media_type":"movie","title":"Fight Club","release_date":"1999-10-15","poster_path":"/a.jpg"},
                {"id":1396,"media_type":"tv","name":"Breaking Bad","first_air_date":"2008-01-20","poster_path":"/b.jpg"}
            ]}"""
        )

        val details = helper.getListDetails("user-token", listId = 7, page = 2).first()

        assertEquals("https://api.themoviedb.org/4/list/7?page=2", lastRequest!!.url.toString())
        assertEquals(3, details.totalPages)
        assertEquals(listOf("Fight Club" to false, "Breaking Bad" to true), details.items.map { it.displayTitle to it.isTv })
        assertEquals("2008-01-20", details.items[1].displayDate)
    }

    @Test
    fun `account lists are read from the v4 account and expose their item counts`() = runBlocking {
        respond("""{"page":1,"results":[{"id":7,"name":"Mix","number_of_items":12}],"total_pages":1}""")

        val lists = helper.getLists("user-token", accountObjectId = "abc123", page = 1).first()

        assertEquals("https://api.themoviedb.org/4/account/abc123/lists?page=1", lastRequest!!.url.toString())
        assertEquals(12, lists.results.single().itemCount)
    }

    @Test
    fun `creating a list sends its language and reads back the new id`() = runBlocking {
        respond("""{"success":true,"id":99,"status_code":1}""")

        val created = helper.createList("user-token", name = "Weekend", description = "").first()

        val request = lastRequest!!
        assertEquals("https://api.themoviedb.org/4/list", request.url.toString())
        assertEquals("""{"name":"Weekend","description":"","iso_639_1":"en"}""", request.bodyText())
        assertEquals(99, created.listId)
    }

    @Test
    fun `revoking the user's token is a DELETE carrying the token`() = runBlocking {
        respond("""{"success":true,"status_code":1}""")

        helper.revokeAccessToken("user-token").first()

        val request = lastRequest!!
        assertEquals("DELETE", request.method)
        assertEquals("https://api.themoviedb.org/3/auth/access_token", request.url.toString())
        assertEquals("""{"access_token":"user-token"}""", request.bodyText())
    }
}
