package tech.salroid.filmy.services;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import me.tatarka.support.job.JobParameters;
import me.tatarka.support.job.JobService;
import tech.salroid.filmy.network_stuff.TmdbVolleySingleton;
import tech.salroid.filmy.network_stuff.VolleySingleton;
import tech.salroid.filmy.parser.MainActivityParseWork;

/**
 * Created by R Ankit on 05-08-2016.
 */

public class FilmyJobService extends JobService {


    TmdbVolleySingleton tmdbVolleySingleton = TmdbVolleySingleton.getInstance();
    RequestQueue tmdbrequestQueue = tmdbVolleySingleton.getRequestQueue();
    private JobParameters jobParameters;

    private int taskFinished;


    @Override
    public boolean onStartJob(JobParameters params) {

        jobParameters = params;

        syncNowTrending();
        syncNowInTheaters();
        syncNowUpComing();

        return true;
    }


    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }


    private void syncNowInTheaters() {


        final String Intheatres_Base_URL = "https://api.themoviedb.org/3/movie/now_playing?api_key=b640f55eb6ecc47b3433cfe98d0675b1";

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


        final String Upcoming_Base_URL = "https://api.themoviedb.org/3/movie/upcoming?api_key=b640f55eb6ecc47b3433cfe98d0675b1";

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

        VolleySingleton volleySingleton = VolleySingleton.getInstance();
        RequestQueue requestQueue = volleySingleton.getRequestQueue();

        final String BASE_URL = "https://api.trakt.tv/movies/trending?extended=images,page=1&limit=30";

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, BASE_URL, null,
                new Response.Listener<JSONArray>() {
                    @Override

                    public void onResponse(JSONArray response) {

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

        requestQueue.add(jsonObjectRequest);

    }


    private void intheatresparseOutput(String s, int type) {

        MainActivityParseWork pa = new MainActivityParseWork(this, s);
        pa.intheatres();

    }

    private void upcomingparseOutput(String result_upcoming) {
        MainActivityParseWork pa = new MainActivityParseWork(this, result_upcoming);
        pa.parseupcoming();
    }


    private void parseOutput(String result) {

        MainActivityParseWork pa = new MainActivityParseWork(this, result);
        pa.parse();
    }

    private void sendFetchFailedMessage(int message) {

        Intent intent = new Intent("fetch-failed");
        intent.putExtra("message", message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }


}