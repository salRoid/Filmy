package tech.salroid.filmy.activities;

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

import tech.salroid.filmy.R;
import tech.salroid.filmy.animations.RevealAnimation;
import tech.salroid.filmy.customs.BreathingProgress;
import tech.salroid.filmy.databases.FilmContract;
import tech.salroid.filmy.databases.MovieDetailsUpdation;
import tech.salroid.filmy.databases.MovieLoaders;
import tech.salroid.filmy.databases.MovieSelection;
import tech.salroid.filmy.databases.OfflineMovies;
import tech.salroid.filmy.fragment.CastFragment;
import tech.salroid.filmy.fragment.FullReadFragment;
import tech.salroid.filmy.network_stuff.GetDataFromNetwork;
import tech.salroid.filmy.utility.NullChecker;


public class MovieDetailsActivity extends AppCompatActivity implements
        View.OnClickListener,
        LoaderManager.LoaderCallbacks<Cursor>, GetDataFromNetwork.DataFetchedListener {

    static String movie_title, movie_id_final;
    private static TextView det_title, det_tagline, det_overview,
            det_rating, det_released, det_certification,
            det_language, det_runtime, tvRating;
    private static ImageView youtube_link, banner, youtube_play_button;
    Context context = this;
    BreathingProgress breathingProgress;
    LinearLayout trailorBackground;
    FrameLayout trailorView, newMain, headerContainer, main_content, allDetails;
    FullReadFragment fullReadFragment;
    HashMap<String, String> movieMap;
    boolean networkApplicable, databaseApplicable, savedDatabaseApplicable, trailer_boolean = false;
    int type;
    private String movie_id;
    private String trailer = null;
    private String movie_desc;
    private String quality;
    private RelativeLayout header, main;
    private String movie_tagline;
    private String movie_rating;
    private String show_centre_img_url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        det_title = (TextView) findViewById(R.id.detail_title);
        det_tagline = (TextView) findViewById(R.id.detail_tagline);
        det_overview = (TextView) findViewById(R.id.detail_overview);
        det_rating = (TextView) findViewById(R.id.detail_rating);
        tvRating = (TextView) findViewById(R.id.tvRating);
        det_released = (TextView) findViewById(R.id.detail_released);
        det_certification = (TextView) findViewById(R.id.detail_certification);
        det_runtime = (TextView) findViewById(R.id.detail_runtime);
        det_language = (TextView) findViewById(R.id.detail_language);

        youtube_link = (ImageView) findViewById(R.id.detail_youtube);
        banner = (ImageView) findViewById(R.id.backdrop);
        youtube_play_button = (ImageView) findViewById(R.id.play_button);

        trailorBackground = (LinearLayout) findViewById(R.id.trailorBackground);

        trailorView = (FrameLayout) findViewById(R.id.trailorView);
        newMain = (FrameLayout) findViewById(R.id.new_main);
        main_content = (FrameLayout) findViewById(R.id.all_details_container);
        headerContainer = (FrameLayout) findViewById(R.id.header_container);

        breathingProgress = (BreathingProgress) findViewById(R.id.breathingProgress);

        main = (RelativeLayout) findViewById(R.id.main);
        header = (RelativeLayout) findViewById(R.id.header);

        SharedPreferences prefrence = PreferenceManager.getDefaultSharedPreferences(MovieDetailsActivity.this);
        quality = prefrence.getString("image_quality", "w780");

        headerContainer.setOnClickListener(this);
        newMain.setOnClickListener(this);
        trailorView.setOnClickListener(this);

        Intent intent = getIntent();
        getDataFromIntent(intent);

        if (savedInstanceState == null)
            RevealAnimation.performReveal(allDetails);

    }

    @Override
    protected void onResume() {
        super.onResume();
        performDataFetching();
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

    private void showCastFragment() {
        CastFragment castFragment = CastFragment.newInstance(movie_id_final, movie_title);
        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.cast_container, castFragment)
                .commit();

    }

    void parseMovieDetails(String movieDetails) {

        String title, tagline, overview, banner_profile, runtime, language, released,
                poster, img_url = null, trailor = null, get_poster_path_from_json, get_banner_from_json;

        try {

            JSONObject jsonObject = new JSONObject(movieDetails);

            title = jsonObject.getString("title");
            tagline = jsonObject.getString("tagline");
            overview = jsonObject.getString("overview");
            released = jsonObject.getString("release_date");
            runtime = jsonObject.getString("runtime") + " mins";
            language = jsonObject.getString("original_language");

            movie_id_final = jsonObject.getString("imdb_id");
            showCastFragment();

            movie_rating = jsonObject.getString("vote_average");

            if (movie_rating.equals("0")) {
                movie_rating = "N.A";
            }

            //poster and banner
            get_poster_path_from_json = jsonObject.getString("poster_path");
            get_banner_from_json = jsonObject.getString("backdrop_path");

            poster = getResources().getString(R.string.poster_prefix_185) + get_poster_path_from_json;

            String banner_for_full_activity;
            String poster_prefix_500 = getResources().getString(R.string.poster_prefix_500);
            String poster_prefix_add_quality = getResources().getString(R.string.poster_prefix_add_quality);

            if (!get_banner_from_json.equals("null")) {
                banner_profile = poster_prefix_500 + get_banner_from_json;
                banner_for_full_activity = poster_prefix_add_quality + quality + get_banner_from_json;

            } else {
                banner_profile = poster_prefix_500 + get_poster_path_from_json;
                banner_for_full_activity = poster_prefix_add_quality + quality + get_poster_path_from_json;
            }

            //trailer

            JSONObject trailorsObject = jsonObject.getJSONObject("trailers");
            JSONArray youTubeArray = trailorsObject.getJSONArray("youtube");

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

                trailer = getResources().getString(R.string.trailer_link_prefix) + trailor;
            } else
                trailer = null;


            //genre
            String genre = "";

            JSONArray genreArray = jsonObject.getJSONArray("genres");

            for (int i = 0; i < genreArray.length(); i++) {

                if (i > 3)
                    break;

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

            movieMap = new HashMap<String, String>();
            movieMap.put("imdb_id", movie_id_final);
            movieMap.put("title", movie_title);
            movieMap.put("tagline", tagline);
            movieMap.put("overview", overview);
            movieMap.put("rating", movie_rating);
            movieMap.put("certification", genre);
            movieMap.put("language", language);
            movieMap.put("released", released);
            movieMap.put("runtime", runtime);
            movieMap.put("trailer", trailer);
            movieMap.put("banner", banner_profile);
            movieMap.put("poster", poster);


            try {
                if (trailor != null) {
                    trailer_boolean = true;
                    String videoId = extractYoutubeId(trailer);
                    img_url = getResources().getString(R.string.trailer_img_prefix) + videoId
                            + getResources().getString(R.string.trailer_img_suffix);

                } else {
                    img_url = getResources().getString(R.string.poster_prefix_185) + jsonObject.getString("poster_path");

                }
                movieMap.put("trailer_img", img_url);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } finally {

                if (databaseApplicable) {
                    MovieDetailsUpdation.performMovieDetailsUpdation(MovieDetailsActivity.this, type, movieMap, movie_id);
                } else {

                    showParsedContent(title, banner_profile, img_url, tagline, overview, movie_rating, runtime, released, genre, language);

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
        det_runtime.setText(runtime);
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
            int id_index = data.getColumnIndex(FilmContract.SaveEntry.SAVE_ID);
            int poster_link_index = data.getColumnIndex(FilmContract.SaveEntry.SAVE_POSTER_LINK);

            String title = data.getString(title_index);
            String banner_url = data.getString(banner_index);
            String tagline = data.getString(tagline_index);
            String overview = data.getString(description_index);

            //as it will be used to show it on YouTube
            trailer = data.getString(trailer_index);
            String posterLink = data.getString(poster_link_index);

            String rating = (data.getString(rating_index)) + " mins";
            String runtime = data.getString(runtime_index);
            String released = data.getString(released_index);
            String certification = data.getString(certification_index);
            String language = data.getString(language_index);

            movie_id_final = data.getString(id_index);

            det_title.setText(title);
            det_tagline.setText(tagline);
            det_overview.setText(overview);
            det_rating.setText(rating);
            det_runtime.setText(runtime);
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
            if ((trailer != null)) {
                trailer_boolean = true;
                try {
                    String videoId = extractYoutubeId(trailer);
                    thumbNail = getResources().getString(R.string.trailer_img_prefix) + videoId
                            + getResources().getString(R.string.trailer_img_prefix);
                } catch (Exception e) {

                    //whatever you want to do
                }
            } else {
                thumbNail = posterLink;
            }


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

            if (NullChecker.isSettable(title))
                det_title.setText(title);

            if (NullChecker.isSettable(tagline))
                det_tagline.setText(tagline);

            if (NullChecker.isSettable(overview))
                det_overview.setText(overview);
            if (NullChecker.isSettable(rating))
                det_rating.setText(rating);

            if (runtime != null && !runtime.equals("null mins"))
                det_runtime.setText(runtime);

            if (NullChecker.isSettable(released))
                det_released.setText(released);

            if (NullChecker.isSettable(certification))
                det_certification.setText(certification);

            if (NullChecker.isSettable(language))
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
                // showCastFragment();
                break;

            case GetDataFromNetwork.CAST_CODE:


                break;

        }

    }

    private void shareMovie() {
        String movie_imdb = getResources().getString(R.string.imdb_link_prefix) + movie_id_final;
        if (!(movie_title == null && movie_rating.equals("null") && movie_id_final.equals("null"))) {
            Intent myIntent = new Intent(Intent.ACTION_SEND);
            myIntent.setType("text/plain");
            myIntent.putExtra(Intent.EXTRA_TEXT, "*" + movie_title + "*\n" + movie_tagline + "\nRating: " + movie_rating + " / 10\n" + movie_imdb + "\n");
            startActivity(Intent.createChooser(myIntent, "Share with"));
        }

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


}