package tech.salroid.filmy.network_stuff;

import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import tech.salroid.filmy.BuildConfig;
import tech.salroid.filmy.FilmyApplication;
import tech.salroid.filmy.R;

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
public class GetDataFromNetwork {

    public static final int MOVIE_DETAILS_CODE = 1;
    public static final int CAST_CODE = 2;
    private DataFetchedListener mDataFetchedListener;

    public void getMovieDetailsFromNetwork(String movie_id) {

        VolleySingleton volleySingleton = VolleySingleton.getInstance();
        RequestQueue requestQueue = volleySingleton.getRequestQueue();


        //still here we will use the query builder
        //this String is not awesome.
        String api_key = BuildConfig.TMDB_API_KEY;

        final String BASE_URL_MOVIE_DETAILS = new String(FilmyApplication.getContext().getResources().getString(R.string.tmdb_movie_base_url)
                + movie_id
                + "?"
                + "api_key="+api_key
                + "&append_to_response=trailers");


        JsonObjectRequest jsonObjectRequestForMovieDetails = new JsonObjectRequest(BASE_URL_MOVIE_DETAILS, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        mDataFetchedListener.dataFetched(response.toString(), MOVIE_DETAILS_CODE);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("webi", "Volley Error: " + error.getCause());

            }
        }
        );

        requestQueue.add(jsonObjectRequestForMovieDetails);


    }

    public void setDataFetchedListener(DataFetchedListener dataFetchedListener) {

        this.mDataFetchedListener = dataFetchedListener;

    }


    public interface DataFetchedListener {

        void dataFetched(String response, int type);

    }

}