package tech.salroid.filmy.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONObject;
import java.util.List;
import tech.salroid.filmy.Activity.CharacterDetailsActivity;
import tech.salroid.filmy.Activity.FullCastActivity;
import tech.salroid.filmy.CustomAdapter.MovieDetailsActivityAdapter;
import tech.salroid.filmy.DataClasses.MovieDetailsData;
import tech.salroid.filmy.Parsers.MovieDetailsActivityParseWork;
import tech.salroid.filmy.Network.VolleySingleton;
import tech.salroid.filmy.R;

/**
 * Created by R Ankit on 05-08-2016.
 */

public class CastFragment extends Fragment implements View.OnClickListener, MovieDetailsActivityAdapter.ClickListener {


    private String cast_json;
    private RecyclerView cast_recycler;
    private TextView more;
    private String movieId,movieTitle;


    public static CastFragment newInstance(String movie_Id, String movie_Title) {
        CastFragment fragment = new CastFragment();
        Bundle args = new Bundle();
        args.putString("movie_id", movie_Id);
        args.putString("movie_title", movie_Title);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.cast_fragment,container,false);

        more = (TextView) view.findViewById(R.id.more);

        cast_recycler = (RecyclerView) view.findViewById(R.id.cast_recycler);
        cast_recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        cast_recycler.setNestedScrollingEnabled(false);
        more.setOnClickListener(this);


        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle savedBundle = getArguments();

        if(savedBundle!=null){

            movieId = savedBundle.getString("movie_id");
            movieTitle = savedBundle.getString("movie_title");

        }

        getCastFromNetwork();

    }


    private void getCastFromNetwork() {


        final String BASE_MOVIE_CAST_DETAILS = new String("https://api.trakt.tv/movies/" + movieId + "/people?extended=images");
        JsonObjectRequest jsonObjectRequestForMovieCastDetails = new JsonObjectRequest(Request.Method.GET, BASE_MOVIE_CAST_DETAILS, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        cast_json = response.toString();
                        cast_parseOutput(response.toString());



                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("webi", "Volley Error: " + error.getCause());

            }
        }
        );


        VolleySingleton volleySingleton = VolleySingleton.getInstance();
        RequestQueue requestQueue = volleySingleton.getRequestQueue();
        requestQueue.add(jsonObjectRequestForMovieCastDetails);
    }


    private void cast_parseOutput(String cast_result) {

        MovieDetailsActivityParseWork par = new MovieDetailsActivityParseWork(getActivity(), cast_result);
        List<MovieDetailsData> cast_list = par.parse_cast();
        Boolean size = true;
        MovieDetailsActivityAdapter cast_adapter = new MovieDetailsActivityAdapter(getActivity(), cast_list, size);
        cast_adapter.setClickListener(this);
        cast_recycler.setAdapter(cast_adapter);
        more.setVisibility(View.VISIBLE);

    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.more) {

            if (cast_json != null && movieTitle != null) {

                Intent intent = new Intent(getActivity(), FullCastActivity.class);
                intent.putExtra("cast_json", cast_json);
                intent.putExtra("toolbar_title", movieTitle);
                startActivity(intent);

            }
        }


    }

    @Override
    public void itemClicked(MovieDetailsData setterGetter, int position) {
        Intent intent = new Intent(getActivity(), CharacterDetailsActivity.class);
        intent.putExtra("id", setterGetter.getCast_id());
        startActivity(intent);
    }

}
