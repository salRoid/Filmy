package tech.salroid.filmy.Activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import java.util.List;
import tech.salroid.filmy.DataClasses.MovieDetailsData;
import tech.salroid.filmy.Database.FilmContract;
import tech.salroid.filmy.Datawork.MovieDetailsActivityParseWork;
import tech.salroid.filmy.CustomAdapter.MovieDetailsActivityAdapter;
import tech.salroid.filmy.R;
import tech.salroid.filmy.Network.VolleySingleton;

public class MovieDetailsActivity extends AppCompatActivity implements MovieDetailsActivityAdapter.ClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    Context context = this;
    private String movie_id;
    private boolean fromActivity;
    private RecyclerView cast_recycler;
    private RelativeLayout header;
    private static TextView det_title, det_tagline, det_overview, det_rating;
    private static ImageView youtube_link, banner;

    private final String LOG_TAG = MovieDetailsActivity.class.getSimpleName();
    private final int MOVIE_DETAILS_LOADER = 2;


    private static final String[] GET_MOVIE_COLUMNS = {

            FilmContract.MoviesEntry.MOVIE_TITLE,
            FilmContract.MoviesEntry.MOVIE_BANNER,
            FilmContract.MoviesEntry.MOVIE_DESCRIPTION,
            FilmContract.MoviesEntry.MOVIE_TAGLINE,
            FilmContract.MoviesEntry.MOVIE_TRAILER,
            FilmContract.MoviesEntry.MOVIE_RATING
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);


        det_title = (TextView) findViewById(R.id.detail_title);
        det_tagline = (TextView) findViewById(R.id.detail_tagline);
        det_overview = (TextView) findViewById(R.id.detail_overview);
        det_rating = (TextView) findViewById(R.id.detail_rating);
        youtube_link = (ImageView) findViewById(R.id.detail_youtube);
        banner = (ImageView) findViewById(R.id.bannu);

        Intent intent = getIntent();
        if (intent != null) {
            fromActivity = intent.getBooleanExtra("activity",false);
            movie_id = intent.getStringExtra("id");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cast_recycler = (RecyclerView) findViewById(R.id.cast_recycler);
        header = (RelativeLayout) findViewById(R.id.header);
        cast_recycler.setLayoutManager(new LinearLayoutManager(MovieDetailsActivity.this));
        cast_recycler.setNestedScrollingEnabled(false);

        getMovieDetails();


        if (fromActivity)
          getSupportLoaderManager().initLoader(MOVIE_DETAILS_LOADER, null, this);


    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private void getMovieDetails() {

        VolleySingleton volleySingleton = VolleySingleton.getInstance();
        RequestQueue requestQueue = volleySingleton.getRequestQueue();

        final String BASE_URL_MOVIE_DETAILS = new String("https://api.trakt.tv/movies/" + movie_id + "?extended=full,images");
        final String BASE_MOVIE_CAST_DETAILS = new String("https://api.trakt.tv/movies/" + movie_id + "/people?extended=images");

        JsonObjectRequest jsonObjectRequestForMovieDetails = new JsonObjectRequest(Request.Method.GET, BASE_URL_MOVIE_DETAILS, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        parseMovieDetails(response.toString());


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("webi", "Volley Error: " + error.getCause());

            }
        }
        );


        JsonObjectRequest jsonObjectRequestForMovieCastDetails = new JsonObjectRequest(Request.Method.GET, BASE_MOVIE_CAST_DETAILS, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        cast_parseOutput(response.toString());

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("webi", "Volley Error: " + error.getCause());

            }
        }
        );


        requestQueue.add(jsonObjectRequestForMovieDetails);
        requestQueue.add(jsonObjectRequestForMovieCastDetails);


    }


    void parseMovieDetails(String movieDetails) {


        String title,tagline, overview, trailer, banner_profile;
        double rating;
        String img_url = "http://salroid.tech";

        try {

            JSONObject jsonObject = new JSONObject(movieDetails);

            ContentValues contentValues = new ContentValues();

            title = jsonObject.getString("title");
            tagline = jsonObject.getString("tagline");
            overview = jsonObject.getString("overview");
            trailer = jsonObject.getString("trailer");
            rating = jsonObject.getDouble("rating");

            double roundOff = Math.round(rating * 100.0) / 100.0;

            banner_profile = jsonObject.getJSONObject("images").getJSONObject("fanart").getString("medium");


            try {
                String videoId = extractYoutubeId(trailer);
                img_url = "http://img.youtube.com/vi/" + videoId + "/0.jpg";

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } finally {

                if(fromActivity){

                    contentValues.put(FilmContract.MoviesEntry.MOVIE_BANNER, banner_profile);
                    contentValues.put(FilmContract.MoviesEntry.MOVIE_TAGLINE, tagline);
                    contentValues.put(FilmContract.MoviesEntry.MOVIE_DESCRIPTION, overview);
                    contentValues.put(FilmContract.MoviesEntry.MOVIE_TRAILER, img_url);
                    contentValues.put(FilmContract.MoviesEntry.MOVIE_RATING, String.valueOf(roundOff));

                    final String selection =
                            FilmContract.MoviesEntry.TABLE_NAME +
                                    "." + FilmContract.MoviesEntry.MOVIE_ID + " = ? ";
                    final String[] selectionArgs = {movie_id};

                    long id = context.getContentResolver().update(FilmContract.MoviesEntry.buildMovieByTag(movie_id),contentValues, selection, selectionArgs);

                    if (id != -1) {
                        Log.d(LOG_TAG, "Movie row updated with new values.");
                        Log.d("webi",img_url);
                    }
                }else{

                    showParsedContent(title,banner_profile,img_url,tagline,overview,String.valueOf(roundOff));

                }


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showParsedContent(String title,String banner_profile,String img_url,String tagline,String overview,String rating) {


        det_title.setText(title);
        det_tagline.setText(tagline);
        det_overview.setText(overview);
        det_rating.setText(rating);


        Log.d("webi","cast"+img_url);

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
                                Palette.Swatch swatch = p.getMutedSwatch();

                                if (swatch != null) {
                                    header.setBackgroundColor(swatch.getRgb());
                                    det_title.setTextColor(swatch.getTitleTextColor());
                                    det_tagline.setTextColor(swatch.getBodyTextColor());
                                }
                            }
                        });

                    }
                });


        Glide.with(context)
                .load(img_url)
                .fitCenter()
                .into(youtube_link);


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
        MovieDetailsActivityAdapter cast_adapter = new MovieDetailsActivityAdapter(this, cast_list);
        cast_adapter.setClickListener(this);
        cast_recycler.setAdapter(cast_adapter);


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

        return new CursorLoader(this, FilmContract.MoviesEntry.buildMovieWithMovieId(movie_id), GET_MOVIE_COLUMNS, null, null, null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {


        if (data != null && data.moveToFirst()) {

            int title_index = data.getColumnIndex(FilmContract.MoviesEntry.MOVIE_TITLE);
            int banner_index = data.getColumnIndex(FilmContract.MoviesEntry.MOVIE_BANNER);
            int tagline_index = data.getColumnIndex(FilmContract.MoviesEntry.MOVIE_TAGLINE);
            int description_index = data.getColumnIndex(FilmContract.MoviesEntry.MOVIE_DESCRIPTION);
            int trailer_index = data.getColumnIndex(FilmContract.MoviesEntry.MOVIE_TRAILER);
            int rating_index = data.getColumnIndex(FilmContract.MoviesEntry.MOVIE_RATING);

            String title = data.getString(title_index);
            String banner_url = data.getString(banner_index);
            String tagline = data.getString(tagline_index);
            String overview = data.getString(description_index);
            String trailer = data.getString(trailer_index);
            String rating = data.getString(rating_index);


            det_title.setText(title);
            det_tagline.setText(tagline);
            det_overview.setText(overview);
            det_rating.setText(rating);


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

                                    if (swatch != null) {
                                        header.setBackgroundColor(swatch.getRgb());
                                        det_title.setTextColor(swatch.getTitleTextColor());
                                        det_tagline.setTextColor(swatch.getBodyTextColor());
                                    }
                                }
                            });

                        }
                    });


            Glide.with(context)
                    .load(trailer)
                    .fitCenter()
                    .into(youtube_link);
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }



}
