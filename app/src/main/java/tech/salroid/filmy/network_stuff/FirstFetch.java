package tech.salroid.filmy.network_stuff;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;

import tech.salroid.filmy.BuildConfig;
import tech.salroid.filmy.parser.MainActivityParseWork;
import tech.salroid.filmy.work_manager.FilmyWorkManager;

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

public class FirstFetch {


    private Context context;
    private TmdbVolleySingleton tmdbVolleySingleton = TmdbVolleySingleton.getInstance();
    private RequestQueue tmdbRequestQueue = tmdbVolleySingleton.getRequestQueue();

    public FirstFetch(Context context){
        this.context = context;
    }

    public void start(){

        syncNowTrending();
        syncNowInTheaters();
        syncNowUpComing();

        FilmyWorkManager workManager = new FilmyWorkManager(context);
        workManager.createWork();
    }


    private void syncNowInTheaters() {

        String api_key = BuildConfig.TMDB_API_KEY;
        final String inTheatresBaseUrl = "https://api.themoviedb.org/3/movie/now_playing?api_key="+api_key;
        JsonObjectRequest inTheatresJsonObjectRequest = new JsonObjectRequest(inTheatresBaseUrl, null,
                response -> inTheatresParseOutput(response.toString(), 2), error -> Log.e("webi", "Volley Error: " + error.getCause()));

        tmdbRequestQueue.add(inTheatresJsonObjectRequest);

    }

    private void syncNowUpComing() {


        String api_key = BuildConfig.TMDB_API_KEY;
        final String Upcoming_Base_URL = "https://api.themoviedb.org/3/movie/upcoming?api_key="+api_key;

        JsonObjectRequest UpcomingJsonObjectRequest = new JsonObjectRequest(Upcoming_Base_URL, null,
                response -> upcomingParseOutput(response.toString()), error -> Log.e("webi", "Volley Error: " + error.getCause()));

        tmdbRequestQueue.add(UpcomingJsonObjectRequest);

    }

    private void syncNowTrending() {

        String api_key = BuildConfig.TMDB_API_KEY;
        final String BASE_URL = "https://api.themoviedb.org/3/movie/popular?api_key="+api_key;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(BASE_URL, null,
                response -> parseOutput(response.toString()), error -> {

                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null) {
                        sendFetchFailedMessage(networkResponse.statusCode);
                    } else {
                        sendFetchFailedMessage(00);
                    }
                }
        );

        tmdbRequestQueue.add(jsonObjectRequest);

    }

    private void inTheatresParseOutput(String s, int type) {

        MainActivityParseWork pa = new MainActivityParseWork(context, s);
        pa.inTheatres();

    }

    private void upcomingParseOutput(String result_upcoming) {
        MainActivityParseWork pa = new MainActivityParseWork(context, result_upcoming);
        pa.parseUpcoming();
    }


    private void parseOutput(String result) {

        MainActivityParseWork pa = new MainActivityParseWork(context, result);
        pa.parse();
    }

    private void sendFetchFailedMessage(int message) {

        Intent intent = new Intent("fetch-failed");
        intent.putExtra("message", message);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

    }


}
