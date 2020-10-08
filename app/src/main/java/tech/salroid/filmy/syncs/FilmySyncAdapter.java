package tech.salroid.filmy.syncs;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import tech.salroid.filmy.BuildConfig;
import tech.salroid.filmy.FilmyApplication;
import tech.salroid.filmy.R;
import tech.salroid.filmy.network_stuff.TmdbVolleySingleton;
import tech.salroid.filmy.network_stuff.VolleySingleton;
import tech.salroid.filmy.parser.MainActivityParseWork;

/*
 * Filmy Application for Android
 * Copyright (c) 2016 Ramankit Singh (http://github.com/webianks).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


public class FilmySyncAdapter extends AbstractThreadedSyncAdapter {

    public static final int SYNC_INTERVAL = 60 * 180;

    // Interval at which to sync with the weather, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    private static final int NOTIFICATION_ID = 3004;
    private static String LOG_TAG = FilmySyncAdapter.class.getSimpleName();
    Resources resource = FilmyApplication.getContext().getResources();
    TmdbVolleySingleton tmdbVolleySingleton = TmdbVolleySingleton.getInstance();
    RequestQueue tmdbrequestQueue = tmdbVolleySingleton.getRequestQueue();

    private String api_key = BuildConfig.TMDB_API_KEY;

    public FilmySyncAdapter(Context context, boolean autoInitialize) {

        super(context, autoInitialize);

    }

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {

        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {

        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
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
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);

        }

        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {

        /*
         * Since we've created an account
         */
        FilmySyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {

        getSyncAccount(context);

    }

    @Override
    public void onPerformSync(Account account,
                              Bundle bundle,
                              String s,
                              ContentProviderClient contentProviderClient,
                              SyncResult syncResult) {

        syncNowTrending();
        syncNowInTheaters();
        syncNowUpComing();

    }

    private void syncNowInTheaters() {


        final String Intheatres_Base_URL = resource.getString(R.string.tmdb_movie_base_url) + "now_playing?" + api_key;

        JsonObjectRequest IntheatresJsonObjectRequest = new JsonObjectRequest(Intheatres_Base_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        intheatresparseOutput(response.toString(), 2);

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("webi", "Volley Error: " + error.getCause());

            }
        });


        tmdbrequestQueue.add(IntheatresJsonObjectRequest);

    }

    private void syncNowUpComing() {


        final String Upcoming_Base_URL = resource.getString(R.string.tmdb_movie_base_url) + "upcoming?" + api_key;

        JsonObjectRequest UpcomingJsonObjectRequest = new JsonObjectRequest(Upcoming_Base_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        upcomingparseOutput(response.toString());

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("webi", "Volley Error: " + error.getCause());

            }
        });

        tmdbrequestQueue.add(UpcomingJsonObjectRequest);

    }

    private void syncNowTrending() {

        final String BASE_URL = "https://api.themoviedb.org/3/movie/popular?api_key=b640f55eb6ecc47b3433cfe98d0675b1";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(BASE_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override

                    public void onResponse(JSONObject response) {
                        parseOutput(response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("webi", "Volley Error: " + error.getCause());

            }
        }
        );

        tmdbrequestQueue.add(jsonObjectRequest);

    }

    private void intheatresparseOutput(String s, int type) {
        MainActivityParseWork pa = new MainActivityParseWork(getContext(), s);
        pa.inTheatres();
    }

    private void upcomingparseOutput(String result_upcoming) {
        MainActivityParseWork pa = new MainActivityParseWork(getContext(), result_upcoming);
        pa.parseUpcoming();
    }

    private void parseOutput(String result) {

        MainActivityParseWork pa = new MainActivityParseWork(getContext(), result);
        pa.parse();
    }


}