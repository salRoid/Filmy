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

public class CastDetailsData {


    String cast_name, cast_character, cast_profile, cast_id;
    String cast_description;

    public String getCast_name() {
        return cast_name;
    }

    public void setCast_name(String cast_name) {
        this.cast_name = cast_name;
    }

    public String getCast_id() {
        return cast_id;
    }

    /*  public void setCast_description(String cast_description) {
          this.cast_description = cast_description;
      }
  */
    public void setCast_id(String cast_id) {
        this.cast_id = cast_id;
    }

    public String getCast_profile() {
        return cast_profile;
    }

    public void setCast_profile(String cast_profile) {
        this.cast_profile = cast_profile;
    }

    public String getCast_description() {
        return cast_description;
    }


    public String getCast_character() {
        return cast_character;
    }

   /* public String getCast_description() {
        return cast_description;
    }*/

    public void setCast_character(String cast_character) {
        this.cast_character = cast_character;
    }


}