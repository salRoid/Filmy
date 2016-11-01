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

public class SimilarMoviesData {
    String movie_banner,movie_title,movie_id;

    public void setMovie_id(String movie_id) {
        this.movie_id = movie_id;
    }


    public void setMovie_title(String movie_title) {
        this.movie_title = movie_title;
    }

    public void setMovie_banner(String movie_banner) {
        this.movie_banner = movie_banner;
    }

    public String getMovie_banner() {
        return movie_banner;
    }

    public String getMovie_title() {
        return movie_title;
    }

    public String getMovie_id() {
        return movie_id;
    }
}
