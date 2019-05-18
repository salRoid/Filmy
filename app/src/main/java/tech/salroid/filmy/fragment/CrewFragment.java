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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.salroid.filmy.R;
import tech.salroid.filmy.activities.CharacterDetailsActivity;
import tech.salroid.filmy.activities.FullCrewActivity;
import tech.salroid.filmy.custom_adapter.CrewAdapter;
import tech.salroid.filmy.customs.BreathingProgress;
import tech.salroid.filmy.data_classes.CrewDetailsData;
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

public class CrewFragment extends Fragment implements View.OnClickListener, CrewAdapter.ClickListener {

    @BindView(R.id.crew_more)
    TextView more;
    @BindView(R.id.crew_recycler)
    RecyclerView crew_recycler;
    @BindView(R.id.card_holder)
    TextView card_holder;
    @BindView(R.id.breathingProgressFragment)
    BreathingProgress breathingProgress;
    @BindView(R.id.detail_fragment_views_layout)
    RelativeLayout relativeLayout;
    private String jsonCrew;
    private String movieId, movieTitle;

    public static CrewFragment newInstance(String movieId, String movieTitle) {

        CrewFragment fragment = new CrewFragment();
        Bundle args = new Bundle();
        args.putString("movie_id", movieId);
        args.putString("movie_title", movieTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.crew_fragment, container, false);
        ButterKnife.bind(this, view);

        crew_recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        crew_recycler.setNestedScrollingEnabled(false);
        crew_recycler.setVisibility(View.INVISIBLE);
        more.setOnClickListener(this);

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

    }

    public void parseCrewOutput(String crewResult) {

        MovieDetailsActivityParseWork par = new MovieDetailsActivityParseWork(getActivity(), crewResult);
        List<CrewDetailsData> crewList = par.parse_crew();

        jsonCrew = crewResult;

        CrewAdapter crewAdapter = new CrewAdapter(getActivity(), crewList, true);
        crewAdapter.setClickListener(this);
        crew_recycler.setAdapter(crewAdapter);

        if (crewList.size() > 4) {
            more.setVisibility(View.VISIBLE);
        } else if (crewList.size() == 0) {
            more.setVisibility(View.INVISIBLE);
            card_holder.setVisibility(View.INVISIBLE);
        } else {
            more.setVisibility(View.INVISIBLE);
        }

        breathingProgress.setVisibility(View.GONE);
        crew_recycler.setVisibility(View.VISIBLE);

        relativeLayout.setMinimumHeight(0);
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.crew_more) {
            Log.d("webi", "" + movieTitle);
            if (jsonCrew != null && movieTitle != null) {
                Intent intent = new Intent(getActivity(), FullCrewActivity.class);
                intent.putExtra("crew_json", jsonCrew);
                intent.putExtra("toolbar_title", movieTitle);
                startActivity(intent);
            }
        }
    }

    @Override
    public void itemClicked(CrewDetailsData setterGetter, int position, View view) {

        Intent intent = new Intent(getActivity(), CharacterDetailsActivity.class);
        intent.putExtra("id", setterGetter.getCrewId());

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            Pair<View, String> p1 = Pair.create(view.findViewById(R.id.crew_poster), "profile");
            Pair<View, String> p2 = Pair.create(view.findViewById(R.id.crew_name), "name");
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(getActivity(), p1, p2);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }
}
