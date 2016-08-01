package tech.salroid.filmy.Activity;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import tech.salroid.filmy.Custom.BreathingProgress;
import tech.salroid.filmy.DataClasses.MovieDetailsData;
import tech.salroid.filmy.Database.FilmContract;
import tech.salroid.filmy.Datawork.MovieDetailsActivityParseWork;
import tech.salroid.filmy.CustomAdapter.MovieDetailsActivityAdapter;
import tech.salroid.filmy.FullReadFragment;
import tech.salroid.filmy.R;
import tech.salroid.filmy.Network.VolleySingleton;

public class MovieDetailsActivity extends AppCompatActivity implements View.OnClickListener, MovieDetailsActivityAdapter.ClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    Context context = this;
    private String movie_id, trailer = null, movie_desc;
    private RecyclerView cast_recycler;
    private RelativeLayout header,main;
    BreathingProgress breathingProgress;

    private RequestQueue requestQueue;


    private static TextView det_title, det_tagline, det_overview,
                            det_rating, det_released, det_certification,
                            det_language, det_runtime;

    private static ImageView youtube_link, banner;
    private final String LOG_TAG = MovieDetailsActivity.class.getSimpleName();
    private final int MOVIE_DETAILS_LOADER = 2,SAVED_MOVIE_DETAILS_LOADER = 5;
    LinearLayout trailorBackground;
    TextView tvRating;
    FrameLayout trailorView, newMain, headerContainer;
    FullReadFragment fullReadFragment;
    HashMap<String, String> movieMap;
    boolean networkApplicable = false, databaseApplicable = false, savedDatabaseApplicable = false;


    private static final String[] GET_MOVIE_COLUMNS = {

            FilmContract.MoviesEntry.MOVIE_TITLE,
            FilmContract.MoviesEntry.MOVIE_BANNER,
            FilmContract.MoviesEntry.MOVIE_DESCRIPTION,
            FilmContract.MoviesEntry.MOVIE_TAGLINE,
            FilmContract.MoviesEntry.MOVIE_TRAILER,
            FilmContract.MoviesEntry.MOVIE_RATING,
            FilmContract.MoviesEntry.MOVIE_LANGUAGE,
            FilmContract.MoviesEntry.MOVIE_RELEASED,
            FilmContract.MoviesEntry.MOVIE_CERTIFICATION,
            FilmContract.MoviesEntry.MOVIE_RUNTIME,
    };


    private static final String[] GET_SAVE_COLUMNS = {

            FilmContract.SaveEntry.SAVE_ID,
            FilmContract.SaveEntry.SAVE_TITLE,
            FilmContract.SaveEntry.SAVE_BANNER,
            FilmContract.SaveEntry.SAVE_DESCRIPTION,
            FilmContract.SaveEntry.SAVE_TAGLINE,
            FilmContract.SaveEntry.SAVE_TRAILER,
            FilmContract.SaveEntry.SAVE_RATING,
            FilmContract.SaveEntry.SAVE_LANGUAGE,
            FilmContract.SaveEntry.SAVE_RELEASED,
            FilmContract.SaveEntry._ID,
            FilmContract.SaveEntry.SAVE_YEAR,
            FilmContract.SaveEntry.SAVE_CERTIFICATION,
            FilmContract.SaveEntry.SAVE_RUNTIME,
            FilmContract.SaveEntry.SAVE_POSTER_LINK,
    };


    private ImageView youtube_play_button;
    private TextView more;
    private String cast_json = null, movie_title = null, movie_tagline = null, movie_rating = null, show_centre_img_url = null, movie_trailer = null;
    private boolean trailer_boolean = false;
    private FrameLayout main_content;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        det_title = (TextView) findViewById(R.id.detail_title);
        det_tagline = (TextView) findViewById(R.id.detail_tagline);
        det_overview = (TextView) findViewById(R.id.detail_overview);
        det_rating = (TextView) findViewById(R.id.detail_rating);
        youtube_link = (ImageView) findViewById(R.id.detail_youtube);
        banner = (ImageView) findViewById(R.id.bannu);
        youtube_play_button = (ImageView) findViewById(R.id.play_button);
        trailorBackground = (LinearLayout) findViewById(R.id.trailorBackground);
        tvRating = (TextView) findViewById(R.id.tvRating);
        det_released = (TextView) findViewById(R.id.detail_released);
        det_certification = (TextView) findViewById(R.id.detail_certification);
        det_runtime = (TextView) findViewById(R.id.detail_runtime);
        det_language = (TextView) findViewById(R.id.detail_language);
        trailorView = (FrameLayout) findViewById(R.id.trailorView);
        more = (TextView) findViewById(R.id.more);

        breathingProgress  = (BreathingProgress) findViewById(R.id.breathingProgress);

        main = (RelativeLayout) findViewById(R.id.main);
        newMain = (FrameLayout) findViewById(R.id.new_main);
        main_content = (FrameLayout) findViewById(R.id.all_details_container);
        header = (RelativeLayout) findViewById(R.id.header);
        headerContainer = (FrameLayout) findViewById(R.id.header_container);

        cast_recycler = (RecyclerView) findViewById(R.id.cast_recycler);
        cast_recycler.setLayoutManager(new LinearLayoutManager(MovieDetailsActivity.this));
        cast_recycler.setNestedScrollingEnabled(false);


        headerContainer.setOnClickListener(this);

        more.setOnClickListener(this);

        newMain.setOnClickListener(this);

        trailorView.setOnClickListener(this);

        Intent intent = getIntent();

        getDataFromIntent(intent);


        //this should be called only when coming from the mainActivity and searchActivity & from
        //characterDetailsActivity
        if (networkApplicable)
            getMovieDetailsFromNetwork();

        if (databaseApplicable)
            getSupportLoaderManager().initLoader(MOVIE_DETAILS_LOADER, null, this);


        if (savedDatabaseApplicable)
            getSupportLoaderManager().initLoader(SAVED_MOVIE_DETAILS_LOADER, null, this);

        if (!databaseApplicable && !savedDatabaseApplicable){

            main.setVisibility(View.INVISIBLE);
            breathingProgress.setVisibility(View.VISIBLE);

        }


    }

    private void getDataFromIntent(Intent intent) {

        if (intent != null) {

            networkApplicable = intent.getBooleanExtra("network_applicable", false);

            databaseApplicable = intent.getBooleanExtra("database_applicable", false);

            savedDatabaseApplicable = intent.getBooleanExtra("saved_database_applicable", false);

            movie_id = intent.getStringExtra("id");

            movie_title = intent.getStringExtra("title");

        }

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private void getMovieDetailsFromNetwork() {

        VolleySingleton volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();

        final String BASE_URL_MOVIE_DETAILS = new String("https://api.trakt.tv/movies/" + movie_id + "?extended=full,images");
        JsonObjectRequest jsonObjectRequestForMovieDetails = new JsonObjectRequest(Request.Method.GET, BASE_URL_MOVIE_DETAILS, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        parseMovieDetails(response.toString());


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

               // Log.e("webi", "Volley Error: " + error.getCause());

            }
        }
        );

        requestQueue.add(jsonObjectRequestForMovieDetails);

        getCastFromNetwork(requestQueue);


    }

    private void getCastFromNetwork(RequestQueue requestQueue) {

        final String BASE_MOVIE_CAST_DETAILS = new String("https://api.trakt.tv/movies/" + movie_id + "/people?extended=images");
        JsonObjectRequest jsonObjectRequestForMovieCastDetails = new JsonObjectRequest(Request.Method.GET, BASE_MOVIE_CAST_DETAILS, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        cast_json = response.toString();
                        cast_parseOutput(response.toString());

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

               // Log.e("webi", "Volley Error: " + error.getCause());

            }
        }
        );

        if (requestQueue != null)
            requestQueue.add(jsonObjectRequestForMovieCastDetails);
        else {
            VolleySingleton volleySingleton = VolleySingleton.getInstance();
            requestQueue = volleySingleton.getRequestQueue();
            requestQueue.add(jsonObjectRequestForMovieCastDetails);
        }

    }


    void parseMovieDetails(String movieDetails) {


        String title, tagline, overview, banner_profile, certification, runtime, language, released, poster;
        double rating;
        String img_url = null;

        try {


            JSONObject jsonObject = new JSONObject(movieDetails);

            ContentValues contentValues = new ContentValues();

            title = jsonObject.getString("title");
            tagline = jsonObject.getString("tagline");
            overview = jsonObject.getString("overview");
            trailer = jsonObject.getString("trailer");
            rating = jsonObject.getDouble("rating");
            certification = jsonObject.getString("certification");
            language = jsonObject.getString("language");
            released = jsonObject.getString("released");
            runtime = jsonObject.getString("runtime");

            if (certification.equals("null")) {
                certification = "--";
            }

            double roundOff = Math.round(rating * 100.0) / 100.0;

            movie_rating = String.valueOf(roundOff);

            banner_profile = jsonObject.getJSONObject("images").getJSONObject("fanart").getString("medium");
            poster = jsonObject.getJSONObject("images").getJSONObject("poster").getString("thumb");

            movie_desc = overview;
            movie_title = title;
            movie_tagline = tagline;
            show_centre_img_url = banner_profile;

            movieMap = new HashMap<String, String>();
            movieMap.put("title", title);
            movieMap.put("tagline", tagline);
            movieMap.put("overview", overview);
            movieMap.put("rating", movie_rating);
            movieMap.put("certification", certification);
            movieMap.put("language", language);
            movieMap.put("year", "0");
            movieMap.put("released", released);
            movieMap.put("runtime", runtime);
            movieMap.put("trailer", trailer);
            movieMap.put("banner", banner_profile);
            movieMap.put("poster", poster);


            try {

                if (!(trailer.equals("null"))) {

                    trailer_boolean = true;
                    String videoId = extractYoutubeId(trailer);
                    img_url = "http://img.youtube.com/vi/" + videoId + "/0.jpg";

                    //  movie_trailer=trailer;

                } else {

                    img_url = jsonObject.getJSONObject("images").getJSONObject("poster").getString("medium");

                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } finally {

                if (databaseApplicable) {

                    contentValues.put(FilmContract.MoviesEntry.MOVIE_BANNER, banner_profile);
                    contentValues.put(FilmContract.MoviesEntry.MOVIE_TAGLINE, tagline);
                    contentValues.put(FilmContract.MoviesEntry.MOVIE_DESCRIPTION, overview);
                    contentValues.put(FilmContract.MoviesEntry.MOVIE_TRAILER, img_url);
                    contentValues.put(FilmContract.MoviesEntry.MOVIE_CERTIFICATION, certification);
                    contentValues.put(FilmContract.MoviesEntry.MOVIE_LANGUAGE, language);
                    contentValues.put(FilmContract.MoviesEntry.MOVIE_RUNTIME, runtime);
                    contentValues.put(FilmContract.MoviesEntry.MOVIE_RELEASED, released);
                    contentValues.put(FilmContract.MoviesEntry.MOVIE_RATING, movie_rating);

                    final String selection =
                            FilmContract.MoviesEntry.TABLE_NAME +
                                    "." + FilmContract.MoviesEntry.MOVIE_ID + " = ? ";
                    final String[] selectionArgs = {movie_id};

                    long id = context.getContentResolver().update(FilmContract.MoviesEntry.buildMovieByTag(movie_id), contentValues, selection, selectionArgs);

                    if (id != -1) {
                        //  Log.d(LOG_TAG, "Movie row updated with new values.");
                    }
                } else {

                    showParsedContent(title, banner_profile, img_url, tagline, overview, movie_rating, runtime, released, certification, language);

                }


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showParsedContent(String title, String banner_profile, String img_url, String tagline,
                                   String overview, String rating, String runtime,
                                   String released, String certification, String language) {


        det_title.setText(title);
        det_tagline.setText(tagline);
        det_overview.setText(overview);
        det_rating.setText(rating);

        det_runtime.setText(runtime + " mins");
        det_released.setText(released);
        det_certification.setText(certification);
        det_language.setText(language);


        Glide.with(context)
                .load(banner_profile)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {

                        banner.setImageBitmap(resource);

                        Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
                            public void onGenerated(Palette p) {
                                // Use generated instance
                                Palette.Swatch swatch = p.getVibrantSwatch();
                                Palette.Swatch trailorSwatch = p.getDarkVibrantSwatch();

                                if (swatch != null) {
                                    header.setBackgroundColor(swatch.getRgb());
                                    det_title.setTextColor(swatch.getTitleTextColor());
                                    det_tagline.setTextColor(swatch.getBodyTextColor());
                                    det_overview.setTextColor(swatch.getBodyTextColor());


                                }
                                if (trailorSwatch != null) {
                                    trailorBackground.setBackgroundColor(trailorSwatch.getRgb());
                                    tvRating.setTextColor(trailorSwatch.getTitleTextColor());
                                    det_rating.setTextColor(trailorSwatch.getBodyTextColor());
                                }
                            }
                        });

                    }
                });


        Glide.with(context)
                .load(img_url)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {

                        youtube_link.setImageBitmap(resource);
                        if (trailer_boolean)
                            youtube_play_button.setVisibility(View.VISIBLE);
                    }

                });

        main.setVisibility(View.VISIBLE);
        breathingProgress.setVisibility(View.INVISIBLE);

    }


    @Override
    public void itemClicked(MovieDetailsData setterGetter, int position) {
        Intent intent = new Intent(this, CharacterDetailsActivity.class);
        intent.putExtra("id", setterGetter.getCast_id());
        startActivity(intent);
    }


    private void cast_parseOutput(String cast_result) {

        MovieDetailsActivityParseWork par = new MovieDetailsActivityParseWork(this, cast_result);
        List<MovieDetailsData> cast_list = par.parse_cast();
        Boolean size = true;
        MovieDetailsActivityAdapter cast_adapter = new MovieDetailsActivityAdapter(this, cast_list, size);
        cast_adapter.setClickListener(this);
        cast_recycler.setAdapter(cast_adapter);
        more.setVisibility(View.VISIBLE);


    }

    public String extractYoutubeId(String url) throws MalformedURLException {
        String query = new URL(url).getQuery();
        String[] param = query.split("&");
        String id = null;
        for (String row : param) {
            String[] param1 = row.split("=");
            if (param1[0].equals("v")) {
                id = param1[1];
            }
        }
        return id;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        CursorLoader cursorloader = null;

        if (id == MOVIE_DETAILS_LOADER) {

            cursorloader = new CursorLoader(this, FilmContract.MoviesEntry.buildMovieWithMovieId(movie_id), GET_MOVIE_COLUMNS, null, null, null);

        } else if (id == SAVED_MOVIE_DETAILS_LOADER) {

            final String selection = FilmContract.SaveEntry.TABLE_NAME +
                    "." + FilmContract.SaveEntry.SAVE_ID + " = ? ";
            String[] selectionArgs = {movie_id};

            cursorloader = new CursorLoader(this, FilmContract.SaveEntry.CONTENT_URI, GET_SAVE_COLUMNS, selection, selectionArgs, null);

        }

        return cursorloader;

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        int id = loader.getId();

        if (id == MOVIE_DETAILS_LOADER) {

            fetchMovieDetailsFromCursor(data);

        } else if (id == SAVED_MOVIE_DETAILS_LOADER) {

            fetchSavedMovieDetailsFromCursor(data);
        }

    }

    private void fetchSavedMovieDetailsFromCursor(Cursor data) {

        if (data != null && data.moveToFirst()) {

            int title_index = data.getColumnIndex(FilmContract.SaveEntry.SAVE_TITLE);
            int banner_index = data.getColumnIndex(FilmContract.SaveEntry.SAVE_BANNER);
            int tagline_index = data.getColumnIndex(FilmContract.SaveEntry.SAVE_TAGLINE);
            int description_index = data.getColumnIndex(FilmContract.SaveEntry.SAVE_DESCRIPTION);
            int trailer_index = data.getColumnIndex(FilmContract.SaveEntry.SAVE_TRAILER);
            int rating_index = data.getColumnIndex(FilmContract.SaveEntry.SAVE_RATING);
            int released_index = data.getColumnIndex(FilmContract.SaveEntry.SAVE_RATING);
            int runtime_index = data.getColumnIndex(FilmContract.SaveEntry.SAVE_RUNTIME);
            int language_index = data.getColumnIndex(FilmContract.SaveEntry.SAVE_LANGUAGE);
            int certification_index = data.getColumnIndex(FilmContract.SaveEntry.SAVE_CERTIFICATION);

            int poster_link_index = data.getColumnIndex(FilmContract.SaveEntry.SAVE_POSTER_LINK);

            String title = data.getString(title_index);
            String banner_url = data.getString(banner_index);
            String tagline = data.getString(tagline_index);
            String overview = data.getString(description_index);

            //as it will be used to show it on YouTube
            trailer = data.getString(trailer_index);
            String posterLink = data.getString(poster_link_index);

            String rating = data.getString(rating_index);
            String runtime = data.getString(runtime_index);
            String released = data.getString(released_index);
            String certification = data.getString(certification_index);
            String language = data.getString(language_index);


            det_title.setText(title);
            det_tagline.setText(tagline);
            det_overview.setText(overview);
            det_rating.setText(rating);

            det_runtime.setText(runtime + " mins");
            det_released.setText(released);
            det_certification.setText(certification);
            det_language.setText(language);

            movie_desc = overview;
            show_centre_img_url = banner_url;


            Glide.with(context)
                    .load(banner_url)
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {

                            banner.setImageBitmap(resource);

                            Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
                                public void onGenerated(Palette p) {
                                    // Use generated instance
                                    Palette.Swatch swatch = p.getMutedSwatch();
                                    Palette.Swatch trailorSwatch = p.getDarkVibrantSwatch();

                                    if (swatch != null) {

                                        header.setBackgroundColor(swatch.getRgb());
                                        det_title.setTextColor(swatch.getTitleTextColor());
                                        det_tagline.setTextColor(swatch.getBodyTextColor());
                                        det_overview.setTextColor(swatch.getBodyTextColor());

                                    }
                                    if (trailorSwatch != null) {
                                        trailorBackground.setBackgroundColor(trailorSwatch.getRgb());
                                        tvRating.setTextColor(trailorSwatch.getTitleTextColor());
                                        det_rating.setTextColor(trailorSwatch.getBodyTextColor());
                                    }
                                }
                            });

                        }
                    });


            String thumbNail = null;

            if (!trailer.equals("null")) {

                trailer_boolean = true;

                try {

                    String videoId = extractYoutubeId(trailer);
                    thumbNail = "http://img.youtube.com/vi/" + videoId + "/0.jpg";

                } catch (Exception e) {

                }

            } else {
                thumbNail = posterLink;
            }


        //    Toast.makeText(this, thumbNail, Toast.LENGTH_LONG).show();


            Glide.with(context)
                    .load(thumbNail)
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {

                            youtube_link.setImageBitmap(resource);
                            if (trailer_boolean)
                                youtube_play_button.setVisibility(View.VISIBLE);
                        }

                    });


        }


        getCastFromNetwork(requestQueue);

    }

    private void fetchMovieDetailsFromCursor(Cursor data) {

        if (data != null && data.moveToFirst()) {

            int title_index = data.getColumnIndex(FilmContract.MoviesEntry.MOVIE_TITLE);
            int banner_index = data.getColumnIndex(FilmContract.MoviesEntry.MOVIE_BANNER);
            int tagline_index = data.getColumnIndex(FilmContract.MoviesEntry.MOVIE_TAGLINE);
            int description_index = data.getColumnIndex(FilmContract.MoviesEntry.MOVIE_DESCRIPTION);
            int trailer_index = data.getColumnIndex(FilmContract.MoviesEntry.MOVIE_TRAILER);
            int rating_index = data.getColumnIndex(FilmContract.MoviesEntry.MOVIE_RATING);
            int released_index = data.getColumnIndex(FilmContract.MoviesEntry.MOVIE_RELEASED);
            int runtime_index = data.getColumnIndex(FilmContract.MoviesEntry.MOVIE_RUNTIME);
            int language_index = data.getColumnIndex(FilmContract.MoviesEntry.MOVIE_LANGUAGE);
            int certification_index = data.getColumnIndex(FilmContract.MoviesEntry.MOVIE_CERTIFICATION);

            String title = data.getString(title_index);
            String banner_url = data.getString(banner_index);
            String tagline = data.getString(tagline_index);
            String overview = data.getString(description_index);
            String trailer = data.getString(trailer_index);
            String rating = data.getString(rating_index);
            String runtime = data.getString(runtime_index);
            String released = data.getString(released_index);
            String certification = data.getString(certification_index);
            String language = data.getString(language_index);


            det_title.setText(title);
            det_tagline.setText(tagline);
            det_overview.setText(overview);
            det_rating.setText(rating);

            det_runtime.setText(runtime + " mins");
            det_released.setText(released);
            det_certification.setText(certification);
            det_language.setText(language);


            Glide.with(context)
                    .load(banner_url)
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {

                            banner.setImageBitmap(resource);

                            Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
                                public void onGenerated(Palette p) {
                                    // Use generated instance
                                    Palette.Swatch swatch = p.getMutedSwatch();
                                    Palette.Swatch trailorSwatch = p.getDarkVibrantSwatch();

                                    if (swatch != null) {

                                        header.setBackgroundColor(swatch.getRgb());
                                        det_title.setTextColor(swatch.getTitleTextColor());
                                        det_tagline.setTextColor(swatch.getBodyTextColor());
                                        det_overview.setTextColor(swatch.getBodyTextColor());

                                    }
                                    if (trailorSwatch != null) {
                                        trailorBackground.setBackgroundColor(trailorSwatch.getRgb());
                                        tvRating.setTextColor(trailorSwatch.getTitleTextColor());
                                        det_rating.setTextColor(trailorSwatch.getBodyTextColor());
                                    }
                                }
                            });

                        }
                    });


            Glide.with(context)
                    .load(trailer)
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {

                            youtube_link.setImageBitmap(resource);
                            if (trailer_boolean)
                                youtube_play_button.setVisibility(View.VISIBLE);
                        }

                    });


        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {


            case android.R.id.home:

                finish();
                break;

            case R.id.action_search:

                movie_trailer = "http://www.imdb.com/title/" + movie_id;

                if (!(movie_title.equals(" ") && movie_rating.equals(" ") && movie_tagline.equals(" "))) {
                    Intent myIntent = new Intent(Intent.ACTION_SEND);
                    myIntent.setType("text/plain");
                    myIntent.putExtra(Intent.EXTRA_TEXT, "*" + movie_title + "*\n" + movie_tagline + "\nRating: " + movie_rating + " / 10\n" + movie_trailer + "\n");
                    startActivity(Intent.createChooser(myIntent, "Share with"));
                }

                break;

            case R.id.action_save:

                saveMovie();

                break;

        }

        return super.onOptionsItemSelected(item);
    }


    private void saveMovie() {

        if (movieMap != null && !movieMap.isEmpty()) {


            final ContentValues saveValues = new ContentValues();

            saveValues.put(FilmContract.SaveEntry.SAVE_ID, movie_id);
            saveValues.put(FilmContract.SaveEntry.SAVE_TITLE, movieMap.get("title"));
            saveValues.put(FilmContract.SaveEntry.SAVE_TAGLINE, movieMap.get("tagline"));
            saveValues.put(FilmContract.SaveEntry.SAVE_DESCRIPTION, movieMap.get("overview"));
            saveValues.put(FilmContract.SaveEntry.SAVE_BANNER, movieMap.get("banner"));
            saveValues.put(FilmContract.SaveEntry.SAVE_TRAILER, movieMap.get("trailer"));
            saveValues.put(FilmContract.SaveEntry.SAVE_RATING, movieMap.get("rating"));
            saveValues.put(FilmContract.SaveEntry.SAVE_YEAR, movieMap.get("year"));
            saveValues.put(FilmContract.SaveEntry.SAVE_POSTER_LINK, movieMap.get("poster"));
            saveValues.put(FilmContract.SaveEntry.SAVE_RUNTIME, movieMap.get("runtime"));
            saveValues.put(FilmContract.SaveEntry.SAVE_CERTIFICATION, movieMap.get("certification"));
            saveValues.put(FilmContract.SaveEntry.SAVE_LANGUAGE, movieMap.get("language"));
            saveValues.put(FilmContract.SaveEntry.SAVE_RELEASED, movieMap.get("released"));


            final String selection =
                    FilmContract.SaveEntry.TABLE_NAME +
                            "." + FilmContract.SaveEntry.SAVE_ID + " = ? ";
            final String[] selectionArgs = {movie_id};

            //  boolean deletePermission = false;
            Cursor alreadyCursor = context.getContentResolver().query(FilmContract.SaveEntry.CONTENT_URI, null, selection, selectionArgs, null);

            if (alreadyCursor.moveToFirst()) {
                //Already present in databse
                Snackbar.make(main_content,"Already present in database",Snackbar.LENGTH_SHORT).show();

            } else {

                final Cursor returnedCursor = context.getContentResolver().query(FilmContract.SaveEntry.CONTENT_URI, null, null, null, null);


                if (returnedCursor.moveToFirst() && returnedCursor.getCount() == 10) {
                    //No space to fill more. Have to delete oldest entry to save this Agree?

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    alertDialog.setTitle("Remove");
                    alertDialog.setIcon(R.drawable.ic_delete_sweep_black_24dp);

                    final TextView input = new TextView(context);
                    FrameLayout container = new FrameLayout(context);
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(96,48,96,48);
                    input.setLayoutParams(params);

                    input.setText("Save Limit reached , want to remove the oldest movie and save this one ?");
                    input.setTextColor(Color.parseColor("#303030"));

                    container.addView(input);


                    alertDialog.setView(container);
                    alertDialog.setPositiveButton("Okay",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    final String deleteSelection = FilmContract.SaveEntry.TABLE_NAME + "." + FilmContract.SaveEntry._ID + " = ? ";

                                    returnedCursor.moveToFirst();

                                    //Log.d(LOG_TAG, "This is the last index value which is going to be deleted "+returnedCursor.getInt(0));

                                    final String[] deletionArgs = {String.valueOf(returnedCursor.getInt(0))};


                                    long deletion_id = context.getContentResolver().delete(FilmContract.SaveEntry.CONTENT_URI, deleteSelection, deletionArgs);

                                    if (deletion_id != -1) {

                                        // Log.d(LOG_TAG, "We deleted this row" + deletion_id);

                                        Uri uri = context.getContentResolver().insert(FilmContract.SaveEntry.CONTENT_URI, saveValues);

                                        long movieRowId = ContentUris.parseId(uri);

                                        if (movieRowId != -1) {
                                            //inserted
                                            Snackbar.make(main_content, "Movie Saved", Snackbar.LENGTH_SHORT).show();

                                        } else {

                                            // Log.d(LOG_TAG, "row not Inserted in database");
                                        }

                                    } else {

                                        //delete was unsuccessful
                                    }

                                    dialog.cancel();
                                }
                            });

                    alertDialog.setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Write your code here to execute after dialog
                                    dialog.cancel();
                                }
                            });

                    alertDialog.show();
                }

                else {

                    Uri uri = context.getContentResolver().insert(FilmContract.SaveEntry.CONTENT_URI, saveValues);

                    long movieRowId = ContentUris.parseId(uri);

                    if (movieRowId != -1) {

                        Snackbar.make(main_content, "Movie Saved", Snackbar.LENGTH_SHORT).show();

                        // Toast.makeText(MovieDetailsActivity.this, "Movie Inserted", Toast.LENGTH_SHORT).show();

                    } else {

                        Snackbar.make(main_content, "Movie Not Saved", Snackbar.LENGTH_SHORT).show();

                    }
                }
            }
        }
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
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.movie_detail_menu, menu);

        menu.findItem(R.id.action_save).setVisible(!savedDatabaseApplicable);

        return true;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.header_container:


                if (movie_title != null && movie_desc != null) {

                    fullReadFragment = new FullReadFragment();
                    Bundle args = new Bundle();
                    args.putString("title", movie_title);
                    args.putString("desc", movie_desc);
                    fullReadFragment.setArguments(args);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.all_details_container, fullReadFragment, "DESC").commit();
                }

                break;

            case R.id.more:

                if (cast_json != null && movie_title != null) {

                    Intent intent = new Intent(MovieDetailsActivity.this, FullCastActivity.class);
                    intent.putExtra("cast_json", cast_json);
                    intent.putExtra("toolbar_title", movie_title);
                    startActivity(intent);

                }

                break;

            case R.id.new_main:

                if (!(show_centre_img_url == null)) {
                    Intent intent = new Intent(MovieDetailsActivity.this, FullScreenImage.class);
                    intent.putExtra("img_url", show_centre_img_url);
                    startActivity(intent);
                }

                break;

            case R.id.trailorView:

                if ((trailer_boolean))
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(trailer)));

                break;

        }
    }
}
