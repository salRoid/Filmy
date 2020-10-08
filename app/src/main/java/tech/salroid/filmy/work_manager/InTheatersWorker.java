package tech.salroid.filmy.work_manager;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.common.util.concurrent.ListenableFuture;

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


public class InTheatersWorker extends ListenableWorker {

    private TmdbVolleySingleton tmdbVolleySingleton = TmdbVolleySingleton.getInstance();
    private RequestQueue tmdbRequestQueue = tmdbVolleySingleton.getRequestQueue();
    private WorkerParameters workParameters;
    private int taskFinished;
    private String api_key = BuildConfig.TMDB_API_KEY;
    private Context context;


    public InTheatersWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
        workParameters = params;
    }

    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {

        //syncNowTrending();
        syncNowInTheaters();
        //syncNowUpComing();

        return null;
    }


    private void syncNowInTheaters() {

        final String inTheatersBaseUrl = "https://api.themoviedb.org/3/movie/now_playing?api_key=" + api_key;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(inTheatersBaseUrl, null,
                response -> {
                    inTheatresParseOutput(response.toString(), 2);
                    taskFinished++;
                    if (taskFinished == 3) {
                        //jobFinished(jobParameters, false);
                        taskFinished = 0;
                    }
                }, error -> Log.e("webi", "Volley Error: " + error.getCause()));

        tmdbRequestQueue.add(jsonObjectRequest);

    }

    private void inTheatresParseOutput(String s, int type) {
        MainActivityParseWork pa = new MainActivityParseWork(context, s);
        pa.inTheatres();
    }



}