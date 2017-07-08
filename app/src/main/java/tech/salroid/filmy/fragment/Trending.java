package tech.salroid.filmy.fragment;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.salroid.filmy.R;
import tech.salroid.filmy.activities.MainActivity;
import tech.salroid.filmy.activities.MovieDetailsActivity;
import tech.salroid.filmy.custom_adapter.MainActivityAdapter;
import tech.salroid.filmy.customs.BreathingProgress;
import tech.salroid.filmy.customs.CustomToast;
import tech.salroid.filmy.customs.RecyclerviewEndlessScrollListener;
import tech.salroid.filmy.database.FilmContract;
import tech.salroid.filmy.database.MovieProjection;

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

public class Trending extends Fragment implements MainActivityAdapter.ClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.breathingProgress)
    BreathingProgress breathingProgress;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.moreProgress)
    ProgressBar moreProgress;

    private MainActivityAdapter mainActivityAdapter;
    public boolean isShowingFromDatabase;
    private boolean multiWindowMode;
    private boolean multiWindowModeLast;


    public Trending() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_trending, container, false);
        ButterKnife.bind(this, view);

        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
        StaggeredGridLayoutManager gridLayoutManager;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            multiWindowMode = getActivity().isInMultiWindowMode();

        if (savedInstanceState != null)
            multiWindowModeLast = savedInstanceState.getBoolean("last_multi_value");

        if (tabletSize) {

            if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

                gridLayoutManager = new StaggeredGridLayoutManager(6,
                        StaggeredGridLayoutManager.VERTICAL);
                recycler.setLayoutManager(gridLayoutManager);
            } else {
                gridLayoutManager = new StaggeredGridLayoutManager(8,
                        StaggeredGridLayoutManager.VERTICAL);
                recycler.setLayoutManager(gridLayoutManager);
            }

        } else {

            if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {


                Log.d("webi", "onCreateView: portrait " + multiWindowMode);

                gridLayoutManager = new StaggeredGridLayoutManager(3,
                        StaggeredGridLayoutManager.VERTICAL);
                recycler.setLayoutManager(gridLayoutManager);

            } else {

                if (multiWindowMode && !multiWindowModeLast) {

                    Log.d("webi", "onCreateView: landscape " + multiWindowMode + " last " + multiWindowModeLast);

                    gridLayoutManager = new StaggeredGridLayoutManager(3,
                            StaggeredGridLayoutManager.VERTICAL);
                    recycler.setLayoutManager(gridLayoutManager);

                } else if (multiWindowMode && multiWindowModeLast) {

                    Log.d("webi", "onCreateView: landscape " + multiWindowMode+" last "+ multiWindowModeLast);

                    gridLayoutManager = new StaggeredGridLayoutManager(5,
                            StaggeredGridLayoutManager.VERTICAL);
                    recycler.setLayoutManager(gridLayoutManager);


                } else {

                    Log.d("webi", "onCreateView: landscape " + multiWindowMode);

                    gridLayoutManager = new StaggeredGridLayoutManager(5,
                            StaggeredGridLayoutManager.VERTICAL);
                    recycler.setLayoutManager(gridLayoutManager);

                }

            }

        }

        mainActivityAdapter = new MainActivityAdapter(getActivity(), null);
        recycler.setAdapter(mainActivityAdapter);
        mainActivityAdapter.setClickListener(this);

        recycler.addOnScrollListener(new RecyclerviewEndlessScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                moreProgress.setVisibility(View.VISIBLE);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getSupportLoaderManager().initLoader(MovieProjection.TRENDING_MOVIE_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {


        Uri moviesForTheUri = FilmContract.MoviesEntry.CONTENT_URI;

        return new CursorLoader(getActivity(),
                moviesForTheUri,
                MovieProjection.MOVIE_COLUMNS,
                null,
                null,
                null);

    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor != null && cursor.getCount() > 0) {

            isShowingFromDatabase = true;
            mainActivityAdapter.swapCursor(cursor);
            breathingProgress.setVisibility(View.GONE);

        } else if (!((MainActivity) getActivity()).fetchingFromNetwork) {

            CustomToast.show(getActivity(), "Failed to get latest movies.", true);
            ((MainActivity) getActivity()).cantProceed(-1);

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mainActivityAdapter.swapCursor(null);
    }


    @Override
    public void itemClicked(Cursor cursor) {

        int id_index = cursor.getColumnIndex(FilmContract.MoviesEntry.MOVIE_ID);
        int title_index = cursor.getColumnIndex(FilmContract.MoviesEntry.MOVIE_TITLE);


        Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
        intent.putExtra("title", cursor.getString(title_index));
        intent.putExtra("activity", true);
        intent.putExtra("type", 0);
        intent.putExtra("database_applicable", true);
        intent.putExtra("network_applicable", true);
        intent.putExtra("id", cursor.getString(id_index));
        startActivity(intent);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT)
            getActivity().overridePendingTransition(0, 0);
    }

    public void retryLoading() {
        getActivity().getSupportLoaderManager().restartLoader(MovieProjection.TRENDING_MOVIE_LOADER, null, this);
    }

    @Override
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        super.onMultiWindowModeChanged(isInMultiWindowMode);

        Log.d("webi", "onMultiWindowModeChanged: ");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d("webi", "onConfigurationChanged: ");
    }

}