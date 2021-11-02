package tech.salroid.filmy.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import tech.salroid.filmy.data.SearchData;

public class SearchResultParseWork {

    private String result;

    public SearchResultParseWork(String result) {
        this.result = result;
    }

    public List<SearchData> parseSearchData() {
        final List<SearchData> searchArray = new ArrayList();
        SearchData searchData;

        try {
            JSONObject jsonobject = new JSONObject(result);
            JSONArray jsonArray = jsonobject.getJSONArray("results");

            for (int i = 0; i < jsonArray.length(); i++) {

                String id = jsonArray.getJSONObject(i).getString("id");
                String date = jsonArray.getJSONObject(i).getString("release_date");
                String moviePoster = "http://image.tmdb.org/t/p/w185" + jsonArray.getJSONObject(i).getString("poster_path");
                String title = jsonArray.getJSONObject(i).getString("original_title");
                String type = "movie";

                searchData = new SearchData(id);

                if (!(moviePoster.equals("null") && date.equals("null"))) {
                    searchData.setDate(date);
                    searchData.setType(type);
                    searchData.setMovie(title);
                    searchData.setPoster(moviePoster);
                    searchArray.add(searchData);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return searchArray;
    }
}
