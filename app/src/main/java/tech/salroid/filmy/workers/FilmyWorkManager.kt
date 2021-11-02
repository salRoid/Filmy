package tech.salroid.filmy.workers

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

class FilmyWorkManager(private val context: Context) {

    fun createWork() {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()

        val workRequestTrending = PeriodicWorkRequest.Builder(
            TrendingWorker::class.java, SYNC_INTERVAL,
            TimeUnit.MILLISECONDS
        )
            .setConstraints(constraints)
            .build()

        val workRequestInTheaters = PeriodicWorkRequest.Builder(
            InTheatersWorker::class.java, SYNC_INTERVAL,
            TimeUnit.MILLISECONDS
        )
            .setConstraints(constraints)
            .build()

        val workRequestUpcoming = PeriodicWorkRequest.Builder(
            UpcomingWorker::class.java, SYNC_INTERVAL,
            TimeUnit.MILLISECONDS
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance().enqueueUniquePeriodicWork(
            "filmy-trending-updates",
            ExistingPeriodicWorkPolicy.KEEP, workRequestTrending
        )

        WorkManager.getInstance().enqueueUniquePeriodicWork(
            "filmy-intheaters-updates",
            ExistingPeriodicWorkPolicy.KEEP, workRequestInTheaters
        )

        WorkManager.getInstance().enqueueUniquePeriodicWork(
            "filmy-upcoming-updates",
            ExistingPeriodicWorkPolicy.KEEP, workRequestUpcoming
        )
    }

    companion object {
        private const val SYNC_INTERVAL: Long = 21600000
    }
}