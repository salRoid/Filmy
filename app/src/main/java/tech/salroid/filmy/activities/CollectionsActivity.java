package tech.salroid.filmy.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.salroid.filmy.BuildConfig;
import tech.salroid.filmy.R;
import tech.salroid.filmy.custom_adapter.MyPagerAdapter;
import tech.salroid.filmy.fragment.Favorite;
import tech.salroid.filmy.fragment.SavedMovies;
import tech.salroid.filmy.fragment.Trending;
import tech.salroid.filmy.fragment.WatchList;
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

public class CollectionsActivity extends AppCompatActivity {


    private final int REQUEST_CODE = 000;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.logo)
    TextView logo;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.header)
    FrameLayout loginHeader;
    @BindView(R.id.username)
    TextView tvUserName;
    @BindView(R.id.favContainer)
    FrameLayout favourite_layout;
    @BindView(R.id.watchlistContainer)
    FrameLayout watchlist_layout;
    TmdbVolleySingleton tmdbVolleySingleton = TmdbVolleySingleton.getInstance();
    RequestQueue tmdbrequestQueue = tmdbVolleySingleton.getRequestQueue();
    private boolean nightMode;
    private boolean logged_in;
    private String PREF_NAME = "SESSION_PREFERENCE";
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        SharedPreferences spref = PreferenceManager.getDefaultSharedPreferences(this);
        nightMode = spref.getBoolean("dark", false);
        if (nightMode)
            setTheme(R.style.AppTheme_Base_Dark);
        else
            setTheme(R.style.AppTheme_Base);
        setContentView(R.layout.activity_collections);
        ButterKnife.bind(this);


        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(" ");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (nightMode)
            allThemeLogic();


        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/canaro_extra_bold.otf");
        logo.setTypeface(typeface);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        favourite_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CollectionsActivity.this, Favorite.class));
            }
        });

        watchlist_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CollectionsActivity.this, WatchList.class));
            }
        });

        SharedPreferences sp = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String session_id = sp.getString("session", "");
        String username = sp.getString("username", "Not logged in");

        loginHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!logged_in) {

                    Intent intent = new Intent(CollectionsActivity.this, LoginActivity.class);
                    startActivityForResult(intent, REQUEST_CODE);

                }

            }
        });

        tvUserName.setText(username);
        getProfile(session_id);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item.getItemId() == android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }



    private void setupViewPager(ViewPager viewPager) {


        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new SavedMovies(), getString(R.string.offline));
        adapter.addFragment(new Trending(), getString(R.string.favorite));
        adapter.addFragment(new Trending(), getString(R.string.watchlist));
        viewPager.setAdapter(adapter);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {

            //save this session_id
            //this session id can be used next time to so that logged in is not
            //required next time. ( if session is not expired)

            String session_id = data.getStringExtra("session_id");
            logged_in = true;

            SharedPreferences sp = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("session", session_id);
            editor.apply();

            getProfile(session_id);

        }

    }

    private void getProfile(String session_id) {

        String api_key = BuildConfig.API_KEY;
        String PROFILE_URI = "https://api.themoviedb.org/3/account?api_key=" + api_key + "&session_id=" + session_id;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, PROFILE_URI, null,
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

            String username = response.getString("username");
            if (username != null) {

                tvUserName.setText(username);
                SharedPreferences sp = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("username", username);
                editor.apply();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Account Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    private void allThemeLogic() {
        logo.setTextColor(Color.parseColor("#bdbdbd"));

    }
}
