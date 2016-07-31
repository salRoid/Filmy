package tech.salroid.filmy.Activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONArray;

import java.util.ArrayList;

import tech.salroid.filmy.CustomAdapter.MainActivityAdapter;
import tech.salroid.filmy.Database.FilmContract;
import tech.salroid.filmy.Datawork.MainActivityParseWork;
import tech.salroid.filmy.R;
import tech.salroid.filmy.Network.VolleySingleton;
import tech.salroid.filmy.SearchFragment;
import tech.salroid.filmy.Service.FilmyService;
import tech.salroid.filmy.Sync.FilmySyncAdapter;


public class MainActivity extends AppCompatActivity implements MainActivityAdapter.ClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private Toolbar toolbar;
    RecyclerView recycler;
    private static final int MOVIE_LOADER = 1;
    private MainActivityAdapter mainActivityAdapter;
    private MaterialSearchView materialSearchView;
    private SearchFragment searchFragment;
    private FloatingActionButton fab;


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
        getSupportActionBar().setTitle(" ");


        getSupportActionBar().setLogo(R.drawable.ic_action_filmy_logo);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        recycler = (RecyclerView) findViewById(R.id.recycler);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recycler.setLayoutManager(gridLayoutManager);

        mainActivityAdapter = new MainActivityAdapter(this, null);
        recycler.setAdapter(mainActivityAdapter);
        mainActivityAdapter.setClickListener(this);


        materialSearchView = (MaterialSearchView) findViewById(R.id.search_view);
        materialSearchView.setVoiceSearch(true);
        materialSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Do some magic

                getSearchedResult(query);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                 getSearchedResult(newText);

                return true;
            }
        });

        materialSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic

                searchFragment = new SearchFragment();
                getSupportFragmentManager().
                        beginTransaction().
                        replace(R.id.fragment_container, searchFragment)
                        .commit();

            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic

                getSupportFragmentManager()
                        .beginTransaction()
                        .remove(searchFragment)
                        .commit();

            }
        });

/*

        Intent alarmIntent = new Intent(this, FilmyService.AlarmReciever.class);

        PendingIntent pi  = PendingIntent.getBroadcast(this,0,alarmIntent,PendingIntent.FLAG_ONE_SHOT);

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+5000,pi);
*/


        FilmySyncAdapter.initializeSyncAdapter(this);
        getSupportLoaderManager().initLoader(MOVIE_LOADER, null, this);

    }

    private void getSearchedResult(String query) {

        if (searchFragment != null) {
            searchFragment.getSearchedResult(query);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        materialSearchView.setMenuItem(item);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {

            startActivity(new Intent(this, SearchFragment.class));
        }

        if(id==R.id.ic_collection){
            startActivity(new Intent(this,SavedMovies.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void itemClicked(Cursor cursor) {

        int id_index = cursor.getColumnIndex(FilmContract.MoviesEntry.MOVIE_ID);
        int title_index = cursor.getColumnIndex(FilmContract.MoviesEntry.MOVIE_TITLE);

        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra("title", cursor.getString(title_index));
        intent.putExtra("activity", true);
        intent.putExtra("database_applicable",true);
        intent.putExtra("network_applicable",true);
        intent.putExtra("id", cursor.getString(id_index));
        startActivity(intent);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    materialSearchView.setQuery(searchWrd, false);
                }
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (materialSearchView.isSearchOpen()) {
            materialSearchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    /*@Override
    public void onSuggestionReady(String[] suggestionArray) {
        materialSearchView.setSuggestions(suggestionArray);
    }*/
}
