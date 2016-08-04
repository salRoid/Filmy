package tech.salroid.filmy;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import org.json.JSONArray;
import java.util.List;
import tech.salroid.filmy.Activity.CharacterDetailsActivity;
import tech.salroid.filmy.Activity.MovieDetailsActivity;
import tech.salroid.filmy.Custom.BreathingProgress;
import tech.salroid.filmy.CustomAdapter.SearchResultAdapter;
import tech.salroid.filmy.DataClasses.SearchData;
import tech.salroid.filmy.Datawork.SearchResultParseWork;
import tech.salroid.filmy.Network.VolleySingleton;

public class SearchFragment extends Fragment implements SearchResultAdapter.ClickListener {

    private RecyclerView recycler;
    private Intent intent;
    BreathingProgress breathingProgress;
    SearchResultAdapter sadapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_search, container, false);

        recycler = (RecyclerView) view.findViewById(R.id.search_results_recycler);
        recycler.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL));
        breathingProgress = (BreathingProgress) view.findViewById(R.id.breathingProgress);


        return view;
    }


    @Override
    public void itemClicked(SearchData setterGetter, int position) {

        if (setterGetter.getType().equals("person"))
            intent = new Intent(getActivity(), CharacterDetailsActivity.class);
        else{
            intent = new Intent(getActivity(), MovieDetailsActivity.class);
            intent.putExtra("network_applicable",true);
        }


        intent.putExtra("id", setterGetter.getId());
        intent.putExtra("activity", false);

        startActivity(intent);
    }


    public void getSearchedResult(String query) {


        String trimmedQuery = query.trim();
        String finalQuery = trimmedQuery.replace(" ","-");

        VolleySingleton volleySingleton = VolleySingleton.getInstance();
        RequestQueue requestQueue = volleySingleton.getRequestQueue();

        final String BASE_URL = "https://api.trakt.tv/search/movie,person?query=" + finalQuery + "&extended=images,full";

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, BASE_URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        parseSearchedOutput(response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //Log.e("webi", "Volley Error: " + error.getCause());

            }
        }
        );

        requestQueue.add(jsonObjectRequest);


    }

    private void parseSearchedOutput(String s) {

        SearchResultParseWork park = new SearchResultParseWork(getActivity(), s);
        List<SearchData> list = park.parsesearchdata();
        sadapter = new SearchResultAdapter(getActivity(),list);
        recycler.setAdapter(sadapter);
        sadapter.setClickListener(this);

        hideProgress();

    }


    public void showProgress() {



        if (breathingProgress!=null && recycler!=null){

            breathingProgress.setVisibility(View.VISIBLE);
            recycler.setVisibility(View.INVISIBLE);

        }
    }


    public void  hideProgress() {

        if (breathingProgress!=null && recycler!=null){

            breathingProgress.setVisibility(View.INVISIBLE);
            recycler.setVisibility(View.VISIBLE);

        }
    }
}
