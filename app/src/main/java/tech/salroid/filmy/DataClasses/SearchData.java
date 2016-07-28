package tech.salroid.filmy.DataClasses;

/**
 * Created by Home on 7/27/2016.
 */
public class SearchData {


    String movie, poster, id,type;

    public void setType(String type) {
        this.type = type;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMovie(String movie) {
        this.movie = movie;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getId() {
        return id;
    }

    public String getMovie() {
        return movie;
    }

    public String getPoster() {
        return poster;
    }

    public String getType() {
        return type;
    }
}
