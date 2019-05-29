package tech.salroid.filmy.parser;

import android.content.ContentValues;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;

import tech.salroid.filmy.database.FilmContract;
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

public class MainActivityParseWork {

    private final String LOG_TAG = MainActivityParseWork.class.getSimpleName();
    // private final int type;
    private Context context;
    private String result;
    private String imdb_id;


    public MainActivityParseWork(Context context, String result) {
        this.context = context;
        this.result = result;
        // this.type=type;
    }

    public void parse() {

        //final List<MovieData> movieDataArrayList = new ArrayList<MovieData>();
        //MovieData movieData = null;

        try {

            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("results");

            Vector<ContentValues> cVVector = new Vector<ContentValues>(jsonArray.length());

            for (int i = 0; i < jsonArray.length(); i++) {

                //movieData = new MovieData();
                String title, poster, id;

                title = (jsonArray.getJSONObject(i)).getString("title");
                poster = (jsonArray.getJSONObject(i).getString("poster_path"));
                id = (jsonArray.getJSONObject(i)).getString("id");

                String[] temp_year = (jsonArray.getJSONObject(i)).getString("release_date").split("-");
                String year = temp_year[0];


                String trimmedQuery = (title.toLowerCase()).trim();

                String finalQuery = trimmedQuery.replace(" ", "-");
                finalQuery = finalQuery.replace("'", "-");
                String slug = (finalQuery.replace(":", "")) + "-" + year;


                //movieData.setMovie(title);
                //movieData.setYear(year);
                //movieData.setId(id);
                //movieData.setPoster(poster);

                //movieDataArrayList.add(movieData);

                // Insert the new weather information into the database

                ContentValues movieValues = new ContentValues();

                if (!(poster.equals("null"))) {


                    movieValues.put(FilmContract.MoviesEntry.MOVIE_ID, id);
                    movieValues.put(FilmContract.MoviesEntry.MOVIE_TITLE, title);
                    movieValues.put(FilmContract.MoviesEntry.MOVIE_YEAR, year);
                    movieValues.put(FilmContract.MoviesEntry.MOVIE_POSTER_LINK, "http://image.tmdb.org/t/p/w185" + poster);


                    cVVector.add(movieValues);
                }

            }
            int inserted = 0;
            // add to database
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);

                context.getContentResolver().delete(FilmContract.MoviesEntry.CONTENT_URI, null, null);
                inserted = context.getContentResolver().bulkInsert(FilmContract.MoviesEntry.CONTENT_URI, cvArray);

            }

             //Log.d(LOG_TAG, "Fetching Complete. " + inserted + " Inserted");


        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }

    public void parseUpcoming() {

        try {

            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("results");

            Vector<ContentValues> cVVector = new Vector<ContentValues>(jsonArray.length());

            for (int i = 0; i < jsonArray.length(); i++) {


                String title, poster, id;

                title = (jsonArray.getJSONObject(i)).getString("title");
                poster = (jsonArray.getJSONObject(i).getString("poster_path"));
                id = (jsonArray.getJSONObject(i)).getString("id");

                String[] temp_year = (jsonArray.getJSONObject(i)).getString("release_date").split("-");
                String year = temp_year[0];


                String trimmedQuery = (title.toLowerCase()).trim();

                String finalQuery = trimmedQuery.replace(" ", "-");
                finalQuery = finalQuery.replace("'", "-");
                String slug = (finalQuery.replace(":", "")) + "-" + year;


                // Insert the new weather information into the database

                ContentValues movieValues = new ContentValues();

                if (!(poster.equals("null"))) {


                    movieValues.put(FilmContract.MoviesEntry.MOVIE_ID, id);
                    movieValues.put(FilmContract.MoviesEntry.MOVIE_TITLE, title);
                    movieValues.put(FilmContract.MoviesEntry.MOVIE_YEAR, year);
                    movieValues.put(FilmContract.MoviesEntry.MOVIE_POSTER_LINK, "http://image.tmdb.org/t/p/w185" + poster);


                    cVVector.add(movieValues);
                }
            }
            int inserted = 0;
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);

                context.getContentResolver().delete(FilmContract.UpComingMoviesEntry.CONTENT_URI, null, null);
                inserted = context.getContentResolver().bulkInsert(FilmContract.UpComingMoviesEntry.CONTENT_URI, cvArray);

            }



        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }


    public void inTheatres() {
        try {


            JSONObject jsonObject = new JSONObject(result);

            JSONArray jsonArray = jsonObject.getJSONArray("results");

            Vector<ContentValues> cVVector = new Vector<ContentValues>(jsonArray.length());

            for (int i = 0; i < jsonArray.length(); i++) {

                //movieData = new MovieData();
                String title, poster, id;
                title = (jsonArray.getJSONObject(i)).getString("title");
                poster = (jsonArray.getJSONObject(i).getString("poster_path"));
                id = (jsonArray.getJSONObject(i)).getString("id");


                String[] temp_year = (jsonArray.getJSONObject(i)).getString("release_date").split("-");
                String year = temp_year[0];

                String trimmedQuery = (title.toLowerCase()).trim();
                String finalQuery = trimmedQuery.replace(" ", "-");
                finalQuery = finalQuery.replace("'", "-");
                String slug = (finalQuery.replace(":", "")) + "-" + year;


                if (!(poster.equals("null"))) {

                    ContentValues movieValues = new ContentValues();

                    movieValues.put(FilmContract.MoviesEntry.MOVIE_ID, id);
                    movieValues.put(FilmContract.MoviesEntry.MOVIE_TITLE, title);
                    movieValues.put(FilmContract.MoviesEntry.MOVIE_YEAR, year);
                    movieValues.put(FilmContract.MoviesEntry.MOVIE_POSTER_LINK, "http://image.tmdb.org/t/p/w185" + poster);

                    cVVector.add(movieValues);
                }
            }
            int inserted = 0;
            // add to database
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);

                context.getContentResolver().delete(FilmContract.InTheatersMoviesEntry.CONTENT_URI, null, null);
                inserted = context.getContentResolver().bulkInsert(FilmContract.InTheatersMoviesEntry.CONTENT_URI, cvArray);

            }


        } catch (JSONException e1) {
            e1.printStackTrace();
        }

    }

}
