package tech.salroid.filmy.parser;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tech.salroid.filmy.data_classes.WatchlistData;

/*
 * Filmy Application for Android
 * Copyright (c) 2016 Sajal Gupta (http://github.com/salroid).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
