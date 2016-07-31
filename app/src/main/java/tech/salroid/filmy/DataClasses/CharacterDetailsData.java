package tech.salroid.filmy.DataClasses;

/**
 * Created by Home on 7/23/2016.
 */
public class CharacterDetailsData {


    String char_movie, char_role, char_id, Charmovie_img;
    int char_date;


    public void setCharmovie_img(String charmovie_img) {
        Charmovie_img = charmovie_img;
    }

    public void setChar_date(int char_date) {
        this.char_date = char_date;
    }

    public void setChar_id(String char_id) {
        this.char_id = char_id;
    }

    public void setChar_movie(String char_movie) {
        this.char_movie = char_movie;
    }

    public void setChar_role(String char_role) {
        this.char_role = char_role;
    }


    public int getChar_date() {
        return char_date;
    }

    public String getChar_id() {
        return char_id;
    }

    public String getChar_movie() {
        return char_movie;
    }

    public String getChar_role() {
        return char_role;
    }

    public String getCharmovie_img() {
        return Charmovie_img;
    }


}
