package tech.salroid.filmy.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
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
import com.google.android.youtube.player.YouTubeStandalonePlayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.salroid.filmy.R;
import tech.salroid.filmy.animations.RevealAnimation;
import tech.salroid.filmy.customs.BreathingProgress;
import tech.salroid.filmy.database.FilmContract;
import tech.salroid.filmy.database.MovieDetailsUpdation;
import tech.salroid.filmy.database.MovieLoaders;
import tech.salroid.filmy.database.MovieProjection;
import tech.salroid.filmy.database.OfflineMovies;
import tech.salroid.filmy.fragment.AllTrailerFragment;
import tech.salroid.filmy.fragment.CastFragment;
import tech.salroid.filmy.fragment.CrewFragment;
import tech.salroid.filmy.fragment.FullReadFragment;
import tech.salroid.filmy.fragment.SimilarFragment;
import tech.salroid.filmy.network_stuff.GetDataFromNetwork;
import tech.salroid.filmy.tmdb_account.MarkingFavorite;
import tech.salroid.filmy.tmdb_account.MarkingWatchList;
import tech.salroid.filmy.utility.Confirmation;
import tech.salroid.filmy.utility.NullChecker;

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

public class MovieDetailsActivity extends AppCompatActivity implements
        View.OnClickListener,
        LoaderManager.LoaderCallbacks<Cursor>,
        GetDataFromNetwork.DataFetchedListener,
        CastFragment.GotCrewListener {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.detail_title)
    TextView det_title;
    @BindView(R.id.detail_tagline)
    TextView det_tagline;
    @BindView(R.id.detail_overview)
    TextView det_overview;
    @BindView(R.id.tmdbRating)
    TextView det_rating;
    @BindView(R.id.tomatoRating)
    TextView tomato_rating;
    @BindView(R.id.flixterRating)
    TextView flixter_rating;
    @BindView(R.id.metaRating)
    TextView meta_rating;
    @BindView(R.id.imdbRating)
    TextView rating_of_imdb;
    @BindView(R.id.metaRatingView)
    TextView metascore_setter;
    @BindView(R.id.detail_released)
    TextView det_released;
    @BindView(R.id.detail_certification)
    TextView det_certification;
    @BindView(R.id.detail_runtime)
    TextView det_runtime;
    @BindView(R.id.detail_language)
    TextView det_language;
    @BindView(R.id.detail_youtube)
    ImageView youtube_link;
    @BindView(R.id.backdrop)
    ImageView banner;
    @BindView(R.id.play_button)
    ImageView youtube_play_button;
    @BindView(R.id.tomatoRating_image)
    ImageView tomatoRating_image;
    @BindView(R.id.flixterRating_image)
    ImageView flixterRating_image;
    @BindView(R.id.breathingProgress)
    BreathingProgress breathingProgress;
    @BindView(R.id.trailorBackground)
    LinearLayout trailorBackground;

    @BindView(R.id.youtube_icon)
    ImageView youtubeIcon;

    @BindView(R.id.trailorView)
    FrameLayout trailorView;
    @BindView(R.id.new_main)
    FrameLayout newMain;
    @BindView(R.id.all_details_container)
    FrameLayout main_content;
    @BindView(R.id.header_container)
    FrameLayout headerContainer;
    @BindView(R.id.main)
    RelativeLayout main;
    @BindView(R.id.metaRating_background)
    RelativeLayout metaRating_background;
    @BindView(R.id.header)
    LinearLayout header;

    @BindView(R.id.extraDetails)
    RelativeLayout extraDetails;

    @BindView(R.id.ratingBar)
    RelativeLayout ratingBar;

    @BindView(R.id.cast_divider)
    View castDivider;

    @BindView(R.id.layout_imdb)
    LinearLayout layout_imdb;

    @BindView(R.id.layout_flixi)
    LinearLayout layout_flixi;

    @BindView(R.id.layout_meta)
    LinearLayout layout_meta;

    @BindView(R.id.layout_tmdb)
    LinearLayout layout_tmdb;

    @BindView(R.id.layout_tomato)
    LinearLayout layout_tomato;


    Context context = this;
    String[] trailer_array;
    String [] trailer_array_name;
    FullReadFragment fullReadFragment;
    AllTrailerFragment allTrailerFragment;
    HashMap<String, String> movieMap;
    boolean networkApplicable, databaseApplicable, savedDatabaseApplicable, trailer_boolean = false;
    int type;
    private String movie_id;
    private String trailor = null;
    private String trailer = null;
    private String movie_desc;
    private String quality;
    private String movie_tagline;
    private String movie_rating;
    private String movie_rating_tmdb;
    private String show_centre_img_url;
    private String movie_title;
    private String movie_id_final;

    private CastFragment castFragment;
    private boolean nightMode;
    private String movie_imdb_id;
    private CrewFragment crewFragment;
    private SimilarFragment similarFragment;
    private String movie_rating_audience;
    private String movie_rating_metascore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        nightMode = sp.getBoolean("dark", false);
        if (nightMode)
            setTheme(R.style.DetailsActivityThemeDark);
        else
            setTheme(R.style.DetailsActivityTheme);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);
        ButterKnife.bind(this);

        if (!nightMode)
            allThemeLogic();
        else {
            nightModeLogic();
            castDivider.setVisibility(View.GONE);
        }

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        SharedPreferences prefrence = PreferenceManager.getDefaultSharedPreferences(MovieDetailsActivity.this);
        quality = prefrence.getString("image_quality", "w1000");

        headerContainer.setOnClickListener(this);
        newMain.setOnClickListener(this);
        trailorView.setOnClickListener(this);
        youtubeIcon.setOnClickListener(this);


        Intent intent = getIntent();
        getDataFromIntent(intent);

        if (savedInstanceState == null) {
            RevealAnimation.performReveal(main_content);
            performDataFetching();
        }
        showCastFragment();
        showCrewFragment();
        showSimilarFragment();

    }

    private void nightModeLogic() {

        main_content.setBackgroundColor(Color.parseColor("#212121"));
        headerContainer.setBackgroundColor(Color.parseColor("#212121"));
        extraDetails.setBackgroundColor(Color.parseColor("#212121"));
        ratingBar.setBackgroundColor(Color.parseColor("#212121"));

    }

    private void allThemeLogic() {

        main_content.setBackgroundColor(Color.parseColor("#f5f5f5"));
        headerContainer.setBackgroundColor(getResources().getColor(R.color.primaryColor));
        extraDetails.setBackgroundColor(getResources().getColor(R.color.primaryColor));
        ratingBar.setBackgroundColor(getResources().getColor(R.color.primaryColor));
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean nightModeNew = sp.getBoolean("dark", false);
        if (nightMode != nightModeNew)
            recreate();


        RevealAnimation.performReveal(main_content);
        performDataFetching();

        showCastFragment();
        showCrewFragment();
        showSimilarFragment();
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

        castFragment = CastFragment.newInstance(null, movie_title);

        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.cast_container, castFragment)
                .commit();

        castFragment.setGotCrewListener(this);
    }


    private void showCrewFragment() {

        crewFragment = CrewFragment.newInstance(null, movie_title);

        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.crew_container, crewFragment)
                .commit();
    }

    private void showSimilarFragment() {
        similarFragment = SimilarFragment.newInstance(null, movie_title);

        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.similar_container, similarFragment)
                .commit();
    }

    void parseMovieDetails(String movieDetails) {

        String title, tagline, overview, banner_profile, runtime, language, released,
                poster, img_url = null, get_poster_path_from_json, get_banner_from_json;

        try {

            JSONObject jsonObject = new JSONObject(movieDetails);
            title = jsonObject.getString("title");
            tagline = jsonObject.getString("tagline");
            overview = jsonObject.getString("overview");
            released = jsonObject.getString("release_date");
            runtime = jsonObject.getString("runtime") + " mins";
            language = jsonObject.getString("original_language");

            movie_id_final = jsonObject.getString("id");
            movie_imdb_id = jsonObject.getString("imdb_id");

            movie_rating_tmdb = jsonObject.getString("vote_average");

            if (!(tagline.equals("")))
                det_tagline.setVisibility(View.VISIBLE);

            if (castFragment != null)
                castFragment.getCastFromNetwork(movie_id_final);

            if (similarFragment != null)
                similarFragment.getSimilarFromNetwork(movie_id_final);

            Rating.getRating(context, movie_imdb_id);

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

            trailer_array = new String[youTubeArray.length()];
            trailer_array_name = new String[youTubeArray.length()];

            if (youTubeArray.length() != 0) {
                Boolean main_trailer = true;
                for (int i = 0; i < youTubeArray.length(); i++) {

                    JSONObject singleTrailor = youTubeArray.getJSONObject(i);
                    trailer_array[i] = singleTrailor.getString("source");
                    trailer_array_name[i] = singleTrailor.getString("name");


                    String type = singleTrailor.getString("type");
                    if (main_trailer) {
                        if (type.equals("Trailer")) {
                            trailor = singleTrailor.getString("source");
                            main_trailer = false;
                        } else
                            trailor = youTubeArray.getJSONObject(0).getString("source");
                    }
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

            movieMap = new HashMap<>();
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
                    //  String videoId = extractYoutubeId(trailer);
                    img_url = getResources().getString(R.string.trailer_img_prefix) + trailor
                            + getResources().getString(R.string.trailer_img_suffix);

                } else {
                    img_url = getResources().getString(R.string.poster_prefix_185) + jsonObject.getString("poster_path");

                }
                movieMap.put("trailer_img", img_url);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                if (databaseApplicable)
                    MovieDetailsUpdation.performMovieDetailsUpdation(MovieDetailsActivity.this, type, movieMap, movie_id);
                else
                    showParsedContent(title, banner_profile, img_url, tagline, overview, movie_rating, runtime, released, genre, language);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void showParsedContent(String title, String banner_profile, String img_url, String tagline,
                                   String overview, String rating, String runtime,
                                   String released, String certification, String language) {

        det_tagline.setText(tagline);
        det_title.setText(title);
        det_overview.setText(overview);
        // det_rating.setText(rating);
        det_runtime.setText(runtime);
        det_released.setText(released);
        det_certification.setText(certification);
        det_language.setText(language);

        try {
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
                                        youtubeIcon.setColorFilter(trailorSwatch.getBodyTextColor(), PorterDuff.Mode.SRC_IN);
                                    }
                                }
                            });
                        }
                    });

        } catch (Exception e) {
            //Log.d(LOG_TAG, e.getMessage());
        }
        try {

            Glide.with(context)
                    .load(img_url).asBitmap().diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            youtube_link.setImageBitmap(resource);
                            if (trailer_boolean)
                                youtube_play_button.setVisibility(View.VISIBLE);
                        }

                    });
        } catch (Exception e) {
            //Log.d(LOG_TAG, e.getMessage());
        }
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
                            MovieProjection.GET_MOVIE_COLUMNS, null, null, null);
                    break;

                case 1:
                    cursorloader = new CursorLoader(this,
                            FilmContract.InTheatersMoviesEntry.buildMovieWithMovieId(movie_id),
                            MovieProjection.GET_MOVIE_COLUMNS, null, null, null);
                    break;

                case 2:
                    cursorloader = new CursorLoader(this,
                            FilmContract.UpComingMoviesEntry.buildMovieWithMovieId(movie_id),
                            MovieProjection.GET_MOVIE_COLUMNS, null, null, null);
                    break;

            }

        } else if (id == MovieLoaders.SAVED_MOVIE_DETAILS_LOADER) {

            final String selection = FilmContract.SaveEntry.TABLE_NAME +
                    "." + FilmContract.SaveEntry.SAVE_ID + " = ? ";
            String[] selectionArgs = {movie_id};

            cursorloader = new CursorLoader(this, FilmContract.SaveEntry.CONTENT_URI,
                    MovieProjection.GET_SAVE_COLUMNS, selection, selectionArgs, null);

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

            String rating = (data.getString(rating_index));
            String runtime = data.getString(runtime_index);
            String released = data.getString(released_index);
            String certification = data.getString(certification_index);
            String language = data.getString(language_index);

            movie_id_final = data.getString(id_index);

            det_tagline.setText(tagline);
            det_title.setText(title);
            det_overview.setText(overview);
            // det_rating.setText(rating);
            det_runtime.setText(runtime);
            det_released.setText(released);
            det_certification.setText(certification);
            det_language.setText(language);

            movie_desc = overview;
            show_centre_img_url = banner_url;

            try {

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
                                            youtubeIcon.setColorFilter(trailorSwatch.getBodyTextColor(), PorterDuff.Mode.SRC_IN);
                                        }
                                    }
                                });

                            }
                        });
            } catch (Exception e) {
                //Log.d(LOG_TAG, e.getMessage());
            }

            String thumbNail = null;
            if ((trailor != null)) {
                trailer_boolean = true;
                thumbNail = getResources().getString(R.string.trailer_img_prefix) + trailor
                        + getResources().getString(R.string.trailer_img_prefix);
            } else {
                thumbNail = posterLink;
            }

            try {

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

            } catch (Exception e) {
                //Log.d(LOG_TAG, e.getMessage());
            }
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

            if (runtime != null && !runtime.equals("null mins"))
                det_runtime.setText(runtime);

            if (NullChecker.isSettable(released))
                det_released.setText(released);

            if (NullChecker.isSettable(certification))
                det_certification.setText(certification);

            if (NullChecker.isSettable(language))
                det_language.setText(language);

            try {
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
                                            youtubeIcon.setColorFilter(trailorSwatch.getBodyTextColor(), PorterDuff.Mode.SRC_IN);
                                        }
                                    }
                                });

                            }
                        });
            } catch (Exception e) {
                //Log.d(LOG_TAG, e.getMessage());
            }

            try {

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
            } catch (Exception e) {
                //Log.d(LOG_TAG, e.getMessage());
            }
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

                if (type == -1)
                    startActivity(new Intent(this, MainActivity.class));
                break;

            case R.id.action_share:
                shareMovie();
                break;

            case R.id.action_save:
                OfflineMovies offlineMovies = new OfflineMovies(this, main_content);
                offlineMovies.saveMovie(movieMap, movie_id, movie_id_final);
                break;

            case R.id.action_fav:

                Confirmation.confirmFav(this,movie_id);

                break;

            case R.id.action_watch:

                Confirmation.confirmWatchlist(this,movie_id);

                break;

            default:
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (getFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {
            getFragmentManager().popBackStack();
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
                            .replace(R.id.all_details_container, fullReadFragment).addToBackStack("DESC").commit();
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
                    startActivity(YouTubeStandalonePlayer.createVideoIntent(MovieDetailsActivity.this,
                            getString(R.string.Youtube_Api_Key), trailor));

                break;
            case R.id.youtube_icon:
                if (trailer_boolean) {
                    allTrailerFragment = new AllTrailerFragment();
                   // Log.d(TAG, "onClick: "+ Arrays.toString(trailer_array));
                    Bundle args = new Bundle();
                    args.putString("title", movie_title);
                    args.putStringArray("trailers",trailer_array);
                    args.putStringArray("trailers_name",trailer_array_name);
                    allTrailerFragment.setArguments(args);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.all_details_container, allTrailerFragment).addToBackStack("TRAILER").commit();
                }
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
        String movie_imdb = getResources().getString(R.string.imdb_link_prefix) + movie_imdb_id;
        if (!(movie_title == null && movie_rating.equals("null") && movie_imdb_id.equals("null"))) {
            Intent myIntent = new Intent(Intent.ACTION_SEND);
            myIntent.setType("text/plain");
            myIntent.putExtra(Intent.EXTRA_TEXT, "*" + movie_title + "*\n" + movie_tagline + "\nRating: " + movie_rating + " / 10\n" + movie_imdb + "\n");
            startActivity(Intent.createChooser(myIntent, "Share with"));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        castFragment = null;
        similarFragment = null;

    }

    @Override
    public void gotCrew(String crewData) {

        if (crewFragment != null)
            crewFragment.crew_parseOutput(crewData);
    }

    public void setRating(String movie_rating_imdb, String movie_rating_tomatometer,
                          String audience_rating, String metascore_rating, String image) {

        movie_rating_audience = audience_rating;
        movie_rating_metascore = metascore_rating;

        if (movie_rating_imdb.equals("N/A"))
            layout_imdb.setVisibility(View.GONE);
        else
            rating_of_imdb.setText(movie_rating_imdb);


        if (movie_rating_tomatometer.equals("N/A"))
            layout_tomato.setVisibility(View.GONE);
        else {
            if (image.equals("certified"))
                tomatoRating_image.setImageDrawable(getResources().getDrawable(R.drawable.certified));
            else if (image.equals("fresh"))
                tomatoRating_image.setImageDrawable(getResources().getDrawable(R.drawable.fresh));
            else if (image.equals("rotten"))
                tomatoRating_image.setImageDrawable(getResources().getDrawable(R.drawable.rotten));
            tomato_rating.setText(movie_rating_tomatometer);
        }

        if (movie_rating_audience.equals("N/A"))
            layout_flixi.setVisibility(View.GONE);

        else {

            float audi_rating = Float.valueOf(audience_rating);

            if (audi_rating > 3.4)
                flixterRating_image.setImageDrawable(getResources().getDrawable(R.drawable.popcorn));
            else
                flixterRating_image.setImageDrawable(getResources().getDrawable(R.drawable.spilt));

            flixter_rating.setText(movie_rating_audience);
        }

        if (movie_rating_metascore.equals("N/A"))
            layout_meta.setVisibility(View.GONE);

        else {

            int metasco_rating = Integer.valueOf(metascore_rating);

            if (metasco_rating > 60)
                metaRating_background.setBackgroundColor(Color.parseColor("#66cc33"));
            else if (metasco_rating > 40 && metasco_rating < 61)
                metaRating_background.setBackgroundColor(Color.parseColor("#ffcc33"));
            else
                metaRating_background.setBackgroundColor(Color.parseColor("#ff0000"));


            meta_rating.setText(movie_rating_metascore);
            metascore_setter.setText(movie_rating_metascore);

        }

        if (movie_rating_tmdb.equals("0"))
            layout_tmdb.setVisibility(View.GONE);

        else
            det_rating.setText(movie_rating_tmdb);


    }

}