package tech.salroid.filmy.Service;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import org.json.JSONArray;
import tech.salroid.filmy.Datawork.MainActivityParseWork;
import tech.salroid.filmy.Network.VolleySingleton;




public class FilmyService extends IntentService {

    public FilmyService() {
        super("FilmyService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        getData();

    }

    private void getData() {

        VolleySingleton volleySingleton = VolleySingleton.getInstance();
        RequestQueue requestQueue = volleySingleton.getRequestQueue();

        final String BASE_URL = "https://api.trakt.tv/movies/trending?extended=images,page=1&limit=30";


        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, BASE_URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        parseOutput(response.toString());
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


    private void parseOutput(String result) {


      MainActivityParseWork pa = new MainActivityParseWork(this, result);
      pa.parseupcoming();
    }


    public static class AlarmReciever extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {

            Intent sendIntent = new Intent(context,FilmyService.class);
            context.startService(sendIntent);
        }
    }


}
