package tech.salroid.filmy.fragment;

/*
 * Filmy Application for Android
 * Copyright (c) 2017 Sajal Gupta (http://github.com/salroid).
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

import android.animation.Animator;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.youtube.player.YouTubeStandalonePlayer;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.salroid.filmy.R;
import tech.salroid.filmy.custom_adapter.TrailerAdapter;

public class AllTrailerFragment extends Fragment implements View.OnClickListener, TrailerAdapter.OnItemClickListener {

    private String titleValue;
    private String[] trailers;
    private String[] trailers_name;
    private RecyclerView recyclerView;

    @BindView(R.id.textViewTitle)
    TextView title;
    @BindView(R.id.cross)
    ImageView crossButton;
    @BindView(R.id.main_content)
    RelativeLayout mainContent;

    private boolean nightMode;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        nightMode = sp.getBoolean("dark", false);

        View view = inflater.inflate(R.layout.all_trailer_layout, container, false);
        ButterKnife.bind(this, view);

        if (!nightMode)
            allThemeLogic();
        else {
            nightModeLogic();
        }

        crossButton.setOnClickListener(this);

        // To run the animation as soon as the view is layout in the view hierarchy we add this
        // listener and remove it
        // as soon as it runs to prevent multiple animations if the view changes bounds
        view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop,
                                       int oldRight, int oldBottom) {
                v.removeOnLayoutChangeListener(this);

                int cx = getArguments().getInt("cx");
                int cy = getArguments().getInt("cy");

                // get the hypothenuse so the radius is from one corner to the other
                int radius = (int) Math.hypot(right, bottom);

                Animator reveal;

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    reveal = ViewAnimationUtils.createCircularReveal(v, cx, cy, 0, radius);
                    reveal.setInterpolator(new DecelerateInterpolator(2f));
                    reveal.setDuration(1000);
                    reveal.start();
                }

            }
        });

        init(view);

        return view;
    }

    private void nightModeLogic() {
        crossButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_navigation_close_inverted));
        mainContent.setBackgroundColor(Color.parseColor("#212121"));
        title.setTextColor(Color.parseColor("#ffffff"));
    }

    private void allThemeLogic() {
        crossButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_close_black_48dp));
        mainContent.setBackgroundColor(Color.parseColor("#ffffff"));
        title.setTextColor(Color.parseColor("#000000"));

    }

    private void init(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.all_trailer_recycler_view);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        titleValue = getArguments().getString("title", " ");
        trailers = getArguments().getStringArray("trailers");
        trailers_name = getArguments().getStringArray("trailers_name");

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        title.setText(titleValue);
        TrailerAdapter trailerAdapter = new TrailerAdapter(trailers, trailers_name, getActivity());
        trailerAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(trailerAdapter);

    }

    @Override
    public void onClick(View view) {
        if (getFragmentManager() != null) {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public void itemClicked(final String trailerId) {
        final int timeMiliSeconds = 0;
        final boolean autoPlay = true;
        final boolean lightBoxMode = false;
       startActivity(YouTubeStandalonePlayer.createVideoIntent(getActivity(),getString(R.string.Youtube_Api_Key),
               trailerId, timeMiliSeconds, autoPlay,lightBoxMode));
    }
}
