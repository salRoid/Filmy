package tech.salroid.filmy.data_classes;


public class MovieDetailsData {


    String cast_name, cast_character, cast_profile, cast_id;
    //String  cast_description;

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
