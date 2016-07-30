package tech.salroid.filmy.Activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import tech.salroid.filmy.FullReadFragment;
import tech.salroid.filmy.R;
import tech.salroid.filmy.Network.VolleySingleton;
import tech.salroid.filmy.SearchFragment;

public class MovieDetailsActivity extends AppCompatActivity implements MovieDetailsActivityAdapter.ClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    Context context = this;
    private String movie_id, trailer = null, movie_desc;
    private boolean fromActivity;
    private RecyclerView cast_recycler;
    private RelativeLayout header;
    private static TextView det_title, det_tagline, det_overview, det_rating, det_released, det_certification, det_language, det_runtime;
    private static ImageView youtube_link, banner;

    private final String LOG_TAG = MovieDetailsActivity.class.getSimpleName();
    private final int MOVIE_DETAILS_LOADER = 2;
    LinearLayout trailorBackground;
    TextView tvRating;
    FrameLayout trailorView, newMain,headerContainer;
    FullReadFragment fullReadFragment;


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
    private ImageView youtube_play_button;
    private TextView more;
    private String cast_json=null,movie_title=null,movie_tagline=null,movie_rating=null, show_centre_img_url=null,movie_trailer=null;
    private boolean trailer_boolean=false;



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
        youtube_play_button=(ImageView)findViewById(R.id.play_button);
        trailorBackground = (LinearLayout) findViewById(R.id.trailorBackground);
        tvRating = (TextView) findViewById(R.id.tvRating);
        det_released = (TextView) findViewById(R.id.detail_released);
        det_certification = (TextView) findViewById(R.id.detail_certification);
        det_runtime = (TextView) findViewById(R.id.detail_runtime);
        det_language = (TextView) findViewById(R.id.detail_language);
        trailorView = (FrameLayout) findViewById(R.id.trailorView);
        more=(TextView)findViewById(R.id.more);
        newMain = (FrameLayout) findViewById(R.id.new_main);
        header = (RelativeLayout) findViewById(R.id.header);
        headerContainer = (FrameLayout) findViewById(R.id.header_container);


        headerContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(movie_title!=null && movie_desc!=null){

                    fullReadFragment = new FullReadFragment();
                    Bundle args = new Bundle();
                    args.putString("title",movie_title);
                    args.putString("desc",movie_desc);

                    fullReadFragment.setArguments(args);

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.all_details_container,fullReadFragment,"DESC").commit();
                }


            }
        });


        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(cast_json.equals(" ")&& movie_title.equals(" "))){
                    Intent intent = new Intent(MovieDetailsActivity.this, FullCastActivity.class);
                    intent.putExtra("cast_json",cast_json);
                    intent.putExtra("toolbar_title",movie_title);
                    startActivity(intent);

                }


            }
        });

        newMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(show_centre_img_url.equals(null))){
                    Intent intent = new Intent(MovieDetailsActivity.this, FullScreenImage.class);
                    intent.putExtra("img_url",show_centre_img_url);
                    startActivity(intent);
                }
            }
        });

        trailorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((trailer_boolean))
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(trailer)));
            }
        });

        Intent intent = getIntent();

        if (intent != null) {
            fromActivity = intent.getBooleanExtra("activity",false);
            movie_id = intent.getStringExtra("id");
            movie_trailer="http://www.imdb.com/title/"+movie_id;
            movie_title = intent.getStringExtra("title");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cast_recycler = (RecyclerView) findViewById(R.id.cast_recycler);
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

                        Log.d("webi",response.toString());
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
                        cast_json=response.toString();
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


        String title,tagline, overview, banner_profile,certification,runtime,language,released;
        double rating;
        String img_url=null;

        try {

            JSONObject jsonObject = new JSONObject(movieDetails);

            ContentValues contentValues = new ContentValues();
            title = jsonObject.getString("title");
            tagline = jsonObject.getString("tagline");
            overview = jsonObject.getString("overview");
            trailer = jsonObject.getString("trailer");
            rating = jsonObject.getDouble("rating");
            certification=jsonObject.getString("certification");
            language=jsonObject.getString("language");
            released=jsonObject.getString("released");
            runtime=jsonObject.getString("runtime");

            movie_desc = overview;

            movie_title=title;
            movie_tagline=tagline;



            if(certification.equals("null")){
                certification = "--";
            }

            double roundOff = Math.round(rating * 100.0) / 100.0;

            movie_rating=String.valueOf(roundOff);

            banner_profile = jsonObject.getJSONObject("images").getJSONObject("fanart").getString("medium");

            show_centre_img_url=banner_profile;

            try {

                if(!(trailer.equals("null"))) {

                    trailer_boolean=true;

                    String videoId = extractYoutubeId(trailer);

                    img_url = "http://img.youtube.com/vi/" + videoId + "/0.jpg";

                  //  movie_trailer=trailer;
                }

                else{

                    img_url=jsonObject.getJSONObject("images").getJSONObject("poster").getString("medium");

                }




            } catch (MalformedURLException e) {
                e.printStackTrace();
            } finally {

                if(fromActivity){

                    contentValues.put(FilmContract.MoviesEntry.MOVIE_BANNER, banner_profile);
                    contentValues.put(FilmContract.MoviesEntry.MOVIE_TAGLINE, tagline);
                    contentValues.put(FilmContract.MoviesEntry.MOVIE_DESCRIPTION, overview);
                    contentValues.put(FilmContract.MoviesEntry.MOVIE_TRAILER, img_url);
                    contentValues.put(FilmContract.MoviesEntry.MOVIE_CERTIFICATION, certification);
                    contentValues.put(FilmContract.MoviesEntry.MOVIE_LANGUAGE, language);
                    contentValues.put(FilmContract.MoviesEntry.MOVIE_RUNTIME, runtime);
                    contentValues.put(FilmContract.MoviesEntry.MOVIE_RELEASED, released);
                    contentValues.put(FilmContract.MoviesEntry.MOVIE_RATING, String.valueOf(roundOff));

                    final String selection =
                            FilmContract.MoviesEntry.TABLE_NAME +
                                    "." + FilmContract.MoviesEntry.MOVIE_ID + " = ? ";
                    final String[] selectionArgs = {movie_id};

                    long id = context.getContentResolver().update(FilmContract.MoviesEntry.buildMovieByTag(movie_id),contentValues, selection, selectionArgs);

                    if (id != -1) {
                        Log.d(LOG_TAG, "Movie row updated with new values.");
                    }
                }else{

                    showParsedContent(title,banner_profile,img_url,tagline,overview,String.valueOf(roundOff),runtime,released,certification,language);

                }


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showParsedContent(String title,String banner_profile,String img_url,String tagline,String overview,
                                   String rating,String runtime,String released,String certification,String language) {


        det_title.setText(title);
        det_tagline.setText(tagline);
        det_overview.setText(overview);
        det_rating.setText(rating);

        det_runtime.setText(runtime+" mins");
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
                                    header.setBackgroundColor( swatch.getRgb());
                                    det_title.setTextColor(swatch.getTitleTextColor());
                                    det_tagline.setTextColor(swatch.getBodyTextColor());
                                    det_overview.setTextColor(swatch.getBodyTextColor());


                                }
                                if (trailorSwatch!=null){
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
                        if(trailer_boolean)
                         youtube_play_button.setVisibility(View.VISIBLE);
                    }

                });

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
        Boolean size=true;
        MovieDetailsActivityAdapter cast_adapter = new MovieDetailsActivityAdapter(this, cast_list,size);
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

            det_runtime.setText(runtime+" mins");
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
                                    if (trailorSwatch!=null){
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

        if(item.getItemId() == android.R.id.home)
            finish();

        if (item.getItemId() == R.id.action_search) {
            if(!(movie_title.equals(" ")&& movie_rating.equals(" ")&&movie_tagline.equals(" "))) {
                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                myIntent.putExtra(Intent.EXTRA_TEXT,"*"+movie_title+"*\n"+movie_tagline+"\nRating: "+movie_rating+" / 10\n"+movie_trailer+"\n");
                startActivity(Intent.createChooser(myIntent, "Share with"));
            }

        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {

        FullReadFragment fragment = (FullReadFragment) getSupportFragmentManager().findFragmentByTag("DESC");
        if (fragment != null && fragment.isVisible()) {
            getSupportFragmentManager().beginTransaction().remove(fullReadFragment).commit();
        }
        else {
            super.onBackPressed();
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.movie_detail_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        return true;
    }


}
