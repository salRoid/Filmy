package tech.salroid.filmy.Sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by R Ankit on 21-07-2016.
 */

public class FilmySyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();
    private static FilmySyncAdapter sFilmySyncAdapter = null;

    @Override
    public void onCreate() {
        // Log.d("FilmySyncService", "FilmySyncService");
        synchronized (sSyncAdapterLock) {
            if (sFilmySyncAdapter == null) {
                sFilmySyncAdapter = new FilmySyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sFilmySyncAdapter.getSyncAdapterBinder();
    }
}
