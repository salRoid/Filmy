package tech.salroid.filmy.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import java.util.List;

import tech.salroid.filmy.CustomAdapter.SearchResultAdapter;
import tech.salroid.filmy.DataClasses.SearchData;
import tech.salroid.filmy.Datawork.SearchResultParseWork;
import tech.salroid.filmy.Network.VolleySingleton;
import tech.salroid.filmy.R;

public class SearchActivity extends AppCompatActivity implements SearchResultAdapter.ClickListener {

    private RecyclerView recycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recycler = (RecyclerView) findViewById(R.id.search_results_recycler);

        recycler.setLayoutManager(new LinearLayoutManager(SearchActivity.this));


        getSearchedResult();


    }



    @Override
    public void itemClicked(SearchData setterGetter, int position) {
        Intent intent = new Intent(this,MovieDetailsActivity.class);
        intent.putExtra("id",setterGetter.getId());
        intent.putExtra("activity",false);

        startActivity(intent);
    }




    private void getSearchedResult() {
        VolleySingleton volleySingleton = VolleySingleton.getInstance();
        RequestQueue requestQueue = volleySingleton.getRequestQueue();

        final String BASE_URL = "https://api.trakt.tv/search/movie?query=iron-man&extended=images";


        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, BASE_URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("webi",response.toString());
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

        SearchResultParseWork park = new SearchResultParseWork(this, s);
        List<SearchData> list = park.parsesearchdata();
        SearchResultAdapter sadapter = new SearchResultAdapter(this, list);
        sadapter.setClickListener(this);
        recycler.setAdapter(sadapter);

    }




}
