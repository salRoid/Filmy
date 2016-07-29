package tech.salroid.filmy;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import tech.salroid.filmy.Activity.CharacterDetailsActivity;
import tech.salroid.filmy.Activity.MovieDetailsActivity;
import tech.salroid.filmy.CustomAdapter.SearchResultAdapter;
import tech.salroid.filmy.DataClasses.SearchData;
import tech.salroid.filmy.Datawork.SearchResultParseWork;
import tech.salroid.filmy.Network.VolleySingleton;

public class SearchFragment extends Fragment implements SearchResultAdapter.ClickListener {

    private RecyclerView recycler;
    private Intent intent;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_search,container,false);

        recycler = (RecyclerView) view.findViewById(R.id.search_results_recycler);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }




    @Override
    public void itemClicked(SearchData setterGetter, int position) {
        if (setterGetter.getType().equals("person"))
            intent = new Intent(getActivity(), CharacterDetailsActivity.class);
        else
            intent = new Intent(getActivity(), MovieDetailsActivity.class);
        intent.putExtra("id", setterGetter.getId());
        intent.putExtra("activity", false);

        startActivity(intent);
    }


    public void getSearchedResult(String query) {

        VolleySingleton volleySingleton = VolleySingleton.getInstance();
        RequestQueue requestQueue = volleySingleton.getRequestQueue();

        final String BASE_URL = "https://api.trakt.tv/search/movie,person?query="+query+"&extended=images,full";

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, BASE_URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("webi", response.toString());
                        parseSearchedOutput(response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("webi", "Volley Error: " + error.getCause());

            }
        }
        );

        requestQueue.add(jsonObjectRequest);


    }

    private void parseSearchedOutput(String s) {

        SearchResultParseWork park = new SearchResultParseWork(getActivity(), s);
        List<SearchData> list = park.parsesearchdata();
        SearchResultAdapter sadapter = new SearchResultAdapter(getActivity(), list);
        sadapter.setClickListener(this);
        recycler.setAdapter(sadapter);

    }





}
