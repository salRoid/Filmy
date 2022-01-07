package tech.salroid.filmy.syncs

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 * The service which allows the sync adapter framework to access the authenticator.
 */
class FilmyAuthenticatorService : Service() {

    // Instance field that stores the authenticator object
    private var mAuthenticator: FilmyAuthenticator? = null

    override fun onCreate() {
        // Create a new authenticator object
        mAuthenticator = FilmyAuthenticator(this)
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    override fun onBind(intent: Intent): IBinder? {
        return mAuthenticator!!.iBinder
    }
}