package tech.salroid.filmy.Datawork;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;

import tech.salroid.filmy.Database.FilmContract;
import tech.salroid.filmy.Network.TmdbVolleySingleton;


public class MainActivityParseWork {

    // private final int type;
    private Context context;
    private String result;
    private final String LOG_TAG = MainActivityParseWork.class.getSimpleName();
    private String imdb_id;


    public MainActivityParseWork(Context context, String result) {
        this.context = context;
        this.result = result;
    }

    public void parseupcoming() {

        try {

            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("results");

            Vector<ContentValues> cVVector = new Vector<ContentValues>(jsonArray.length());

            for (int i = 0; i < jsonArray.length(); i++) {

                String title, poster, id,year;

                title = (jsonArray.getJSONObject(i)).getString("original_title");
                poster = (jsonArray.getJSONObject(i).getString("poster_path"));
                id = (jsonArray.getJSONObject(i)).getString("id");


               String temp_year []=(jsonArray.getJSONObject(i)).getString("release_date").split("-");

                year=temp_year[0];


                String trimmedQuery = (title.toLowerCase()).trim();
                String finalQuery = trimmedQuery.replace(" ","-");

                String slug=(finalQuery.replace(":",""))+"-"+year;



                ContentValues movieValues = new ContentValues();

                movieValues.put(FilmContract.MoviesEntry.MOVIE_ID,slug);
                movieValues.put(FilmContract.MoviesEntry.MOVIE_TITLE, title);
                movieValues.put(FilmContract.MoviesEntry.MOVIE_YEAR, year);
                movieValues.put(FilmContract.MoviesEntry.MOVIE_POSTER_LINK, "http://image.tmdb.org/t/p/w185" + poster);

                Log.d("webi", "Fetching Complete. ");

                cVVector.add(movieValues);

            }
            int inserted = 0;
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);

                context.getContentResolver().delete(FilmContract.MoviesEntry.CONTENT_URI, null, null);
                inserted = context.getContentResolver().bulkInsert(FilmContract.MoviesEntry.CONTENT_URI, cvArray);

            }

            Log.d("webi", "Fetching Complete. " + inserted + " Inserted");

        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        return;
    }

}
