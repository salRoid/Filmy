package tech.salroid.filmy.Parsers;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tech.salroid.filmy.DataClasses.MovieDetailsData;

/**
 * Created by Home on 7/22/2016.
 */
public class MovieDetailsActivityParseWork {

    private Context context;
    private String cast_result;

    public MovieDetailsActivityParseWork(Context context, String cast_result) {
        this.context = context;
        this.cast_result = cast_result;
    }

    public List<MovieDetailsData> parse_cast() {


        final List<MovieDetailsData> setterGettercastArray = new ArrayList<MovieDetailsData>();
        MovieDetailsData setterGettercast = null;

        try {
            JSONObject jsonObject = new JSONObject(cast_result);

            JSONArray jsonArray = jsonObject.getJSONArray("cast");

            for (int i = 0; i < jsonArray.length(); i++) {

                setterGettercast = new MovieDetailsData();

                String id = (jsonArray.getJSONObject(i)).getJSONObject("person").getJSONObject("ids").getString("trakt");
                String character = (jsonArray.getJSONObject(i)).getString("character");
                String name = (jsonArray.getJSONObject(i)).getJSONObject("person").getString("name");
                String cast_poster = (jsonArray.getJSONObject(i)).getJSONObject("person").getJSONObject("images").getJSONObject("headshot").getString("thumb");

                setterGettercast.setCast_character(character);
                setterGettercast.setCast_name(name);
                setterGettercast.setCast_profile(cast_poster);
                setterGettercast.setCast_id(id);

                setterGettercastArray.add(setterGettercast);


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return setterGettercastArray;
    }

}
