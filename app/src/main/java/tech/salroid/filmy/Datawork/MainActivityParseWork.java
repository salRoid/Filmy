package tech.salroid.filmy.Datawork;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.Vector;
import tech.salroid.filmy.Database.FilmContract;


public class MainActivityParseWork {

    private Context context;
    private String result;
    private final String LOG_TAG = MainActivityParseWork.class.getSimpleName();


    public MainActivityParseWork(Context context, String result) {
        this.context = context;
        this.result = result;
    }

    public void parse() {

        //final List<MovieData> movieDataArrayList = new ArrayList<MovieData>();
        //MovieData movieData = null;


        try {

            JSONArray jsonArray = new JSONArray(result);
            Vector<ContentValues> cVVector = new Vector<ContentValues>(jsonArray.length());

            for (int i = 0; i < jsonArray.length(); i++) {

                //movieData = new MovieData();
                String title, poster, id;
                int year;

                title = (jsonArray.getJSONObject(i).getJSONObject("movie")).getString("title");
                year = (jsonArray.getJSONObject(i).getJSONObject("movie")).getInt("year");
                poster = (jsonArray.getJSONObject(i).getJSONObject("movie").getJSONObject("images").getJSONObject("poster").getString("thumb"));
                id = (jsonArray.getJSONObject(i).getJSONObject("movie")).getJSONObject("ids").getString("imdb");

                //movieData.setMovie(title);
                //movieData.setYear(year);
                //movieData.setId(id);
                //movieData.setPoster(poster);

                //movieDataArrayList.add(movieData);

                // Insert the new weather information into the database

                ContentValues movieValues = new ContentValues();

                movieValues.put(FilmContract.MoviesEntry.MOVIE_ID, id);
                movieValues.put(FilmContract.MoviesEntry.MOVIE_TITLE, title);
                movieValues.put(FilmContract.MoviesEntry.MOVIE_YEAR, year);
                movieValues.put(FilmContract.MoviesEntry.MOVIE_POSTER_LINK, poster);


                cVVector.add(movieValues);

            }
                int inserted = 0;
                // add to database
                if (cVVector.size() > 0) {
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);

                    context.getContentResolver().delete(FilmContract.MoviesEntry.CONTENT_URI,null,null);
                    inserted = context.getContentResolver().bulkInsert(FilmContract.MoviesEntry.CONTENT_URI, cvArray);

                }

                Log.d(LOG_TAG, "Fetching Complete. " + inserted + " Inserted");



        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        return;
    }

}
