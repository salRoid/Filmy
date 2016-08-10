package tech.salroid.filmy.fragments;

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

import tech.salroid.filmy.R;
import tech.salroid.filmy.activity.MovieDetailsActivity;
import tech.salroid.filmy.custom_adapter.MainActivityAdapter;
import tech.salroid.filmy.database.FilmContract;
import tech.salroid.filmy.database.MovieSelection;

public class InTheaters extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, MainActivityAdapter.ClickListener {


    private MainActivityAdapter mainActivityAdapter;

    public InTheaters() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_in_theaters, container, false);
        RecyclerView recycler = (RecyclerView) view.findViewById(R.id.recycler);
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

        getActivity().getSupportLoaderManager().initLoader(MovieSelection.INTHEATERS_MOVIE_LOADER, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri moviesForTheUri = FilmContract.InTheatersMoviesEntry.CONTENT_URI;

        return new CursorLoader(getActivity(),
                moviesForTheUri,
                MovieSelection.MOVIE_COLUMNS,
                null,
                null,
                null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mainActivityAdapter.swapCursor(cursor);
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
        intent.putExtra("type",1);
      //  intent.putExtra("update_id",true);
        intent.putExtra("database_applicable", true);
        intent.putExtra("network_applicable", true);
        intent.putExtra("id", cursor.getString(id_index));
        startActivity(intent);
    }
}
