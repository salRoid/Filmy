package tech.salroid.filmy.parser;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tech.salroid.filmy.data_classes.PersonMovieDetailsData;

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
public class CharacterDetailsActivityParseWork {

    private Context context;
    private String moviesResponse;

    public CharacterDetailsActivityParseWork(Context context, String moviesResponse) {
        this.context = context;
        this.moviesResponse = moviesResponse;
    }

    public List<PersonMovieDetailsData> parsePersonMovies() {
        final List<PersonMovieDetailsData> allMovies = new ArrayList<PersonMovieDetailsData>();
        PersonMovieDetailsData movie = null;
        try {
            JSONObject jsonObject = new JSONObject(moviesResponse);
            JSONArray jsonArray = jsonObject.getJSONArray("cast");
            for (int i = 0; i < jsonArray.length(); i++) {
                String playedRole = jsonArray.getJSONObject(i).getString("character");
                String movieTitle = jsonArray.getJSONObject(i).getString("original_title");
                String movieId = jsonArray.getJSONObject(i).getString("id");
                String moviePoster = "http://image.tmdb.org/t/p/w45"
                        + jsonArray.getJSONObject(i).getString("poster_path");

                movie = new PersonMovieDetailsData();
                movie.setMovieTitle(movieTitle);
                movie.setRolePlayed(playedRole);
                movie.setMovieId(movieId);
                movie.setMoviePoster(moviePoster);

                allMovies.add(movie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return allMovies;
    }
}