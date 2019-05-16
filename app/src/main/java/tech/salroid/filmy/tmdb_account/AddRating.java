package tech.salroid.filmy.tmdb_account;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import tech.salroid.filmy.BuildConfig;
import tech.salroid.filmy.R;
import tech.salroid.filmy.customs.CustomToast;
import tech.salroid.filmy.network_stuff.TmdbVolleySingleton;

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
public class AddRating {

    private String api_key = BuildConfig.TMDB_API_KEY;
    private String SESSION_PREF = "SESSION_PREFERENCE";
    private TmdbVolleySingleton tmdbVolleySingleton = TmdbVolleySingleton.getInstance();
    private RequestQueue tmdbrequestQueue = tmdbVolleySingleton.getRequestQueue();
    private Context context;

    private int NOTIFICATION_ID;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;


    public void addRating(Context context, String media_id,int rating) {

        this.context = context;
        SharedPreferences sp = context.getSharedPreferences(SESSION_PREF, Context.MODE_PRIVATE);
        String session_id = sp.getString("session", " ");

        //issue notification to show indeterminate progress

        Random random = new Random();
        NOTIFICATION_ID = random.nextInt(10000);

        mNotifyManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle("Filmy")
                .setContentText("Adding to ratings..")
                .setSmallIcon(R.drawable.ic_stat_status);
        mBuilder.setProgress(0, 0, true);
        mNotifyManager.notify(NOTIFICATION_ID, mBuilder.build());

        getProfile(session_id, media_id,rating);
    }


    private void getProfile(final String session_id, final String media_id, final int rating) {

        String PROFILE_URI = "https://api.themoviedb.org/3/account?api_key=" + api_key + "&session_id=" + session_id;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(PROFILE_URI, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        parseOutput(response, session_id, Integer.valueOf(media_id),rating);
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("webi", "Volley Error: " + error.getCause());

            }
        });

        tmdbrequestQueue.add(jsonObjectRequest);
    }

    private void parseOutput(JSONObject response, String session_id, int media_id,int rating) {

        try {

            String account_id = response.getString("id");
            if (account_id != null) {

                String query = "https://api.themoviedb.org/3/movie/" + media_id + "/rating?api_key=" + api_key + "&session_id=" + session_id;

                add(query,rating);


            } else {
                CustomToast.show(context, "You are not logged in. Please login from account.", false);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            CustomToast.show(context, "You are not logged in. Please login from account.", false);
        }
    }

    private void add(String query,int rating) {

        JSONObject jsonBody = new JSONObject();
        try {

            jsonBody.put("value", rating);

        } catch (JSONException e) {

            e.printStackTrace();
        }
        final String mRequestBody = jsonBody.toString();


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(query, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        parseMarkedResponse(response);
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("webi", "Volley Error: " + error.getCause());

            }
        }) {

            @Override
            public byte[] getBody() {
                try {
                    return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                    return null;
                }
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("content-type", "application/json;charset=utf-8");
                return headers;
            }
        };

        tmdbrequestQueue.add(jsonObjectRequest);

    }


    private void parseMarkedResponse(JSONObject response) {


        try {
            int status_code = response.getInt("status_code");

            if (status_code == 1) {

                CustomToast.show(context, "Your rating has been added.", false);

                mBuilder.setContentText("Movie rating done.")
                        // Removes the progress bar
                        .setProgress(0, 0, false);
                mNotifyManager.notify(NOTIFICATION_ID, mBuilder.build());
                mNotifyManager.cancel(NOTIFICATION_ID);

            } else if (status_code == 12) {

                CustomToast.show(context, "Rating updated.", false);

                mBuilder.setContentText("Rating updated.")
                        // Removes the progress bar
                        .setProgress(0, 0, false);
                mNotifyManager.notify(NOTIFICATION_ID, mBuilder.build());
                mNotifyManager.cancel(NOTIFICATION_ID);
            }

        } catch (JSONException e) {
            Log.d("webi", e.getCause().toString());

            mBuilder.setContentText("Can't add rating.")
                    // Removes the progress bar
                    .setProgress(0, 0, false);
            mNotifyManager.notify(NOTIFICATION_ID, mBuilder.build());
            mNotifyManager.cancel(NOTIFICATION_ID);

            CustomToast.show(context, "Can't add rating.", false);
        }

    }


}
