package tech.salroid.filmy.network_stuff;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONObject;
import tech.salroid.filmy.BuildConfig;
import tech.salroid.filmy.parser.MainActivityParseWork;
import tech.salroid.filmy.services.FilmyJobScheduler;

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
    TmdbVolleySingleton tmdbVolleySingleton = TmdbVolleySingleton.getInstance();
    RequestQueue tmdbrequestQueue = tmdbVolleySingleton.getRequestQueue();

    public FirstFetch(Context context){
        this.context = context;
    }

    public void start(){

        syncNowTrending();
        syncNowInTheaters();
        syncNowUpComing();

        FilmyJobScheduler filmyJobScheduler = new FilmyJobScheduler(context);
        filmyJobScheduler.createJob();
    }


    private void syncNowInTheaters() {


        String api_key = BuildConfig.API_KEY;

        final String Intheatres_Base_URL = "https://api.themoviedb.org/3/movie/now_playing?api_key="+api_key;

        JsonObjectRequest IntheatresJsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Intheatres_Base_URL, null,
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


        String api_key = BuildConfig.API_KEY;
        final String Upcoming_Base_URL = "https://api.themoviedb.org/3/movie/upcoming?api_key="+api_key;

        JsonObjectRequest UpcomingJsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Upcoming_Base_URL, null,
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

        String api_key = BuildConfig.API_KEY;
        final String BASE_URL = "https://api.themoviedb.org/3/movie/popular?api_key="+api_key;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, BASE_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override

                    public void onResponse(JSONObject response) {

                        parseOutput(response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    sendFetchFailedMessage(networkResponse.statusCode);
                } else {

                    sendFetchFailedMessage(00);

                }

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


}
