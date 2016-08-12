package tech.salroid.filmy.parser;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tech.salroid.filmy.data_classes.CharacterDetailsData;

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
public class CharacterDetailActivityParseWork {

    private Context context;
    private String char_result;


    public CharacterDetailActivityParseWork(Context context, String char_result) {
        this.context = context;
        this.char_result = char_result;
    }


    public List<CharacterDetailsData> char_parse_cast() {

        final List<CharacterDetailsData> setterGettercharArray = new ArrayList<CharacterDetailsData>();
        CharacterDetailsData setterGetterchar = null;


        try {
            JSONObject jsonObject = new JSONObject(char_result);

            JSONArray jsonArray = jsonObject.getJSONArray("cast");

            for (int i = 0; i < jsonArray.length(); i++) {

                setterGetterchar = new CharacterDetailsData();

                String role, movie, mov_id, img;

                role = jsonArray.getJSONObject(i).getString("character");
                movie = jsonArray.getJSONObject(i).getJSONObject("movie").getString("title");
                mov_id = (jsonArray.getJSONObject(i).getJSONObject("movie")).getJSONObject("ids").getString("imdb");
                img = jsonArray.getJSONObject(i).getJSONObject("movie").getJSONObject("images").getJSONObject("poster").getString("thumb");

                setterGetterchar.setChar_movie(movie);
                setterGetterchar.setChar_id(mov_id);
                setterGetterchar.setChar_role(role);
                setterGetterchar.setCharmovie_img(img);

                setterGettercharArray.add(setterGetterchar);


              /*  int year= jsonArray.getJSONObject(i).getJSONObject("movie").getInt("year");
                setterGetterchar.setChar_date(year);*/

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


        return setterGettercharArray;
    }


}
