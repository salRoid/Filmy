package tech.salroid.filmy.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
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
    LinearLayout emptyContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_movies);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        my_saved_movies_recycler = (RecyclerView) findViewById(R.id.my_saved_recycler);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        my_saved_movies_recycler.setLayoutManager(gridLayoutManager);
        emptyContainer=(LinearLayout)findViewById(R.id.emptyContainer);

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


            if(cursor!=null && cursor.getCount()>0)
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
    public void itemClicked(String movieId,String title) {


        Intent intent = new Intent(this, MovieDetailsActivity.class);

        intent.putExtra("saved_database_applicable",true);
        intent.putExtra("title",title);
        intent.putExtra("id", movieId);

        startActivity(intent);

    }


    @Override
    public void itemLongClicked(final Cursor mycursor, final int position) {



        AlertDialog.Builder adb = new AlertDialog.Builder(SavedMovies.this);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(SavedMovies.this, android.R.layout.simple_list_item_1);

        arrayAdapter.add("Remove");


        final Context context=this;

        adb.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                final String deleteSelection = FilmContract.SaveEntry.TABLE_NAME + "." + FilmContract.SaveEntry.SAVE_ID + " = ? ";


                final String[] deletionArgs = {mycursor.getString(mycursor.getColumnIndex(FilmContract.SaveEntry.SAVE_ID))};

                long deletion_id = context.getContentResolver().delete(FilmContract.SaveEntry.CONTENT_URI, deleteSelection, deletionArgs);

                if (deletion_id != -1) {

                    mainActivityAdapter.notifyItemRemoved(position);

                    if(mainActivityAdapter.getItemCount()==1)
                        my_saved_movies_recycler.setVisibility(View.GONE);


                }
            }
        });

        adb.show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
