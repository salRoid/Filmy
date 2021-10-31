package tech.salroid.filmy.activities;

import android.content.Context;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tech.salroid.filmy.BuildConfig;
import tech.salroid.filmy.network_stuff.VolleySingleton;

/*
 * Filmy Application for Android
 * Copyright (c) 2016 Sajal Gupta (http://github.com/salroid).
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

class Rating {

    private static String imdb_rating = "0", tomatometer_rating = "0", audience_rating = "0", metascore_rating = "0", image = null, rottenTomatoPage = null;
    private static String OMDB_API_KEY = BuildConfig.OMDB_API_KEY;

    static void getRating(final Context context, String movie_id_final) {

        VolleySingleton volleySingleton = VolleySingleton.getInstance();
        RequestQueue requestQueue = volleySingleton.getRequestQueue();


        String BASE_RATING_URL = "http://www.omdbapi.com/?i=" + movie_id_final + "&apikey=" + OMDB_API_KEY + "&tomatoes=true&r=json";

        JsonObjectRequest jsonObjectRequestForMovieDetails = new JsonObjectRequest(BASE_RATING_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            Boolean responseBool = response.getBoolean("Response");

                            if (responseBool){

                                imdb_rating = response.getString("imdbRating");
                                tomatometer_rating = response.getString("tomatoRating");
                                audience_rating = response.getString("tomatoUserRating");
                                metascore_rating = response.getString("Metascore");
                                image = response.getString("tomatoImage");
                                rottenTomatoPage = response.getString("tomatoURL");


                                //Above tomatometer does not work this does
                                JSONArray jsonArray = response.getJSONArray("Ratings");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    if (jsonArray.getJSONObject(i).getString("Source").equals("Rotten Tomatoes")) {
                                        tomatometer_rating = jsonArray.getJSONObject(i).getString("Value");
                                    }
                                }

                                setRatingCallback(context, imdb_rating, tomatometer_rating, audience_rating, metascore_rating, rottenTomatoPage);

                            }else
                                setRatingFailCallback(context);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            setRatingFailCallback(context);
                        }

                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // Log.e("webi", "Volley Error: " + error.getCause());
                setRatingFailCallback(context);
            }
        }
        );

        requestQueue.add(jsonObjectRequestForMovieDetails);

    }

    private static void setRatingFailCallback(Context context) {
        ((MovieDetailsActivity) context).setRatingGone();
    }


    private static void setRatingCallback(Context context, String imdb_rating, String tomatometer_rating, String audience_rating, String metascore_rating, String rottenTomatoPage) {
        ((MovieDetailsActivity) context).setRating(imdb_rating, tomatometer_rating, audience_rating, metascore_rating, rottenTomatoPage );
    }

}
