package tech.salroid.filmy.parser;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tech.salroid.filmy.data_classes.MovieDetailsData;

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
public class MovieDetailsActivityParseWork {

    private Context context;
    private String cast_result;

    public MovieDetailsActivityParseWork(Context context, String cast_result) {
        this.context = context;
        this.cast_result = cast_result;
    }

    public List<MovieDetailsData> parse_cast() {


        final List<MovieDetailsData> setterGettercastArray = new ArrayList<MovieDetailsData>();
        MovieDetailsData setterGettercast = null;

        try {
            JSONObject jsonObject = new JSONObject(cast_result);

            JSONArray jsonArray = jsonObject.getJSONArray("cast");

            for (int i = 0; i < jsonArray.length(); i++) {

                setterGettercast = new MovieDetailsData();

                String id = (jsonArray.getJSONObject(i)).getJSONObject("person").getJSONObject("ids").getString("trakt");
                String character = (jsonArray.getJSONObject(i)).getString("character");
                String name = (jsonArray.getJSONObject(i)).getJSONObject("person").getString("name");
                String cast_poster = (jsonArray.getJSONObject(i)).getJSONObject("person").getJSONObject("images").getJSONObject("headshot").getString("thumb");

                setterGettercast.setCast_character(character);
                setterGettercast.setCast_name(name);
                setterGettercast.setCast_profile(cast_poster);
                setterGettercast.setCast_id(id);

                setterGettercastArray.add(setterGettercast);


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return setterGettercastArray;
    }

}
