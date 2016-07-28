package tech.salroid.filmy.DataClasses;

/**
 * Created by Home on 7/27/2016.
 */
public class SearchData {


    String movie, poster, id, crew_name, crew_id, crew_profile;

    public void setCrew_id(String crew_id) {
        this.crew_id = crew_id;
    }

    public void setCrew_name(String crew_name) {
        this.crew_name = crew_name;
    }

    public void setCrew_profile(String crew_profile) {
        this.crew_profile = crew_profile;
    }

    public String getCrew_id() {
        return crew_id;
    }

    public String getCrew_name() {
        return crew_name;
    }

    public String getCrew_profile() {
        return crew_profile;
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
}
