package tech.salroid.filmy.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import tech.salroid.filmy.Animation.RevealAnimation;
import tech.salroid.filmy.Custom.BreathingProgress;
import tech.salroid.filmy.Database.FilmContract;
import tech.salroid.filmy.Database.MovieDetailsUpdation;
import tech.salroid.filmy.Database.MovieLoaders;
import tech.salroid.filmy.Database.MovieSelection;
import tech.salroid.filmy.Database.OfflineMovies;
import tech.salroid.filmy.Fragments.CastFragment;
import tech.salroid.filmy.Fragments.FullReadFragment;
import tech.salroid.filmy.Network.GetDataFromNetwork;
import tech.salroid.filmy.R;


public class MovieDetailsActivity extends AppCompatActivity implements
        View.OnClickListener,
        LoaderManager.LoaderCallbacks<Cursor>, GetDataFromNetwork.DataFetchedListener {

    Context context = this;
    String movie_id_final;
    private String movie_id;
    private String trailer = null, movie_desc;

    private RelativeLayout header, main;
    BreathingProgress breathingProgress;

    private static TextView det_title, det_tagline, det_overview,
            det_rating, det_released, det_certification,
            det_language, det_runtime;

    private static ImageView youtube_link, banner;
    private final String LOG_TAG = MovieDetailsActivity.class.getSimpleName();

    LinearLayout trailorBackground;
    TextView tvRating;
    FrameLayout trailorView, newMain, headerContainer;
    FullReadFragment fullReadFragment;
    HashMap<String, String> movieMap;
    boolean networkApplicable, databaseApplicable, savedDatabaseApplicable;
    int type;


    private ImageView youtube_play_button;

    private String cast_json,
            movie_title,
            movie_tagline,
            movie_rating,
            show_centre_img_url, movie_trailer;


    private boolean trailer_boolean = false;
    private FrameLayout main_content;
    private String quality;
    boolean cache = true;
    private String banner_for_full_activity;
    FrameLayout allDetails;


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
        allDetails = (FrameLayout) findViewById(R.id.all_details_container);


        breathingProgress = (BreathingProgress) findViewById(R.id.breathingProgress);

        main = (RelativeLayout) findViewById(R.id.main);
        newMain = (FrameLayout) findViewById(R.id.new_main);
        main_content = (FrameLayout) findViewById(R.id.all_details_container);
        header = (RelativeLayout) findViewById(R.id.header);
        headerContainer = (FrameLayout) findViewById(R.id.header_container);


        SharedPreferences prefrence = PreferenceManager.getDefaultSharedPreferences(MovieDetailsActivity.this);
        quality = prefrence.getString("image_quality", "w780");
        cache = prefrence.getBoolean("cache", false);

        headerContainer.setOnClickListener(this);

        newMain.setOnClickListener(this);

        trailorView.setOnClickListener(this);

        Intent intent = getIntent();
        getDataFromIntent(intent);


        //fetching details and various data on the basis of requirements
        performDataFetching();


        if (savedInstanceState == null)
            RevealAnimation.performReveal(allDetails);

    }

    private void performDataFetching() {


        GetDataFromNetwork getStuffFromNetwork = new GetDataFromNetwork();
        getStuffFromNetwork.setDataFetchedListener(this);

        if (networkApplicable)
            getStuffFromNetwork.getMovieDetailsFromNetwork(movie_id);

        if (databaseApplicable)
            getSupportLoaderManager().initLoader(MovieLoaders.MOVIE_DETAILS_LOADER, null, this);

        if (savedDatabaseApplicable)
            getSupportLoaderManager().initLoader(MovieLoaders.SAVED_MOVIE_DETAILS_LOADER, null, this);

        if (!databaseApplicable && !savedDatabaseApplicable) {

            main.setVisibility(View.INVISIBLE);
            breathingProgress.setVisibility(View.VISIBLE);

        }

    }


    private void getDataFromIntent(Intent intent) {

        if (intent != null) {

            networkApplicable = intent.getBooleanExtra("network_applicable", false);

            databaseApplicable = intent.getBooleanExtra("database_applicable", false);

            savedDatabaseApplicable = intent.getBooleanExtra("saved_database_applicable", false);

            type = intent.getIntExtra("type", 0);

            movie_id = intent.getStringExtra("id");

            movie_title = intent.getStringExtra("title");

        }
    }


    private void showCastFragment(String title) {

        CastFragment castFragment = CastFragment.newInstance(movie_id_final, title);
        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.cast_container, castFragment)
                .commit();
    }


    void parseMovieDetails(String movieDetails) {

        String title, tagline, overview, banner_profile, runtime, language, released, poster;
        double rating;
        String img_url = null;

        try {

            JSONObject jsonObject = new JSONObject(movieDetails);

            title = jsonObject.getString("title");
            tagline = jsonObject.getString("tagline");
            overview = jsonObject.getString("overview");
            released = jsonObject.getString("release_date");
            runtime = jsonObject.getString("runtime");
            language = jsonObject.getString("original_language");

            //check the values correcly

            movie_id_final = jsonObject.getString("imdb_id");
            movie_rating = jsonObject.getString("vote_average");


            showCastFragment(title);


            JSONObject trailorsObject = jsonObject.getJSONObject("trailers");
            JSONArray youTubeArray = trailorsObject.getJSONArray("youtube");
            String trailor = null;

            if (youTubeArray.length() != 0) {

                for (int i = 0; i < youTubeArray.length(); i++) {
                    JSONObject singleTrailor = youTubeArray.getJSONObject(i);

                    String type = singleTrailor.getString("type");

                    if (type.equals("Trailer")) {
                        trailor = singleTrailor.getString("source");
                        break;
                    } else
                        trailor = youTubeArray.getJSONObject(0).getString("source");
                }

                trailer = "https://www.youtube.com/watch?v=" + trailor;
            } else
                trailer = null;

            String get_poster_path_from_json = jsonObject.getString("poster_path");
            poster = "http://image.tmdb.org/t/p/w185" + get_poster_path_from_json;
            String get_banner_from_json = jsonObject.getString("backdrop_path");

            Log.d("webi", "banner" + get_banner_from_json);
            if (get_banner_from_json != "null") {
                banner_profile = "http://image.tmdb.org/t/p/w500" + get_banner_from_json;
                banner_for_full_activity = "http://image.tmdb.org/t/p/" + quality + get_banner_from_json;

            } else {
                banner_for_full_activity = "http://image.tmdb.org/t/p/" + quality + get_poster_path_from_json;
                banner_profile = "http://image.tmdb.org/t/p/w500" + get_poster_path_from_json;
            }


            String genre = "";

            JSONArray genreArray = jsonObject.getJSONArray("genres");

            for (int i = 0; i < genreArray.length(); i++) {

                String finalgenre = genreArray.getJSONObject(i).getString("name");

                String punctuation = ", ";

                if (i == genre.length())
                    punctuation = "";

                genre = genre + punctuation + finalgenre;

            }

            movie_desc = overview;
            movie_title = title;
            movie_tagline = tagline;
            show_centre_img_url = banner_for_full_activity;

            movieMap = new HashMap<>();
            movieMap.put("title", movie_title);
            movieMap.put("tagline", tagline);
            movieMap.put("overview", overview);
            movieMap.put("rating", movie_rating);
            movieMap.put("certification", genre);
            movieMap.put("language", language);
            movieMap.put("year", "0");
            movieMap.put("released", released);
            movieMap.put("runtime", runtime);
            movieMap.put("trailer", trailer);
            movieMap.put("banner", banner_profile);
            movieMap.put("poster", poster);
            movieMap.put("id", movie_id_final);


            try {

                img_url = getTrailorImageUrl(jsonObject);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                movieMap.put("img_url", img_url);

                if (databaseApplicable) {

                    MovieDetailsUpdation.performMovieDetailsUpdation(this, type, movieMap, movie_id);

                } else {

                    showParsedContent(movieMap);

                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getTrailorImageUrl(JSONObject jsonObject) throws Exception {

        String img_url;

        if (trailer != null) {

            trailer_boolean = true;
            String videoId = extractYoutubeId(trailer);
            img_url = "http://img.youtube.com/vi/" + videoId + "/0.jpg";


        } else {

            img_url = "http://image.tmdb.org/t/p/w185" + jsonObject.getString("poster_path");

        }

        return img_url;

    }


    private void showParsedContent(HashMap<String, String> movieMap) {


        String title = movieMap.get("title");
        String banner_profile = movieMap.get("banner");
        String img_url = movieMap.get("img_url");
        String tagline = movieMap.get("tagline");
        String overview = movieMap.get("overview");
        String rating = movieMap.get("rating");
        String runtime = movieMap.get("runtime");
        String released = movieMap.get("released");
        String certification = movieMap.get("certification");
        String language = movieMap.get("language");

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
                .diskCacheStrategy(DiskCacheStrategy.NONE)
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
                .diskCacheStrategy(DiskCacheStrategy.NONE)
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

        if (id == MovieLoaders.MOVIE_DETAILS_LOADER) {

            switch (type) {

                case 0:

                    cursorloader = new CursorLoader(this,
                            FilmContract.MoviesEntry.buildMovieWithMovieId(movie_id),
                            MovieSelection.GET_MOVIE_COLUMNS, null, null, null);
                    break;

                case 1:

                    cursorloader = new CursorLoader(this,
                            FilmContract.InTheatersMoviesEntry.buildMovieWithMovieId(movie_id),
                            MovieSelection.GET_MOVIE_COLUMNS, null, null, null);
                    break;

                case 2:

                    cursorloader = new CursorLoader(this,
                            FilmContract.UpComingMoviesEntry.buildMovieWithMovieId(movie_id),
                            MovieSelection.GET_MOVIE_COLUMNS, null, null, null);
                    break;

            }

        } else if (id == MovieLoaders.SAVED_MOVIE_DETAILS_LOADER) {

            final String selection = FilmContract.SaveEntry.TABLE_NAME +
                    "." + FilmContract.SaveEntry.SAVE_ID + " = ? ";
            String[] selectionArgs = {movie_id};

            cursorloader = new CursorLoader(this, FilmContract.SaveEntry.CONTENT_URI,
                    MovieSelection.GET_SAVE_COLUMNS, selection, selectionArgs, null);

        }

        return cursorloader;

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        int id = loader.getId();

        if (id == MovieLoaders.MOVIE_DETAILS_LOADER) {

            fetchMovieDetailsFromCursor(data);

        } else if (id == MovieLoaders.SAVED_MOVIE_DETAILS_LOADER) {

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
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
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
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
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
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
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
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
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

            case R.id.action_share:

                shareMovie();

                break;

            case R.id.action_save:

                OfflineMovies offlineMovies = new OfflineMovies(this, main_content);
                offlineMovies.saveMovie(movieMap, movie_id, movie_id_final);

                break;

        }

        return super.onOptionsItemSelected(item);
    }


    private void shareMovie() {


        movie_trailer = "http://www.imdb.com/title/" + movie_id_final;
        if (!(movie_title.equals(null) && movie_rating.equals("null") && movie_id_final.equals("null"))) {
            Intent myIntent = new Intent(Intent.ACTION_SEND);
            myIntent.setType("text/plain");
            myIntent.putExtra(Intent.EXTRA_TEXT, "*" + movie_title + "*\n" + movie_tagline + "\nRating: " + movie_rating + " / 10\n" + movie_trailer + "\n");
            startActivity(Intent.createChooser(myIntent, "Share with"));
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


    @Override
    public void dataFetched(String response, int code) {

        switch (code) {

            case GetDataFromNetwork.MOVIE_DETAILS_CODE:

                parseMovieDetails(response);
                break;
        }

    }

}