package tech.salroid.filmy.parser;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

import tech.salroid.filmy.data.WatchlistData;

public class WatchListMovieParseWork {

    private String result;

    public WatchListMovieParseWork(Context context, String result) {
        this.result = result;
    }

    public List<WatchlistData> parseWatchList() {
        final List<WatchlistData> favouriteArray = new ArrayList<>();
        WatchlistData favouriteData;

        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONArray jsonArray = jsonobject.getJSONArray("results");

            for (int i = 0; i < jsonArray.length(); i++) {

                String id = jsonArray.getJSONObject(i).getString("id");
                String poster = "http://image.tmdb.org/t/p/w185" + jsonArray.getJSONObject(i).getString("poster_path");
                String title = jsonArray.getJSONObject(i).getString("original_title");
                favouriteData = new WatchlistData(id);

                if (!(poster.equals("null"))) {
                    favouriteData.setId(id);
                    favouriteData.setTitle(title);
                    favouriteData.setPoster(poster);
                    favouriteArray.add(favouriteData);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return favouriteArray;
    }
}
