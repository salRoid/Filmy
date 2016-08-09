package tech.salroid.filmy.dataClasses;

public class MovieData {

    String movie, poster, id;


    int year;

    public void setYear(int year) {
        this.year = year;
    }


    public int getYear() {
        return year;
    }


    public void setMovie(String movie) {
        this.movie = movie;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getId() {
        return id;
    }

    public String getPoster() {
        return poster;
    }

    public String getMovie() {
        return movie;
    }
}
