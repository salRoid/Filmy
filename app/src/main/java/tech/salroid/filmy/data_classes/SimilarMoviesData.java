package tech.salroid.filmy.data_classes;

/**
 * Created by Home on 11/1/2016.
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
