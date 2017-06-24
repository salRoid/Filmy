package tech.salroid.filmy.tmdb_account;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

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

import tech.salroid.filmy.BuildConfig;
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
public class UnMarkingFavorite {

    private String api_key = BuildConfig.TMDB_API_KEY;
    private String SESSION_PREF = "SESSION_PREFERENCE";
    private TmdbVolleySingleton tmdbVolleySingleton = TmdbVolleySingleton.getInstance();
    private RequestQueue tmdbrequestQueue = tmdbVolleySingleton.getRequestQueue();
    private Context context;
    private UnmarkedListener listener;
    private int position;

    public void unmarkThisAsFavorite(Context context,String media_id,int position){

        this.context = context;
        this.position = position;
        SharedPreferences sp = context.getSharedPreferences(SESSION_PREF,Context.MODE_PRIVATE);
        String session_id = sp.getString("session"," ");
        getProfile(session_id,media_id);

    }


    private void getProfile(final String session_id, final String media_id) {

        String PROFILE_URI = "https://api.themoviedb.org/3/account?api_key="+api_key+"&session_id=" + session_id;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(PROFILE_URI, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        parseOutput(response,session_id,Integer.valueOf(media_id));
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("webi", "Volley Error: " + error.getCause());

            }
        });

        tmdbrequestQueue.add(jsonObjectRequest);
    }

    private void parseOutput(JSONObject response,String session_id,int media_id) {

        try {

            String account_id = response.getString("id");
            if (account_id != null) {

                String query = "https://api.themoviedb.org/3/account/"+account_id+"/favorite?api_key="+
                        api_key+"&session_id="+session_id;

                unmarkFavFinal(query,media_id);



            }else{
                CustomToast.show(context,"You are not logged in. Please login from account.",false);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            CustomToast.show(context,"You are not logged in. Please login from account.",false);
        }
    }

    private void unmarkFavFinal(String query,int media_id) {

        JSONObject jsonBody = new JSONObject();
        try {

            jsonBody.put("media_type", "movie");
            jsonBody.put("media_id", media_id);
            jsonBody.put("favorite", false);

        } catch (JSONException e) {

            e.printStackTrace();
        }
        final String mRequestBody = jsonBody.toString();


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(query, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        parseUnMarkedResponse(response);
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("webi", "Volley Error: " + error.getCause());

            }
        }){

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


    private void parseUnMarkedResponse(JSONObject response) {

        if (listener!=null)
             listener.unmarked(position);

        try {
            int status_code = response.getInt("status_code");

             if(status_code == 13){
                CustomToast.show(context,"Movie removed from favorite list.",false);
            }else{
                 CustomToast.show(context,"Can't remove from favorite list.",false);
             }

        } catch (JSONException e) {
            Log.d("webi",e.getCause().toString());
            CustomToast.show(context,"Can't remove from favorite list.",false);
        }

    }


    public void setUnmarkedListener(UnmarkedListener listener){
        this.listener = listener;
    }

    public interface UnmarkedListener{
        void unmarked(int position);
    }

}
