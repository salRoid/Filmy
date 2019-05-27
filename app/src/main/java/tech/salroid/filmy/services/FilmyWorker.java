package tech.salroid.filmy.services;

import android.app.job.JobParameters;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import tech.salroid.filmy.BuildConfig;
import tech.salroid.filmy.network_stuff.TmdbVolleySingleton;
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


public class FilmyWorker extends Worker {

    TmdbVolleySingleton tmdbVolleySingleton = TmdbVolleySingleton.getInstance();
    RequestQueue tmdbrequestQueue = tmdbVolleySingleton.getRequestQueue();


    private WorkerParameters workParameters;

    private int taskFinished;
    private String api_key = BuildConfig.TMDB_API_KEY;
    private Context context;


    public FilmyWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
        workParameters = params;
    }


    private void syncNowInTheaters() {


        final String inTheatersBaseUrl = "https://api.themoviedb.org/3/movie/now_playing?api_key="+api_key;

        JsonObjectRequest intheatresJsonObjectRequest = new JsonObjectRequest(inTheatersBaseUrl, null,
                response -> {

                    intheatresparseOutput(response.toString(), 2);

                    taskFinished++;

                    if (taskFinished==3){

                        jobFinished(jobParameters,false);
                        taskFinished = 0;
                    }

                }, error -> Log.e("webi", "Volley Error: " + error.getCause()));


        tmdbrequestQueue.add(intheatresJsonObjectRequest);

    }

    private void syncNowUpComing() {


        final String Upcoming_Base_URL = "https://api.themoviedb.org/3/movie/upcoming?api_key="+api_key;

        JsonObjectRequest UpcomingJsonObjectRequest = new JsonObjectRequest(Upcoming_Base_URL, null,
                response -> {

                    upcomingparseOutput(response.toString());
                    taskFinished++;
                    if (taskFinished==3){
                        jobFinished(workParameters,false);
                        taskFinished = 0;
                    }
                }, error -> Log.e("webi", "Volley Error: " + error.getCause()));

        tmdbrequestQueue.add(UpcomingJsonObjectRequest);

    }

    private void syncNowTrending() {


        final String BASE_URL = "https://api.themoviedb.org/3/movie/popular?api_key="+api_key;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(BASE_URL, null,
                response -> {

                    parseOutput(response.toString());
                    taskFinished++;
                    if (taskFinished==3){
                        jobFinished(workParameters,false);
                        taskFinished = 0;
                    }

                }, error -> {

                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null) {
                        sendFetchFailedMessage(networkResponse.statusCode);
                    } else {
                        sendFetchFailedMessage(00);
                    }
                }
        );

        tmdbrequestQueue.add(jsonObjectRequest);

    }


    private void intheatresparseOutput(String s, int type) {

        MainActivityParseWork pa = new MainActivityParseWork(context, s);
        pa.intheatres();

    }

    private void upcomingparseOutput(String result_upcoming) {
        MainActivityParseWork pa = new MainActivityParseWork(context, result_upcoming);
        pa.parseupcoming();
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


    @NonNull
    @Override
    public Result doWork() {

        syncNowTrending();
        syncNowInTheaters();
        syncNowUpComing();

        return Result.success();
    }
}