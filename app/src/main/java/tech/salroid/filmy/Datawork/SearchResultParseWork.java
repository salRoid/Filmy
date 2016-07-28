package tech.salroid.filmy.Datawork;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import tech.salroid.filmy.DataClasses.SearchData;

/**
 * Created by Home on 7/27/2016.
 */
public class SearchResultParseWork {

    private Context context;
    private String result;


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


                String movie_name= jsonArray.getJSONObject(i).getJSONObject("movie").getString("title");
                String type=jsonArray.getJSONObject(i).getString("type");
                String id= jsonArray.getJSONObject(i).getJSONObject("movie").getJSONObject("ids").getString("imdb");
                String movie_poster=jsonArray.getJSONObject(i).getJSONObject("movie").getJSONObject("images").getJSONObject("poster").getString("thumb");

                searchData.setId(id);
                searchData.setMovie(movie_name);
                searchData.setPoster(movie_poster);

                searchArray.add(searchData);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return searchArray;
    }


}
