package tech.salroid.filmy.utility

import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import tech.salroid.filmy.R
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExtensionsTest {

    // --- toReadableDate ---

    @Test
    fun `toReadableDate reformats a yyyy-MM-dd date to dd MMM yyyy`() {
        assertEquals("16 Jul 2010", "2010-07-16".toReadableDate())
    }

    @Test
    fun `toReadableDate returns an empty string unchanged`() {
        assertEquals("", "".toReadableDate())
    }

    @Test
    fun `toReadableDate returns the original string when it cannot be parsed`() {
        assertEquals("not-a-date", "not-a-date".toReadableDate())
    }

    // --- toMoneyString ---

    @Test
    fun `toMoneyString formats billions`() {
        assertEquals("$2.5B", 2_500_000_000L.toMoneyString())
    }

    @Test
    fun `toMoneyString formats millions`() {
        assertEquals("$3.2M", 3_200_000L.toMoneyString())
    }

    @Test
    fun `toMoneyString formats thousands`() {
        assertEquals("$4.0K", 4_000L.toMoneyString())
    }

    @Test
    fun `toMoneyString formats small values as a plain dollar amount`() {
        assertEquals("$500", 500L.toMoneyString())
    }

    // --- parseHtml ---

    @Test
    fun `parseHtml strips html tags and trims surrounding whitespace`() {
        assertEquals("Bold text", "  <b>Bold text</b>  ".parseHtml())
    }

    @Test
    fun `parseHtml returns an empty string unchanged`() {
        assertEquals("", "".parseHtml())
    }

    // --- toUserMessageRes ---

    @Test
    fun `toUserMessageRes maps SocketTimeoutException to a timeout message`() {
        assertEquals(R.string.error_timeout, SocketTimeoutException().toUserMessageRes())
    }

    @Test
    fun `toUserMessageRes maps a generic IOException to a no-internet message`() {
        assertEquals(R.string.error_no_internet, IOException().toUserMessageRes())
    }

    @Test
    fun `toUserMessageRes maps HttpException to a server-side message`() {
        val httpException = HttpException(Response.error<Any>(500, "".toResponseBody(null)))

        assertEquals(R.string.error_server, httpException.toUserMessageRes())
    }

    @Test
    fun `toUserMessageRes maps any other throwable to a generic message`() {
        assertEquals(R.string.error_generic, RuntimeException("oops").toUserMessageRes())
    }
}
