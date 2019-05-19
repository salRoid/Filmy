package tech.salroid.filmy.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.salroid.filmy.BuildConfig;
import tech.salroid.filmy.R;
import tech.salroid.filmy.activities.CharacterDetailsActivity;
import tech.salroid.filmy.activities.FullCastActivity;
import tech.salroid.filmy.custom_adapter.CastAdapter;
import tech.salroid.filmy.customs.BreathingProgress;
import tech.salroid.filmy.data_classes.CastDetailsData;
import tech.salroid.filmy.network_stuff.TmdbVolleySingleton;
import tech.salroid.filmy.parser.MovieDetailsActivityParseWork;
/*
 * Filmy Application for Android
 * Copyright (c) 2016 Ramankit Singh (http://github.com/webianks).
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


public class CastFragment extends Fragment implements View.OnClickListener, CastAdapter.ClickListener {


    @BindView(R.id.more)
    TextView more;
    @BindView(R.id.cast_recycler)
    RecyclerView cast_recycler;
    @BindView(R.id.card_holder)
    TextView card_holder;
    @BindView(R.id.breathingProgressFragment)
    BreathingProgress breathingProgress;
    @BindView(R.id.detail_fragment_views_layout)
    RelativeLayout relativeLayout;
    private String jsonCast;
    private String movieId, movieTitle;
    private GotCrewListener gotCrewListener;

    public static CastFragment newInstance(String movieId, String movieTitle) {
        CastFragment fragment = new CastFragment();
        Bundle args = new Bundle();
        args.putString("movie_id", movieId);
        args.putString("movie_title", movieTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.cast_fragment, container, false);
        ButterKnife.bind(this, view);

        cast_recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        cast_recycler.setNestedScrollingEnabled(false);
        cast_recycler.setVisibility(View.INVISIBLE);
        more.setOnClickListener(this);

        return view;
    }

    public void setGotCrewListener(GotCrewListener gotCrewListener) {
        this.gotCrewListener = gotCrewListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle savedBundle = getArguments();

        if (savedBundle != null) {
            movieId = savedBundle.getString("movie_id");
            movieTitle = savedBundle.getString("movie_title");
        }

        if (movieId != null) getCastFromNetwork(movieId);
    }

    public void getCastFromNetwork(String movieId) {

        String TMBD_API_KEY = BuildConfig.TMDB_API_KEY;
        final String BASE_MOVIE_CAST_DETAILS = "http://api.themoviedb.org/3/movie/" + movieId + "/casts?api_key=" + TMBD_API_KEY;
        JsonObjectRequest jsonObjectRequestForMovieCastDetails = new JsonObjectRequest(BASE_MOVIE_CAST_DETAILS, null,
                response -> {
                    jsonCast = response.toString();
                    parseCastOutput(response.toString());

                    if (gotCrewListener != null) gotCrewListener.gotCrew(response.toString());

                }, error -> {

                    Log.e("webi", "Volley Error: " + error.getCause());
                    breathingProgress.setVisibility(View.GONE);
                }
        );

        TmdbVolleySingleton volleySingleton = TmdbVolleySingleton.getInstance();
        RequestQueue requestQueue = volleySingleton.getRequestQueue();
        requestQueue.add(jsonObjectRequestForMovieCastDetails);
    }

    private void parseCastOutput(String castResult) {

        MovieDetailsActivityParseWork par = new MovieDetailsActivityParseWork(getActivity(), castResult);
        List<CastDetailsData> castList = par.parse_cast();
        CastAdapter cast_adapter = new CastAdapter(getActivity(), castList, true);
        cast_adapter.setClickListener(this);
        cast_recycler.setAdapter(cast_adapter);

        if (castList.size() > 4) {
            more.setVisibility(View.VISIBLE);
        } else if (castList.size() == 0) {
            more.setVisibility(View.INVISIBLE);
            card_holder.setVisibility(View.INVISIBLE);
        } else {
            more.setVisibility(View.INVISIBLE);
        }

        breathingProgress.setVisibility(View.GONE);
        cast_recycler.setVisibility(View.VISIBLE);
        relativeLayout.setMinimumHeight(0);
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.more) {
            Log.d("webi", "" + movieTitle);
            if (jsonCast != null && movieTitle != null) {
                Intent intent = new Intent(getActivity(), FullCastActivity.class);
                intent.putExtra("cast_json", jsonCast);
                intent.putExtra("toolbar_title", movieTitle);
                startActivity(intent);
            }
        }
    }

    @Override
    public void itemClicked(CastDetailsData setterGetter, int position, View view) {
        Intent intent = new Intent(getActivity(), CharacterDetailsActivity.class);
        intent.putExtra("id", setterGetter.getCastId());

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            Pair<View, String> p1 = Pair.create(view.findViewById(R.id.cast_poster), "profile");
            Pair<View, String> p2 = Pair.create(view.findViewById(R.id.cast_name), "name");

            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(getActivity(), p1, p2);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }

    public interface GotCrewListener {
        void gotCrew(String crewData);
    }
}