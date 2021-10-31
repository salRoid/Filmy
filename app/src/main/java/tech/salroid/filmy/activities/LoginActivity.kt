package tech.salroid.filmy.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
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

public class LoginActivity extends AppCompatActivity {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.logo)
    TextView logo;
    TmdbVolleySingleton tmdbVolleySingleton = TmdbVolleySingleton.getInstance();
    RequestQueue tmdbrequestQueue = tmdbVolleySingleton.getRequestQueue();
    private ProgressDialog progressDialog;
    private boolean tokenization = false;
    private String requestToken;
    private boolean nightMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        nightMode = sp.getBoolean("dark", false);
        if (nightMode)
            setTheme(R.style.AppTheme_Base_Dark);
        else
            setTheme(R.style.AppTheme_Base);

        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(" ");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Typeface typeface =  ResourcesCompat.getFont(this,R.font.rubik);
        logo.setTypeface(typeface);


        if (nightMode)
            allThemeLogic();

        loginNow();

    }


    private void allThemeLogic() {
        logo.setTextColor(Color.parseColor("#bdbdbd"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }


    public void loginNow() {


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("TMDB Login");
        progressDialog.setMessage("Please wait...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        logInBackground();


    }

    private void logInBackground() {

        String api_key = BuildConfig.TMDB_API_KEY;
        final String BASE_URL = "https://api.themoviedb.org/3/authentication/token/new?api_key="+api_key;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(BASE_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        parseOutput(response);
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("webi", "Volley Error: " + error.getCause());

            }
        });
        tmdbrequestQueue.add(jsonObjectRequest);


    }

    private void parseOutput(JSONObject response) {

        try {

            boolean status = response.getBoolean("success");

            if (status) {

                requestToken = response.getString("request_token");
                tokenization = true;
                validateToken(requestToken);


            } else {

                progressDialog.dismiss();
                CustomToast.show(this, "Failed to login", false);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void validateToken(final String requestToken) {

        String url = "https://www.themoviedb.org/authenticate/" + requestToken;
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorAccent));
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(url));

    }


    @Override
    protected void onResume() {
        super.onResume();

        if (tokenization && requestToken!=null){

            String api_key = BuildConfig.TMDB_API_KEY;
            final String SESSION_QUERY = "https://api.themoviedb.org/3/authentication/session/new?api_key="+api_key+"&request_token=" + requestToken;
            querySession(SESSION_QUERY);
        }

    }


    private void querySession(String session_query) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(session_query, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        parseSession(response);
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("webi", "Volley Error: " + error.getCause());

            }
        });

        tmdbrequestQueue.add(jsonObjectRequest);


    }

    private void parseSession(JSONObject response) {

        try {

            boolean status = response.getBoolean("success");

            if (status) {

                String session_id = response.getString("session_id");
                progressDialog.dismiss();

                Intent resultIntent = new Intent();
                resultIntent.putExtra("session_id", session_id);
                setResult(RESULT_OK, resultIntent);

                finish();

            } else {

                progressDialog.dismiss();
                CustomToast.show(this, "Failed to login", false);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
