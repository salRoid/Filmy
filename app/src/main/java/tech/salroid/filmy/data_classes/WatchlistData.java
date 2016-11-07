package tech.salroid.filmy.data_classes;

/**
 * Created by Home on 11/7/2016.
 */

public class WatchlistData {

    String fav_id, fav_title, fav_poster;

    public String getFav_id() {
        return fav_id;
    }

    public void setFav_id(String fav_id) {
        this.fav_id = fav_id;
    }

    public String getFav_poster() {
        return fav_poster;
    }

    public void setFav_poster(String fav_poster) {
        this.fav_poster = fav_poster;
    }

    public String getFav_title() {
        return fav_title;
    }

    public void setFav_title(String fav_title) {
        this.fav_title = fav_title;
    }

}
