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


public class UpcomingWorker extends ListenableWorker {

    private TmdbVolleySingleton tmdbVolleySingleton = TmdbVolleySingleton.getInstance();
    private RequestQueue tmdbRequestQueue = tmdbVolleySingleton.getRequestQueue();
    private WorkerParameters workParameters;
    private int taskFinished;
    private String api_key = BuildConfig.TMDB_API_KEY;
    private Context context;


    public UpcomingWorker(
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
        //syncNowInTheaters();
        syncNowUpComing();

        return null;
    }


   private void syncNowUpComing() {


        final String Upcoming_Base_URL = "https://api.themoviedb.org/3/movie/upcoming?api_key=" + api_key;

        JsonObjectRequest UpcomingJsonObjectRequest = new JsonObjectRequest(Upcoming_Base_URL, null,
                response -> {
                    upComingParseOutput(response.toString());
                    taskFinished++;
                    if (taskFinished == 3) {
                        //jobFinished(workParameters, false);
                        taskFinished = 0;
                    }
                }, error -> Log.e("webi", "Volley Error: " + error.getCause()));
        tmdbRequestQueue.add(UpcomingJsonObjectRequest);

    }

  private void upComingParseOutput(String result_upcoming) {
        MainActivityParseWork pa = new MainActivityParseWork(context, result_upcoming);
        pa.parseUpcoming();
    }
}