package tech.salroid.filmy.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.salroid.filmy.R;
import tech.salroid.filmy.activities.MovieDetailsActivity;
import tech.salroid.filmy.custom_adapter.SavedMoviesAdapter;
import tech.salroid.filmy.database.FilmContract;

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

public class SavedMovies extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SavedMoviesAdapter.ClickListener, SavedMoviesAdapter.LongClickListener {

    @BindView(R.id.my_saved_recycler)
    RecyclerView my_saved_movies_recycler;
    @BindView(R.id.emptyContainer)
    LinearLayout emptyContainer;
    @BindView(R.id.database_image)
    ImageView dataImageView;


    private static final int SAVED_DETAILS_LOADER = 3;
    private static final String[] GET_SAVE_COLUMNS = {

            FilmContract.SaveEntry.SAVE_ID,
            FilmContract.SaveEntry.SAVE_TITLE,
            FilmContract.SaveEntry.SAVE_BANNER,
            FilmContract.SaveEntry.SAVE_DESCRIPTION,
            FilmContract.SaveEntry.SAVE_TAGLINE,
            FilmContract.SaveEntry.SAVE_TRAILER,
            FilmContract.SaveEntry.SAVE_RATING,
            FilmContract.SaveEntry.SAVE_LANGUAGE,
            FilmContract.SaveEntry.SAVE_RELEASED,
            FilmContract.SaveEntry._ID,
            FilmContract.SaveEntry.SAVE_YEAR,
            FilmContract.SaveEntry.SAVE_CERTIFICATION,
            FilmContract.SaveEntry.SAVE_RUNTIME,
            FilmContract.SaveEntry.SAVE_POSTER_LINK,
    };

    private SavedMoviesAdapter mainActivityAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_saved_movies, container, false);
        ButterKnife.bind(this, view);

          /*GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        my_saved_movies_recycler.setLayoutManager(gridLayoutManager);*/

        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);

        if (tabletSize) {

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

                StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(6,
                        StaggeredGridLayoutManager.VERTICAL);
                my_saved_movies_recycler.setLayoutManager(gridLayoutManager);
            } else {
                StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(8,
                        StaggeredGridLayoutManager.VERTICAL);
                my_saved_movies_recycler.setLayoutManager(gridLayoutManager);
            }

        } else {

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

                StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(3,
                        StaggeredGridLayoutManager.VERTICAL);
                my_saved_movies_recycler.setLayoutManager(gridLayoutManager);
            } else {
                StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(5,
                        StaggeredGridLayoutManager.VERTICAL);
                my_saved_movies_recycler.setLayoutManager(gridLayoutManager);
            }

        }

        mainActivityAdapter = new SavedMoviesAdapter(getActivity(), null);
        my_saved_movies_recycler.setAdapter(mainActivityAdapter);
        mainActivityAdapter.setClickListener(this);
        mainActivityAdapter.setLongClickListener(this);

        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(getActivity(), FilmContract.SaveEntry.CONTENT_URI, GET_SAVE_COLUMNS, null, null, "_ID DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {


        if (cursor != null && cursor.getCount() > 0)
            mainActivityAdapter.swapCursor(cursor);
        else
            emptyContainer.setVisibility(View.VISIBLE);


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mainActivityAdapter.swapCursor(null);
        emptyContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void itemClicked(String movieId, String title) {


        Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
        intent.putExtra("saved_database_applicable", true);
        intent.putExtra("network_applicable", true);
        intent.putExtra("title", title);
        intent.putExtra("id", movieId);

        startActivity(intent);

    }


    @Override
    public void itemLongClicked(final Cursor mycursor, final int position) {


        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);

        arrayAdapter.add("Remove");


        final Context context = getActivity();

        adb.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                final String deleteSelection = FilmContract.SaveEntry.TABLE_NAME + "." + FilmContract.SaveEntry.SAVE_ID + " = ? ";


                final String[] deletionArgs = {mycursor.getString(mycursor.getColumnIndex(FilmContract.SaveEntry.SAVE_ID))};

                long deletion_id = context.getContentResolver().delete(FilmContract.SaveEntry.CONTENT_URI, deleteSelection, deletionArgs);

                if (deletion_id != -1) {

                    mainActivityAdapter.notifyItemRemoved(position);

                    if (mainActivityAdapter.getItemCount() == 1)
                        my_saved_movies_recycler.setVisibility(View.GONE);


                }
            }
        });

        adb.show();

    }


    @Override
    public void onResume() {
        super.onResume();
        getActivity().getSupportLoaderManager().initLoader(SAVED_DETAILS_LOADER, null, this);
    }
}