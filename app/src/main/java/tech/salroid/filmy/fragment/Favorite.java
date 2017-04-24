package tech.salroid.filmy.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import tech.salroid.filmy.activities.MovieDetailsActivity;
import tech.salroid.filmy.custom_adapter.FavouriteAdapter;
import tech.salroid.filmy.customs.BreathingProgress;
import tech.salroid.filmy.data_classes.FavouriteData;
import tech.salroid.filmy.network_stuff.TmdbVolleySingleton;
import tech.salroid.filmy.parser.FavouriteMovieParseWork;
import tech.salroid.filmy.tmdb_account.UnMarkingFavorite;

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

public class Favorite extends Fragment implements FavouriteAdapter.ClickListener, FavouriteAdapter.LongClickListener, UnMarkingFavorite.UnmarkedListener {

    private FavouriteAdapter favouriteAdapter;

    @BindView(R.id.breathingProgress)
    BreathingProgress breathingProgress;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.logo)
    TextView logo;
    @BindView(R.id.my_fav_recycler)
    RecyclerView my_favourite_movies_recycler;
    @BindView(R.id.fav_image)
    ImageView dataImageView;
    @BindView(R.id.fav_display_text)
    TextView faTextView;

    @BindView(R.id.emptyContainer)
    LinearLayout emptyContainer;


    private boolean nightMode;
    private Context context;

    private TmdbVolleySingleton tmdbVolleySingleton = TmdbVolleySingleton.getInstance();
    private RequestQueue tmdbrequestQueue = tmdbVolleySingleton.getRequestQueue();

    private String api_key = BuildConfig.API_KEY;
    private String SESSION_PREF = "SESSION_PREFERENCE";
    private String account_id;
    private ProgressDialog progressDialog;
    private List<FavouriteData> list;

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

        setContentView(R.layout.activity_favourite);

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
                my_favourite_movies_recycler.setLayoutManager(gridLayoutManager);
            } else {
                StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(8,
                        StaggeredGridLayoutManager.VERTICAL);
                my_favourite_movies_recycler.setLayoutManager(gridLayoutManager);
            }

        } else {

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

                StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(3,
                        StaggeredGridLayoutManager.VERTICAL);
                my_favourite_movies_recycler.setLayoutManager(gridLayoutManager);
            } else {
                StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(5,
                        StaggeredGridLayoutManager.VERTICAL);
                my_favourite_movies_recycler.setLayoutManager(gridLayoutManager);
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
                emptyContainer.setVisibility(View.VISIBLE);
                faTextView.setText("You are not logged in.");
                Log.e("webi", "Volley Error: " + error.getCause());

            }
        });

        tmdbrequestQueue.add(jsonObjectRequest);
    }

    private void getfavourites(String session_id, String id) {

        String Favourite_Url = "https://api.themoviedb.org/3/account/" + id
                + "/favorite/movies?api_key=" + api_key + "&session_id=" + session_id + "&sort_by=vote_average.asc";


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Favourite_Url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            int total_results = response.getInt("total_results");

                            if (total_results>0)
                                parseoutput(response.toString());
                            else{
                                hideProgress();
                                emptyContainer.setVisibility(View.VISIBLE);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                hideProgress();
                emptyContainer.setVisibility(View.VISIBLE);
                faTextView.setText("Failed to get your list.");
                Log.e("webi", "Volley Error below: " + error.getCause());

            }
        });

        tmdbrequestQueue.add(jsonObjectRequest);

    }

    private void parseoutput(String s) {

        FavouriteMovieParseWork pw = new FavouriteMovieParseWork(context, s);
        list = pw.parse_favourite();
        favouriteAdapter = new FavouriteAdapter(this, list);
        if (list.isEmpty())
            faTextView.setVisibility(View.VISIBLE);
        my_favourite_movies_recycler.setAdapter(favouriteAdapter);
        favouriteAdapter.setClickListener(this);
        favouriteAdapter.setLongClickListener(this);

        hideProgress();

    }


    private void allThemeLogic() {
        logo.setTextColor(Color.parseColor("#bdbdbd"));
        dataImageView.setColorFilter(Color.parseColor("#757575"), PorterDuff.Mode.MULTIPLY);
    }

    @Override
    public void itemClicked(FavouriteData favouriteData, int position) {

        Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
        intent.putExtra("network_applicable", true);
        intent.putExtra("title", favouriteData.getFav_title());
        intent.putExtra("id", favouriteData.getFav_id());
        intent.putExtra("activity", false);

        startActivity(intent);

    }

    public void showProgress() {


        if (breathingProgress != null && my_favourite_movies_recycler != null) {

            breathingProgress.setVisibility(View.VISIBLE);
            my_favourite_movies_recycler.setVisibility(View.INVISIBLE);

        }
    }


    public void hideProgress() {

        if (breathingProgress != null && my_favourite_movies_recycler != null) {

            breathingProgress.setVisibility(View.INVISIBLE);
            my_favourite_movies_recycler.setVisibility(View.VISIBLE);

        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                break;

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void itemLongClicked(final FavouriteData favouriteData, final int position) {

        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
        arrayAdapter.add("Remove");
        adb.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                UnMarkingFavorite unMarkingFavorite = new UnMarkingFavorite();
                unMarkingFavorite.setUnmarkedListener(Favorite.this);

                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setTitle("Favorite");
                progressDialog.setMessage("Removing..");
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.show();

                unMarkingFavorite.unmarkThisAsFavorite(context, favouriteData.getFav_id(), position);

            }
        });

        adb.show();

    }

    @Override
    public void unmarked(int position) {
        if (progressDialog != null)
            progressDialog.dismiss();
        if (favouriteAdapter != null && list != null) {
            list.remove(position);
            favouriteAdapter.notifyItemRemoved(position);

            if (favouriteAdapter.getItemCount()==0)
                 emptyContainer.setVisibility(View.VISIBLE);

        }

    }
}
