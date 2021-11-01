package tech.salroid.filmy.data_classes;
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

public class CharacterDetailsData {


     String char_movie, char_role, char_id, Charmovie_img;
    //  int char_date;


    /*public void setChar_date(int char_date) {
        this.char_date = char_date;
    }*/


    public String getChar_id() {
        return char_id;
    }

    public void setChar_id(String char_id) {
        this.char_id = char_id;
    }

    public String getChar_movie() {
        return char_movie;
    }

    public void setChar_movie(String char_movie) {
        this.char_movie = char_movie;
    }

    public String getChar_role() {
        return char_role;
    }

    public void setChar_role(String char_role) {
        this.char_role = char_role;
    }

    public String getCharmovie_img() {
        return Charmovie_img;
    }

    public void setCharmovie_img(String charmovie_img) {
        Charmovie_img = charmovie_img;
    }


}


