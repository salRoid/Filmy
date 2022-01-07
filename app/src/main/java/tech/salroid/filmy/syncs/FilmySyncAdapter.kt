package tech.salroid.filmy.syncs

import android.accounts.Account
import android.accounts.AccountManager
import android.content.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.android.volley.toolbox.JsonObjectRequest
import tech.salroid.filmy.BuildConfig
import tech.salroid.filmy.FilmyApplication
import tech.salroid.filmy.R
import tech.salroid.filmy.networking.TmdbVolleySingleton
import tech.salroid.filmy.parser.MainActivityParseWork

class FilmySyncAdapter(context: Context?, autoInitialize: Boolean) :
    AbstractThreadedSyncAdapter(context, autoInitialize) {

    private var resource = FilmyApplication.context.resources
    private var tmdbVolleySingleton = TmdbVolleySingleton.getInstance()
    private var tmdbrequestQueue = tmdbVolleySingleton.requestQueue
    private val apiKey = BuildConfig.TMDB_API_KEY

    override fun onPerformSync(
        account: Account,
        bundle: Bundle,
        s: String,
        contentProviderClient: ContentProviderClient,
        syncResult: SyncResult
    ) {
        syncNowTrending()
        syncNowInTheaters()
        syncNowUpComing()
    }

    private fun syncNowInTheaters() {
        val baseUrl = resource.getString(R.string.tmdb_movie_base_url) + "now_playing?" + apiKey
        val jsonRequest = JsonObjectRequest(baseUrl, null,
            { response -> inTheatresParseOutput(response.toString(), 2) }) { error ->
            Log.e(
                "webi",
                "Volley Error: " + error.cause
            )
        }
        tmdbrequestQueue.add(jsonRequest)
    }

    private fun syncNowUpComing() {
        val baseUrl =
            resource.getString(R.string.tmdb_movie_base_url) + "upcoming?" + apiKey
        val jsonRequest = JsonObjectRequest(baseUrl, null,
            { response -> upComingParseOutput(response.toString()) }) { error ->
            Log.e(
                "webi",
                "Volley Error: " + error.cause
            )
        }
        tmdbrequestQueue.add(jsonRequest)
    }

    private fun syncNowTrending() {
        val BASE_URL =
            "https://api.themoviedb.org/3/movie/popular?api_key=b640f55eb6ecc47b3433cfe98d0675b1"
        val jsonObjectRequest = JsonObjectRequest(BASE_URL, null,
            { response -> parseOutput(response.toString()) }
        ) { error -> Log.e("webi", "Volley Error: " + error.cause) }
        tmdbrequestQueue.add(jsonObjectRequest)
    }

    private fun inTheatresParseOutput(s: String, type: Int) {
        val pa = MainActivityParseWork(context, s)
        pa.inTheatres()
    }

    private fun upComingParseOutput(result_upcoming: String) {
        val pa = MainActivityParseWork(context, result_upcoming)
        pa.parseUpcoming()
    }

    private fun parseOutput(result: String) {
        val pa = MainActivityParseWork(context, result)
        pa.parse()
    }

    companion object {
        const val SYNC_INTERVAL = 60 * 180

        // Interval at which to sync with the weather, in seconds.
        // 60 seconds (1 minute) * 180 = 3 hours
        const val SYNC_FLEXTIME = SYNC_INTERVAL / 3
        private const val DAY_IN_MILLIS = (1000 * 60 * 60 * 24).toLong()
        private const val NOTIFICATION_ID = 3004
        private val LOG_TAG = FilmySyncAdapter::class.java.simpleName

        /**
         * Helper method to have the sync adapter sync immediately
         *
         * @param context The context used to access the account service
         */
        private fun syncImmediately(context: Context) {
            val bundle = Bundle()
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true)
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true)
            ContentResolver.requestSync(
                getSyncAccount(context),
                context.getString(R.string.content_authority), bundle
            )
        }

        /**
         * Helper method to schedule the sync adapter periodic execution
         */
        fun configurePeriodicSync(context: Context, syncInterval: Int, flexTime: Int) {
            val account = getSyncAccount(context)
            val authority = context.getString(R.string.content_authority)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                // we can enable inexact timers in our periodic sync
                val request =
                    SyncRequest.Builder().syncPeriodic(syncInterval.toLong(), flexTime.toLong())
                        .setSyncAdapter(account, authority).setExtras(Bundle()).build()
                ContentResolver.requestSync(request)
            } else {
                ContentResolver.addPeriodicSync(
                    account,
                    authority, Bundle(), syncInterval.toLong()
                )
            }
        }

        /**
         * Helper method to get the fake account to be used with SyncAdapter, or make a new one
         * if the fake account doesn't exist yet.  If we make a new account, we call the
         * onAccountCreated method so we can initialize things.
         *
         * @param context The context used to access the account service
         * @return a fake account.
         */
        fun getSyncAccount(context: Context): Account? {
            // Get an instance of the Android account manager
            val accountManager = context.getSystemService(Context.ACCOUNT_SERVICE) as AccountManager

            // Create the account type and default account
            val newAccount = Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type)
            )

            // If the password doesn't exist, the account doesn't exist
            if (null == accountManager.getPassword(newAccount)) {

                /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
                if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                    return null
                }
                /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */onAccountCreated(newAccount, context)
            }
            return newAccount
        }

        private fun onAccountCreated(newAccount: Account, context: Context) {

            /*
         * Since we've created an account
         */
            configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME)

            /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */ContentResolver.setSyncAutomatically(
                newAccount,
                context.getString(R.string.content_authority),
                true
            )

            /*
         * Finally, let's do a sync to get things started
         */syncImmediately(context)
        }

        fun initializeSyncAdapter(context: Context) {
            getSyncAccount(context)
        }
    }
}