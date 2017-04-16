package tech.salroid.filmy.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.salroid.filmy.BuildConfig;
import tech.salroid.filmy.R;
import tech.salroid.filmy.custom_adapter.CharacterDetailsActivityAdapter;
import tech.salroid.filmy.data_classes.CharacterDetailsData;
import tech.salroid.filmy.fragment.FullReadFragment;
import tech.salroid.filmy.network_stuff.TmdbVolleySingleton;
import tech.salroid.filmy.network_stuff.VolleySingleton;
import tech.salroid.filmy.parser.CharacterDetailActivityParseWork;

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


public class CharacterDetailsActivity extends AppCompatActivity implements CharacterDetailsActivityAdapter.ClickListener {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.more)
    TextView more;
    @BindView(R.id.cast_name)
    TextView ch_name;
    @BindView(R.id.desc)
    TextView ch_desc;
    @BindView(R.id.birth)
    TextView ch_birth;
    @BindView(R.id.birth_place)
    TextView ch_place;
    @BindView(R.id.character_movies)
    RecyclerView char_recycler;
    @BindView(R.id.header_container)
    FrameLayout headerContainer;
    @BindView(R.id.cast_poster)
    CircularImageView character_small;
    @BindView(R.id.logo)
    TextView logo;

    Context co = this;
    private String character_id;
    private String character_title = null, movie_json = null;
    private String character_bio;
    private FullReadFragment fullReadFragment;

    private boolean nightMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        nightMode = sp.getBoolean("dark", false);
        if (nightMode)
            setTheme(R.style.AppTheme_Base_Dark);
        else
            setTheme(R.style.AppTheme_Base);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_cast);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);


        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");


        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/canaro_extra_bold.otf");
        logo.setTypeface(typeface);



        if (nightMode)
            allThemeLogic();

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!(movie_json == null && character_title == null)) {
                    Intent intent = new Intent(CharacterDetailsActivity.this, FullMovieActivity.class);
                    intent.putExtra("cast_json", movie_json);
                    intent.putExtra("toolbar_title", character_title);
                    startActivity(intent);

                }


            }
        });


        char_recycler.setLayoutManager(new LinearLayoutManager(CharacterDetailsActivity.this));
        char_recycler.setNestedScrollingEnabled(false);

        headerContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (character_title != null && character_bio != null) {

                    fullReadFragment = new FullReadFragment();
                    Bundle args = new Bundle();
                    args.putString("title", character_title);
                    args.putString("desc", character_bio);

                    fullReadFragment.setArguments(args);

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main, fullReadFragment, "DESC").commit();
                }

            }
        });


        Intent intent = getIntent();
        if (intent != null) {
            character_id = intent.getStringExtra("id");
        }


        getDetailedMovieAndCast();

    }

    private void allThemeLogic() {logo.setTextColor(Color.parseColor("#bdbdbd"));}

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean nightModeNew = sp.getBoolean("dark", false);
        if (nightMode!=nightModeNew)
            recreate();
    }

    private void getDetailedMovieAndCast() {


        TmdbVolleySingleton volleySingleton = TmdbVolleySingleton.getInstance();
        RequestQueue requestQueue = volleySingleton.getRequestQueue();

        final String BASE_URL = getResources().getString(R.string.trakt_base_url);
        String api_key = BuildConfig.API_KEY;

        final String BASE_URL_PERSON_DETAIL = "https://api.themoviedb.org/3/person/"+character_id+"?api_key="+api_key;

        final String BASE_URL_PEOPLE_MOVIES = "https://api.themoviedb.org/3/person/"+character_id+"/movie_credits?api_key="+api_key;

        JsonObjectRequest personDetailRequest = new JsonObjectRequest(Request.Method.GET, BASE_URL_PERSON_DETAIL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        personDetailsParsing(response.toString());


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("webi", "Volley Error: " + error.getCause());

            }
        }
        );

        JsonObjectRequest personMovieDetailRequest = new JsonObjectRequest(Request.Method.GET, BASE_URL_PEOPLE_MOVIES, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        movie_json = response.toString();

                        cast_parseOutput(response.toString());

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("webi", "Volley Error: " + error.getCause());

            }
        }
        );


        requestQueue.add(personDetailRequest);
        requestQueue.add(personMovieDetailRequest);


    }


    @Override
    public void itemClicked(CharacterDetailsData setterGetterchar, int position) {
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra("id", setterGetterchar.getChar_id());
        intent.putExtra("network_applicable", true);
        intent.putExtra("activity", false);
        startActivity(intent);
    }


    void personDetailsParsing(String detailsResult) {


        try {
            JSONObject jsonObject = new JSONObject(detailsResult);

            String char_name = jsonObject.getString("name");
            String char_face = "http://image.tmdb.org/t/p/w185"+jsonObject.getString("profile_path");
            String char_desc = jsonObject.getString("biography");
            String char_birthday = jsonObject.getString("birthday");
            String char_birthplace = jsonObject.getString("place_of_birth");

            character_title = char_name;
            character_bio = char_desc;

            ch_name.setText(char_name);
            if (char_birthday.equals("null"))
                ch_birth.setVisibility(View.GONE);
            else
                ch_birth.setText(char_birthday);
            if (char_birthplace.equals("null"))
                ch_place.setVisibility(View.GONE);
            else
                ch_place.setText(char_birthplace);
            if (char_birthplace.equals("null"))
                ch_desc.setVisibility(View.GONE);
            else {
                if (Build.VERSION.SDK_INT >= 24) {
                    ch_desc.setText(Html.fromHtml(char_desc, Html.FROM_HTML_MODE_LEGACY));
                } else {
                    ch_desc.setText(Html.fromHtml(char_desc));
                }
            }

          /*Glide.with(co)
                  .load(char_banner)
                    .fitCenter()
                  .into(character_banner);*/


            try {

                Glide.with(co)
                        .load(char_face)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .fitCenter().into(character_small);

            } catch (Exception e) {
                //Log.d(LOG_TAG, e.getMessage());
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void cast_parseOutput(String cast_result) {

        CharacterDetailActivityParseWork par = new CharacterDetailActivityParseWork(this, cast_result);
        List<CharacterDetailsData> char_list = par.char_parse_cast();
        CharacterDetailsActivityAdapter char_adapter = new CharacterDetailsActivityAdapter(this, char_list, true);
        char_adapter.setClickListener(this);
        char_recycler.setAdapter(char_adapter);
        if (char_list.size() > 4)
            more.setVisibility(View.VISIBLE);
        else
            more.setVisibility(View.INVISIBLE);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                //reverse transition
                supportFinishAfterTransition();
            }

            else
                finish();

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {

        FullReadFragment fragment = (FullReadFragment) getSupportFragmentManager().findFragmentByTag("DESC");
        if (fragment != null && fragment.isVisible()) {
            getSupportFragmentManager().beginTransaction().remove(fullReadFragment).commit();
        } else {
            super.onBackPressed();
        }
    }



    @Override
    protected void onStop() {
        super.onStop();
        Glide.clear(character_small);
    }
}