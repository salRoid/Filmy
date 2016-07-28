package tech.salroid.filmy.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import tech.salroid.filmy.CustomAdapter.MainActivityAdapter;
import tech.salroid.filmy.Database.FilmContract;
import tech.salroid.filmy.Datawork.MainActivityParseWork;
import tech.salroid.filmy.R;
import tech.salroid.filmy.DataClasses.MovieData;
import tech.salroid.filmy.Network.VolleySingleton;


public class MainActivity extends AppCompatActivity implements MainActivityAdapter.ClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private Toolbar toolbar;
    RecyclerView recycler;
    private RecyclerView recycler_boxoffice;
    private static final int MOVIE_LOADER = 1;
    private MainActivityAdapter mainActivityAdapter;


    private static final String[] MOVIE_COLUMNS = {

            FilmContract.MoviesEntry.MOVIE_ID,
            FilmContract.MoviesEntry.MOVIE_TITLE,
            FilmContract.MoviesEntry.MOVIE_YEAR,
            FilmContract.MoviesEntry.MOVIE_POSTER_LINK

    };

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("");

        getSupportActionBar().setLogo(R.drawable.ic_action_filmy_logo);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,SearchActivity.class));
            }
        });

        recycler = (RecyclerView) findViewById(R.id.recycler);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recycler.setLayoutManager(gridLayoutManager);

        mainActivityAdapter = new MainActivityAdapter(this, null);
        recycler.setAdapter(mainActivityAdapter);
        mainActivityAdapter.setClickListener(this);



        getData();

        getSupportLoaderManager().initLoader(MOVIE_LOADER, null, this);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        getMenuInflater().inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void getData() {

        VolleySingleton volleySingleton = VolleySingleton.getInstance();
        RequestQueue requestQueue = volleySingleton.getRequestQueue();

        final String BASE_URL = "https://api.trakt.tv/movies/trending?extended=metadata,page=1&limit=25";


        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, BASE_URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        parseOutput(response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("webi", "Volley Error: " + error.getCause());

            }
        }
        );

        requestQueue.add(jsonObjectRequest);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void itemClicked(Cursor cursor) {

        int id_index = cursor.getColumnIndex(FilmContract.MoviesEntry.MOVIE_ID);
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra("activity", true);
        intent.putExtra("id", cursor.getString(id_index));
        startActivity(intent);

    }

    private void parseOutput(String result) {


        MainActivityParseWork pa = new MainActivityParseWork(this, result);
        pa.parse();

   /*     List<MovieData> list = pa.parse();
        MainActivityAdapter adapter = new MainActivityAdapter(this, list);
        adapter.setClickListener(this);
        recycler.setAdapter(adapter);
        */
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // Sort order:  Ascending, by date.
        //String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri moviesForTheUri = FilmContract.MoviesEntry.CONTENT_URI;
        //locationSetting, System.currentTimeMillis());

        return new CursorLoader(this,
                moviesForTheUri,
                MOVIE_COLUMNS,
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


}
