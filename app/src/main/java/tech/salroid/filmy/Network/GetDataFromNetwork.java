package tech.salroid.filmy.network;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import tech.salroid.filmy.FilmyApplication;
import tech.salroid.filmy.R;


public class GetDataFromNetwork {

    public static final int MOVIE_DETAILS_CODE = 1;
    public static final int CAST_CODE = 2;
    private DataFetchedListener mDataFetchedListener;

    public  void getMovieDetailsFromNetwork(String movie_id) {

        VolleySingleton volleySingleton = VolleySingleton.getInstance();
        RequestQueue requestQueue = volleySingleton.getRequestQueue();


        //still here we will use the query builder
        //this String is not awesome.

        final String BASE_URL_MOVIE_DETAILS = new String(FilmyApplication.getContext().getResources().getString(R.string.tmdb_movie_base_url)
                + movie_id
                + "?"
                +FilmyApplication.getContext().getResources().getString(R.string.tmdb_api_key)
                +"&append_to_response=trailers");



        JsonObjectRequest jsonObjectRequestForMovieDetails = new JsonObjectRequest(Request.Method.GET, BASE_URL_MOVIE_DETAILS, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        mDataFetchedListener.dataFetched(response.toString(),MOVIE_DETAILS_CODE);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // Log.e("webi", "Volley Error: " + error.getCause());

            }
        }
        );

        requestQueue.add(jsonObjectRequestForMovieDetails);


    }

    public void setDataFetchedListener(DataFetchedListener dataFetchedListener) {

        this.mDataFetchedListener = dataFetchedListener;

    }



    public interface DataFetchedListener{

        void dataFetched(String response, int type);

    }

}
