package tech.salroid.filmy.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tech.salroid.filmy.data.FavouriteData;

public class FavouriteMovieParseWork {

    private String result;

    public FavouriteMovieParseWork(String result) {
        this.result = result;
    }

    public List<FavouriteData> parseFavourite() {
        final List<FavouriteData> favouriteArray = new ArrayList<>();
        FavouriteData favouriteData;

        try {

            JSONObject jsonobject = new JSONObject(result);
            JSONArray jsonArray = jsonobject.getJSONArray("results");

            for (int i = 0; i < jsonArray.length(); i++) {

                String title, id, movie_poster;

                id = jsonArray.getJSONObject(i).getString("id");
                movie_poster = "http://image.tmdb.org/t/p/w185" + jsonArray.getJSONObject(i).getString("poster_path");
                title = jsonArray.getJSONObject(i).getString("original_title");

                favouriteData = new FavouriteData(id);

                if (!(movie_poster.equals("null"))) {
                    favouriteData.setId(id);
                    favouriteData.setTitle(title);
                    favouriteData.setPoster(movie_poster);
                    favouriteArray.add(favouriteData);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return favouriteArray;
    }
}

