package tech.salroid.filmy.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.salroid.filmy.BuildConfig;
import tech.salroid.filmy.R;
import tech.salroid.filmy.custom_adapter.WatchlistAdapter;
import tech.salroid.filmy.customs.BreathingProgress;
import tech.salroid.filmy.data_classes.WatchlistData;
import tech.salroid.filmy.network_stuff.TmdbVolleySingleton;
import tech.salroid.filmy.parser.WatchListMovieParseWork;

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

public class WatchedList extends AppCompatActivity implements WatchlistAdapter.ClickListener {

    WatchlistAdapter watchlistAdapter;

    @BindView(R.id.breathingProgress)
    BreathingProgress breathingProgress;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.logo)
    TextView logo;
    @BindView(R.id.my_watchlist_recycler)
    RecyclerView my_watchlist_movies_recycler;
    @BindView(R.id.fav_image)
    ImageView dataImageView;
    @BindView(R.id.wl_display_text)
    TextView wlTextView;

    private boolean nightMode;
    private Context context;

    private TmdbVolleySingleton tmdbVolleySingleton = TmdbVolleySingleton.getInstance();
    private RequestQueue tmdbrequestQueue = tmdbVolleySingleton.getRequestQueue();

    private String api_key = BuildConfig.API_KEY;
    private String SESSION_PREF = "SESSION_PREFERENCE";
    private String account_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        context = this;
        SharedPreferences spref = context.getSharedPreferences(SESSION_PREF, Context.MODE_PRIVATE);
        String session_id = spref.getString("session", " ");
        getProfile(session_id);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        nightMode = sp.getBoolean("dark", false);
        if (nightMode)
            setTheme(R.style.AppTheme_Base_Dark);
        else
            setTheme(R.style.AppTheme_Base);

        setContentView(R.layout.activity_watched_list);

        showProgress();

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/canaro_extra_bold.otf");
        logo.setTypeface(typeface);

        if (nightMode)
            allThemeLogic();


        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);

        if (tabletSize) {

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

                StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(6,
                        StaggeredGridLayoutManager.VERTICAL);
                my_watchlist_movies_recycler.setLayoutManager(gridLayoutManager);
            } else {
                StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(8,
                        StaggeredGridLayoutManager.VERTICAL);
                my_watchlist_movies_recycler.setLayoutManager(gridLayoutManager);
            }

        } else {

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

                StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(3,
                        StaggeredGridLayoutManager.VERTICAL);
                my_watchlist_movies_recycler.setLayoutManager(gridLayoutManager);
            } else {
                StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(5,
                        StaggeredGridLayoutManager.VERTICAL);
                my_watchlist_movies_recycler.setLayoutManager(gridLayoutManager);
            }

        }
    }

    private void getProfile(final String session_id) {

        String PROFILE_URI = "https://api.themoviedb.org/3/account?api_key=" + api_key + "&session_id=" + session_id;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, PROFILE_URI, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            account_id = response.getString("id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        getfavourites(session_id, account_id);
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                wlTextView.setVisibility(View.VISIBLE);
                wlTextView.setText("You are not logged in.");
                Log.e("webi", "Volley Error: " + error.getCause());

            }
        });

        tmdbrequestQueue.add(jsonObjectRequest);
    }

    private void getfavourites(String session_id, String id) {

        String Favourite_Url = "https://api.themoviedb.org/3/account/" + id
                + "/watchlist/movies?api_key=" + api_key + "&session_id=" + session_id + "&sort_by=vote_average.asc";


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Favourite_Url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        parseoutput(response.toString());
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("webi", "Volley Errorbelow: " + error.getCause());

            }
        });

        tmdbrequestQueue.add(jsonObjectRequest);

    }

    private void parseoutput(String s) {
        Log.d("webi", "parseoutput: " + s);
        WatchListMovieParseWork pw = new WatchListMovieParseWork(context, s);
        List<WatchlistData> list = pw.parse_watchlist();
        watchlistAdapter = new WatchlistAdapter(this, list);
        if (list.size() == 0)
            wlTextView.setVisibility(View.VISIBLE);
        my_watchlist_movies_recycler.setAdapter(watchlistAdapter);
        watchlistAdapter.setClickListener(this);

        hideProgress();

    }


    private void allThemeLogic() {
        logo.setTextColor(Color.parseColor("#bdbdbd"));
        dataImageView.setColorFilter(Color.parseColor("#757575"), PorterDuff.Mode.MULTIPLY);


    }

    @Override
    public void itemClicked(WatchlistData watchlistData, int position) {

        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra("network_applicable", true);
        intent.putExtra("title", watchlistData.getFav_title());
        intent.putExtra("id", watchlistData.getFav_id());
        intent.putExtra("activity", false);

        startActivity(intent);

    }

    public void showProgress() {


        if (breathingProgress != null && my_watchlist_movies_recycler != null) {

            breathingProgress.setVisibility(View.VISIBLE);
            my_watchlist_movies_recycler.setVisibility(View.INVISIBLE);

        }
    }


    public void hideProgress() {

        if (breathingProgress != null && my_watchlist_movies_recycler != null) {

            breathingProgress.setVisibility(View.INVISIBLE);
            my_watchlist_movies_recycler.setVisibility(View.VISIBLE);

        }


    }
}
