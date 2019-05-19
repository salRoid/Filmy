package tech.salroid.filmy.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.salroid.filmy.R;
import tech.salroid.filmy.activities.MovieDetailsActivity;
import tech.salroid.filmy.custom_adapter.SavedMoviesAdapter;
import tech.salroid.filmy.database.FilmContract;
import tech.salroid.filmy.utility.Constants;

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

public class Favorite extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SavedMoviesAdapter.ClickListener, SavedMoviesAdapter.LongClickListener {

    @BindView(R.id.my_saved_recycler)
    RecyclerView my_saved_movies_recycler;
    @BindView(R.id.emptyContainer)
    LinearLayout emptyContainer;
    @BindView(R.id.database_image)
    ImageView dataImageView;

    private static final int SAVED_DETAILS_LOADER = 4;
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
            FilmContract.SaveEntry.SAVE_FLAG
    };

    private SavedMoviesAdapter mainActivityAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_fav_movies, container, false);
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

        String selection = FilmContract.SaveEntry.TABLE_NAME + "." + FilmContract.SaveEntry.SAVE_FLAG + "= ?";
        String[] selectionArgs = {String.valueOf(Constants.FLAG_FAVORITE)};

        return new CursorLoader(getActivity(), FilmContract.SaveEntry.CONTENT_URI, GET_SAVE_COLUMNS, selection, selectionArgs, "_ID DESC");
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


        MaterialAlertDialogBuilder adb = new MaterialAlertDialogBuilder(getActivity());
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
        arrayAdapter.add("Remove");


        final Context context = getActivity();

        adb.setAdapter(arrayAdapter, (dialogInterface, i) -> {

            final String deleteSelection = FilmContract.SaveEntry.TABLE_NAME + "." + FilmContract.SaveEntry.SAVE_ID + " = ? AND " +
                    FilmContract.SaveEntry.TABLE_NAME + "." + FilmContract.SaveEntry.SAVE_FLAG + " = ? ";

            int flag_index = mycursor.getColumnIndex(FilmContract.SaveEntry.SAVE_FLAG);
            int flag = mycursor.getInt(flag_index);

            final String[] deletionArgs = {mycursor.getString(mycursor.getColumnIndex(FilmContract.SaveEntry.SAVE_ID)), String.valueOf(flag)};

            long deletion_id = context.getContentResolver().delete(FilmContract.SaveEntry.CONTENT_URI, deleteSelection, deletionArgs);

            if (deletion_id != -1) {

                mainActivityAdapter.notifyItemRemoved(position);

                if (mainActivityAdapter.getItemCount() == 1)
                    my_saved_movies_recycler.setVisibility(View.GONE);
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
