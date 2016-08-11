package tech.salroid.filmy.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import tech.salroid.filmy.R;
import tech.salroid.filmy.custom_adapter.CharacterDetailsActivityAdapter;
import tech.salroid.filmy.data_classes.CharacterDetailsData;
import tech.salroid.filmy.fragment.FullReadFragment;
import tech.salroid.filmy.network_stuff.VolleySingleton;
import tech.salroid.filmy.parser.CharacterDetailActivityParseWork;

public class CharacterDetailsActivity extends AppCompatActivity implements CharacterDetailsActivityAdapter.ClickListener {


    Context co = this;
    FrameLayout headerContainer;
    private String character_id;
    private ImageView character_small;
    private RecyclerView char_recycler;
    private String character_title = null, movie_json = null;
    private TextView more;
    private String character_bio;
    private FullReadFragment fullReadFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_cast);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        more = (TextView) findViewById(R.id.more);


        if (getActionBar() != null)
            getActionBar().setDisplayHomeAsUpEnabled(true);

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!(movie_json==null && character_title==null)) {
                    Intent intent = new Intent(CharacterDetailsActivity.this, FullMovieActivity.class);
                    intent.putExtra("cast_json", movie_json);
                    intent.putExtra("toolbar_title", character_title);
                    startActivity(intent);

                }


            }
        });

        char_recycler = (RecyclerView) findViewById(R.id.character_movies);
        char_recycler.setLayoutManager(new LinearLayoutManager(CharacterDetailsActivity.this));
        char_recycler.setNestedScrollingEnabled(false);

        headerContainer = (FrameLayout) findViewById(R.id.header_container);
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

        character_small = (ImageView) findViewById(R.id.cast_img_small);

        getDetailedMovieAndCast();

    }


    @Override
    protected void onResume() {
        super.onResume();


    }

    private void getDetailedMovieAndCast() {


        VolleySingleton volleySingleton = VolleySingleton.getInstance();
        RequestQueue requestQueue = volleySingleton.getRequestQueue();

        final String BASE_URL = getResources().getString(R.string.trakt_base_url);

        final String BASE_URL_PERSON_DETAIL = BASE_URL + character_id + "?" +
                getResources().getString(R.string.person_details_suffix);

        final String BASE_URL_PEOPLE_MOVIES = BASE_URL + character_id +
                getResources().getString(R.string.person_movies_details);

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
            String char_face = jsonObject.getJSONObject("images").getJSONObject("headshot").getString("thumb");
            String char_desc = jsonObject.getString("biography");
            String char_birthday = jsonObject.getString("birthday");
            String char_birthplace = jsonObject.getString("birthplace");


            character_title = char_name;
            character_bio = char_desc;

            TextView ch_name = (TextView) findViewById(R.id.actor);
            TextView ch_desc = (TextView) findViewById(R.id.desc);
            TextView ch_birth = (TextView) findViewById(R.id.birth);
            TextView ch_place = (TextView) findViewById(R.id.birth_place);


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

            Glide.with(co)
                    .load(char_face)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .fitCenter()
                    .into(character_small);

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
        more.setVisibility(View.VISIBLE);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
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


}