package tech.salroid.filmy.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import tech.salroid.filmy.CustomAdapter.MainActivityAdapter;
import tech.salroid.filmy.CustomAdapter.SavedMoviesAdapter;
import tech.salroid.filmy.Database.FilmContract;
import tech.salroid.filmy.R;

public class SavedMovies extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,SavedMoviesAdapter.ClickListener, SavedMoviesAdapter.LongClickListener {

    private static final int SAVED_DETAILS_LOADER = 3;
    private RecyclerView my_saved_movies_recycler;


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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_movies);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        my_saved_movies_recycler = (RecyclerView) findViewById(R.id.my_saved_recycler);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        my_saved_movies_recycler.setLayoutManager(gridLayoutManager);

        mainActivityAdapter = new SavedMoviesAdapter(this, null);
        my_saved_movies_recycler.setAdapter(mainActivityAdapter);
        mainActivityAdapter.setClickListener(this);
        mainActivityAdapter.setLongClickListener(this);

        getSupportLoaderManager().initLoader(SAVED_DETAILS_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(this, FilmContract.SaveEntry.CONTENT_URI, GET_SAVE_COLUMNS, null, null,"_ID DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

            Log.d("webi", String.valueOf(cursor.getCount()));
            mainActivityAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mainActivityAdapter.swapCursor(null);
    }

    @Override
    public void itemClicked(Cursor cursor) {

        int id_index = cursor.getColumnIndex(FilmContract.SaveEntry.SAVE_ID);
        int title_index = cursor.getColumnIndex(FilmContract.SaveEntry.SAVE_TITLE);

        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra("title", cursor.getString(title_index));
        intent.putExtra("activity", true);
        intent.putExtra("id", cursor.getString(id_index));
        startActivity(intent);

    }


    @Override
    public void itemLongClicked(final Cursor mycursor, final int position) {

        AlertDialog.Builder adb = new AlertDialog.Builder(SavedMovies.this);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(SavedMovies.this, android.R.layout.simple_list_item_1);

        arrayAdapter.add("");

        final Context context=this;

        adb.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                final String deleteSelection = FilmContract.SaveEntry.TABLE_NAME + "." + FilmContract.SaveEntry.SAVE_ID + " = ? ";


                final String[] deletionArgs = {mycursor.getString(mycursor.getColumnIndex(FilmContract.SaveEntry.SAVE_ID))};

                long deletion_id = context.getContentResolver().delete(FilmContract.SaveEntry.CONTENT_URI, deleteSelection, deletionArgs);

                if (deletion_id != -1) {

                    mainActivityAdapter.notifyItemRemoved(position);
                }
            }
        });

        adb.show();


    }
}
