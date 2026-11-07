package tech.salroid.filmy.utility

import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException
import tech.salroid.filmy.BuildConfig

/**
 * Records a handled failure as a Crashlytics non-fatal, so errors the app
 * recovers from still show up somewhere.
 *
 * Coroutine cancellation is normal control flow and connectivity failures
 * (offline, timeouts) aren't bugs, so neither is reported.
 */
fun Throwable.reportNonFatal() {
    if (this is CancellationException) return
    if (BuildConfig.DEBUG) printStackTrace()
    if (this is IOException) return
    // Crashlytics is unavailable when Firebase isn't initialised (unit tests).
    runCatching { FirebaseCrashlytics.getInstance().recordException(this) }
}
