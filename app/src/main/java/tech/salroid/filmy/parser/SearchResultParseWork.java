package tech.salroid.filmy.parser;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tech.salroid.filmy.data_classes.SearchData;

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
public class SearchResultParseWork {

    private Context context;
    private String result;
    private String getImage, getId, getString, getDate, getExtra;


    public SearchResultParseWork(Context context, String result) {
        this.context = context;
        this.result = result;
    }


    public List<SearchData> parsesearchdata() {
        final List<SearchData> searchArray = new ArrayList<SearchData>();
        SearchData searchData = null;


        try {


            JSONObject jsonobject = new JSONObject(result);

            JSONArray jsonArray = jsonobject.getJSONArray("results");


            for (int i = 0; i < jsonArray.length(); i++) {
                searchData = new SearchData();

                String type_name, type, id, movie_poster, date, extra;

               /* type = jsonArray.getJSONObject(i).getString("type");


                if (type.equals("person")) {
                    getString = "name";
                    getImage = "headshot";
                    getId = "tmdb";
                    getDate = "birthday";
                    getExtra = "birthplace";
                } else {
                    getString = "title";
                    getImage = "poster";
                    getId = "tmdb";
                    getDate = "released";
                    getExtra = "tagline";
                }

                type_name = jsonArray.getJSONObject(i).getJSONObject(type).getString(getString);
                id = jsonArray.getJSONObject(i).getJSONObject(type).getJSONObject("ids").getString(getId);
                movie_poster = jsonArray.getJSONObject(i).getJSONObject(type).getJSONObject("images").getJSONObject(getImage).getString("thumb");
                date = jsonArray.getJSONObject(i).getJSONObject(type).getString(getDate);
                extra = jsonArray.getJSONObject(i).getJSONObject(type).getString(getExtra);*/

                id=jsonArray.getJSONObject(i).getString("id");
                date=jsonArray.getJSONObject(i).getString("release_date");
                movie_poster="http://image.tmdb.org/t/p/w185"+jsonArray.getJSONObject(i).getString("poster_path");
                type_name=jsonArray.getJSONObject(i).getString("original_title");
                type="movie";



                if (!(movie_poster.equals("null") && date.equals("null"))) {
                    searchData.setId(id);
                   // searchData.setExtra(extra);
                    searchData.setDate(date);
                    searchData.setType(type);
                   searchData.setMovie(type_name);
                    searchData.setPoster(movie_poster);
                    searchArray.add(searchData);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return searchArray;
    }


}
