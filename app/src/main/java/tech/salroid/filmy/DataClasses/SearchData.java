package tech.salroid.filmy.dataClasses;


public class SearchData {


    String movie, poster, id, type, date, extra;


    public void setExtra(String extra) {
        this.extra = extra;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDate(String date) {
        this.date = date;
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

    public String getDate() {
        return date;
    }

    public String getExtra() {
        return extra;
    }

    public String getPoster() {
        return poster;
    }

    public String getType() {
        return type;
    }
}
