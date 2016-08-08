package tech.salroid.filmy.Fragments;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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

import tech.salroid.filmy.Activity.MainActivity;
import tech.salroid.filmy.Activity.MovieDetailsActivity;
import tech.salroid.filmy.Custom.BreathingProgress;
import tech.salroid.filmy.CustomAdapter.MainActivityAdapter;
import tech.salroid.filmy.Database.FilmContract;
import tech.salroid.filmy.Database.MovieSelection;
import tech.salroid.filmy.R;


public class Trending extends Fragment implements MainActivityAdapter.ClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private RecyclerView recycler;
    private MainActivityAdapter mainActivityAdapter;
    BreathingProgress breathingProgress;


    public Trending() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_trending, container, false);
        recycler = (RecyclerView) view.findViewById(R.id.recycler);

        breathingProgress = (BreathingProgress) view.findViewById(R.id.breathingProgress);

        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        recycler.setLayoutManager(gridLayoutManager);

        mainActivityAdapter = new MainActivityAdapter(getActivity(), null);
        recycler.setAdapter(mainActivityAdapter);
        mainActivityAdapter.setClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getSupportLoaderManager().initLoader(MovieSelection.TRENDING_MOVIE_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {


        Uri moviesForTheUri = FilmContract.MoviesEntry.CONTENT_URI;

        return new CursorLoader(getActivity(),
                moviesForTheUri,
                MovieSelection.MOVIE_COLUMNS,
                null,
                null,
                null);

    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor != null && cursor.getCount() > 0) {

            mainActivityAdapter.swapCursor(cursor);
            breathingProgress.setVisibility(View.GONE);

        } else if (!((MainActivity) getActivity()).fetchingFromNetwork) {
            ((MainActivity) getActivity()).cantProceed();
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


    }


    public void retryLoading() {

        getActivity().getSupportLoaderManager().restartLoader(MovieSelection.TRENDING_MOVIE_LOADER, null, this);

    }


}
