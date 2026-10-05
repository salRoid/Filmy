package tech.salroid.filmy.utility

import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException
import org.junit.After
import org.junit.Before
import org.junit.Test

class CrashReportingTest {

    private val crashlytics = mockk<FirebaseCrashlytics>(relaxed = true)

    @Before
    fun setUp() {
        mockkStatic(FirebaseCrashlytics::class)
        every { FirebaseCrashlytics.getInstance() } returns crashlytics
    }

    @After
    fun tearDown() {
        unmockkStatic(FirebaseCrashlytics::class)
    }

    @Test
    fun `reportNonFatal records an unexpected failure`() {
        val failure = IllegalStateException("boom")

        failure.reportNonFatal()

        verify(exactly = 1) { crashlytics.recordException(failure) }
    }

    @Test
    fun `reportNonFatal ignores coroutine cancellation`() {
        CancellationException("cancelled").reportNonFatal()

        verify(exactly = 0) { crashlytics.recordException(any()) }
    }

    @Test
    fun `reportNonFatal ignores connectivity failures`() {
        IOException("offline").reportNonFatal()

        verify(exactly = 0) { crashlytics.recordException(any()) }
    }

    @Test
    fun `reportNonFatal does not throw when Crashlytics is unavailable`() {
        every { FirebaseCrashlytics.getInstance() } throws IllegalStateException("no FirebaseApp")

        IllegalStateException("boom").reportNonFatal()
    }
}
