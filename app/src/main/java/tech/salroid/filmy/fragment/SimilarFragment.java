package tech.salroid.filmy.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.salroid.filmy.BuildConfig;
import tech.salroid.filmy.R;
import tech.salroid.filmy.activities.MovieDetailsActivity;
import tech.salroid.filmy.custom_adapter.SimilarMovieActivityAdapter;
import tech.salroid.filmy.customs.BreathingProgress;
import tech.salroid.filmy.data_classes.SimilarMoviesData;
import tech.salroid.filmy.network_stuff.TmdbVolleySingleton;
import tech.salroid.filmy.parser.MovieDetailsActivityParseWork;

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

public class SimilarFragment extends Fragment implements SimilarMovieActivityAdapter.ClickListener {

    @BindView(R.id.similar_recycler)
    RecyclerView similar_recycler;
    @BindView(R.id.breathingProgressFragment)
    BreathingProgress breathingProgress;
    @BindView(R.id.card_holder)
    TextView card_holder;
    @BindView(R.id.detail_fragment_views_layout)
    RelativeLayout relativeLayout;
    private String similar_json;
    private String movieId, movieTitle;

    public static SimilarFragment newInstance(String movie_Id, String movie_Title) {
        SimilarFragment fragment = new SimilarFragment();
        Bundle args = new Bundle();
        args.putString("movie_id", movie_Id);
        args.putString("movie_title", movie_Title);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.similar_fragment, container, false);
        ButterKnife.bind(this, view);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        similar_recycler.setLayoutManager(llm);
        similar_recycler.setNestedScrollingEnabled(false);

        similar_recycler.setVisibility(View.INVISIBLE);

        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle savedBundle = getArguments();

        if (savedBundle != null) {
            movieId = savedBundle.getString("movie_id");
            movieTitle = savedBundle.getString("movie_title");
        }

        if (movieId != null)
            getSimilarFromNetwork(movieId);

    }


    public void getSimilarFromNetwork(String movieId) {

        String api_key = BuildConfig.TMDB_API_KEY;
        final String BASE_MOVIE_CAST_DETAILS = new String(" https://api.themoviedb.org/3/movie/" + movieId + "/recommendations?api_key="+ api_key);
        JsonObjectRequest jsonObjectRequestForMovieCastDetails = new JsonObjectRequest(BASE_MOVIE_CAST_DETAILS, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        similar_json = response.toString();
                        similar_parseOutput(response.toString());

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("webi", "Volley Error: " + error.getCause());

                breathingProgress.setVisibility(View.GONE);

            }
        }
        );


        TmdbVolleySingleton volleySingleton = TmdbVolleySingleton.getInstance();
        RequestQueue requestQueue = volleySingleton.getRequestQueue();
        requestQueue.add(jsonObjectRequestForMovieCastDetails);
    }


    private void similar_parseOutput(String similar_result) {

        MovieDetailsActivityParseWork par = new MovieDetailsActivityParseWork(getActivity(), similar_result);

        List<SimilarMoviesData> similar_list = par.parse_similar_movies();

        SimilarMovieActivityAdapter similar_adapter = new SimilarMovieActivityAdapter(getActivity(), similar_list, true);
        similar_adapter.setClickListener(this);
        similar_recycler.setAdapter(similar_adapter);

        if (similar_list.size() == 0) {
            card_holder.setVisibility(View.INVISIBLE);
        }

        breathingProgress.setVisibility(View.GONE);
        similar_recycler.setVisibility(View.VISIBLE);

        relativeLayout.setMinimumHeight(0);
    }

    @Override
    public void itemClicked(SimilarMoviesData setterGetter, int position, View view) {
        Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
        intent.putExtra("title", setterGetter.getMovie_title());
        intent.putExtra("id", setterGetter.getMovie_id());
        intent.putExtra("network_applicable", true);
        intent.putExtra("activity", false);

        startActivity(intent);

    }
}
