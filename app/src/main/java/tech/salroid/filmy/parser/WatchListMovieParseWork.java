package tech.salroid.filmy.parser;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tech.salroid.filmy.data_classes.WatchlistData;

/**
 * Created by Home on 11/7/2016.
 */

public class WatchListMovieParseWork {

    private Context context;
    private String result;

    public WatchListMovieParseWork(Context context, String result) {
        this.context = context;
        this.result = result;
    }

    public List<WatchlistData> parse_watchlist() {
        final List<WatchlistData> favouriteArray = new ArrayList<WatchlistData>();
        WatchlistData favouriteData = null;


        try {


            JSONObject jsonobject = new JSONObject(result);

            JSONArray jsonArray = jsonobject.getJSONArray("results");

            for (int i = 0; i < jsonArray.length(); i++) {
                favouriteData = new WatchlistData();

                String title, id, movie_poster;

                id = jsonArray.getJSONObject(i).getString("id");
                movie_poster = "http://image.tmdb.org/t/p/w185" + jsonArray.getJSONObject(i).getString("poster_path");
                title = jsonArray.getJSONObject(i).getString("original_title");


                if (!(movie_poster.equals("null"))) {
                    favouriteData.setFav_id(id);
                    favouriteData.setFav_title(title);
                    favouriteData.setFav_poster(movie_poster);
                    favouriteArray.add(favouriteData);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return favouriteArray;
    }
}
