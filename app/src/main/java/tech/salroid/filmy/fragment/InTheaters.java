package tech.salroid.filmy.fragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.salroid.filmy.R;
import tech.salroid.filmy.activities.MainActivity;
import tech.salroid.filmy.activities.MovieDetailsActivity;
import tech.salroid.filmy.custom_adapter.MainActivityAdapter;
import tech.salroid.filmy.customs.BreathingProgress;
import tech.salroid.filmy.customs.CustomToast;
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

public class InTheaters extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, MainActivityAdapter.ClickListener {

    private MainActivityAdapter mainActivityAdapter;
    private boolean isShowingFromDatabase;

    @BindView(R.id.breathingProgress)
    BreathingProgress breathingProgress;
    @BindView((R.id.recycler))
    RecyclerView recycler;

    private StaggeredGridLayoutManager gridLayoutManager;
    private boolean isInMultiWindowMode;

    public InTheaters() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_in_theaters, container, false);
        ButterKnife.bind(this, view);


        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            isInMultiWindowMode = getActivity().isInMultiWindowMode();
        }

        if (tabletSize) {

            if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

                gridLayoutManager = new StaggeredGridLayoutManager(6,
                        StaggeredGridLayoutManager.VERTICAL);
                recycler.setLayoutManager(gridLayoutManager);

            } else {

                if (isInMultiWindowMode) {

                    gridLayoutManager = new StaggeredGridLayoutManager(6,
                            StaggeredGridLayoutManager.VERTICAL);
                    recycler.setLayoutManager(gridLayoutManager);

                } else {

                    gridLayoutManager = new StaggeredGridLayoutManager(8,
                            StaggeredGridLayoutManager.VERTICAL);
                    recycler.setLayoutManager(gridLayoutManager);

                }
            }

        } else {

            if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

                gridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
                recycler.setLayoutManager(gridLayoutManager);

            } else {

                if (isInMultiWindowMode) {

                    gridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
                    recycler.setLayoutManager(gridLayoutManager);

                } else {

                    gridLayoutManager = new StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.VERTICAL);
                    recycler.setLayoutManager(gridLayoutManager);

                }
            }
        }

        mainActivityAdapter = new MainActivityAdapter(getActivity(), null);
        recycler.setAdapter(mainActivityAdapter);
        mainActivityAdapter.setClickListener(this);

        return view;

    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().getSupportLoaderManager().initLoader(MovieProjection.INTHEATERS_MOVIE_LOADER, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri moviesForTheUri = FilmContract.InTheatersMoviesEntry.CONTENT_URI;

        return new CursorLoader(getActivity(),
                moviesForTheUri,
                MovieProjection.MOVIE_COLUMNS,
                null,
                null,
                null);

    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {

            isShowingFromDatabase = true;
            mainActivityAdapter.swapCursor(cursor);
            breathingProgress.setVisibility(View.GONE);

        } else if (!((MainActivity) getActivity()).fetchingFromNetwork) {

            CustomToast.show(getActivity(), "Failed to get In Theaters movies.", true);
            ((MainActivity) getActivity()).cantProceed(-1);

        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mainActivityAdapter.swapCursor(null);
    }


    @Override
    public void itemClicked(Cursor cursor) {

        int id_index = cursor.getColumnIndex(FilmContract.MoviesEntry.MOVIE_ID);
        int title_index = cursor.getColumnIndex(FilmContract.MoviesEntry.MOVIE_TITLE);

        Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
        intent.putExtra("title", cursor.getString(title_index));
        intent.putExtra("activity", true);
        intent.putExtra("type", 1);
        //  intent.putExtra("update_id",true);
        intent.putExtra("database_applicable", true);
        intent.putExtra("network_applicable", true);
        intent.putExtra("id", cursor.getString(id_index));
        startActivity(intent);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT)
            getActivity().overridePendingTransition(0, 0);
    }

    @Override
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        super.onMultiWindowModeChanged(isInMultiWindowMode);

        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

            if (isInMultiWindowMode)
                gridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
            else
                gridLayoutManager = new StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.VERTICAL);

            recycler.setLayoutManager(gridLayoutManager);
            recycler.setAdapter(mainActivityAdapter);
        }

    }
}