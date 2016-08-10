package tech.salroid.filmy.parsers;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import tech.salroid.filmy.data_classes.SearchData;

/**
 * Created by Home on 7/27/2016.
 */
public class SearchResultParseWork {

    private Context context;
    private String result;
    private String getImage, getId, getString, getDate, getExtra;


    public SearchResultParseWork(Context context, String result) {
        this.context = context;
        this.result = result;
    }


    public List<SearchData> parsesearchdata() {
        final List<SearchData> searchArray = new ArrayList<SearchData>();
        SearchData searchData = null;


        try {


            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                searchData = new SearchData();

                String type_name, type, id, movie_poster, date, extra;

                type = jsonArray.getJSONObject(i).getString("type");


                if (type.equals("person")) {
                    getString = "name";
                    getImage = "headshot";
                    getId = "trakt";
                    getDate = "birthday";
                    getExtra = "birthplace";
                } else {
                    getString = "title";
                    getImage = "poster";
                    getId = "imdb";
                    getDate = "released";
                    getExtra = "tagline";
                }

                type_name = jsonArray.getJSONObject(i).getJSONObject(type).getString(getString);
                id = jsonArray.getJSONObject(i).getJSONObject(type).getJSONObject("ids").getString(getId);
                movie_poster = jsonArray.getJSONObject(i).getJSONObject(type).getJSONObject("images").getJSONObject(getImage).getString("thumb");
                date = jsonArray.getJSONObject(i).getJSONObject(type).getString(getDate);
                extra = jsonArray.getJSONObject(i).getJSONObject(type).getString(getExtra);


                if (!(movie_poster.equals("null") && date.equals("null") && extra.equals("null"))) {
                    searchData.setId(id);
                    searchData.setExtra(extra);
                    searchData.setDate(date);
                    searchData.setType(type);
                    searchData.setMovie(type_name);
                    searchData.setPoster(movie_poster);
                    searchArray.add(searchData);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return searchArray;
    }


}
