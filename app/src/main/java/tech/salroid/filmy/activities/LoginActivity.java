package tech.salroid.filmy.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.salroid.filmy.R;
import tech.salroid.filmy.customs.CustomToast;
import tech.salroid.filmy.network_stuff.TmdbVolleySingleton;

public class LoginActivity extends AppCompatActivity {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.logo)
    TextView logo;

    private ProgressDialog progressDialog;
    TmdbVolleySingleton tmdbVolleySingleton = TmdbVolleySingleton.getInstance();
    RequestQueue tmdbrequestQueue = tmdbVolleySingleton.getRequestQueue();
    private boolean tokenization = false;
    private String requestToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(" ");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/canaro_extra_bold.otf");
        logo.setTypeface(typeface);

        loginNow();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }


    public void loginNow() {


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("TMDB Login");
        progressDialog.setMessage("Please wait...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        logInBackground();


    }

    private void logInBackground() {

        final String BASE_URL = "https://api.themoviedb.org/3/authentication/token/new?api_key=b640f55eb6ecc47b3433cfe98d0675b1";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, BASE_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        parseOutput(response);
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("webi", "Volley Error: " + error.getCause());

            }
        });
        tmdbrequestQueue.add(jsonObjectRequest);


    }

    private void parseOutput(JSONObject response) {

        try {

            boolean status = response.getBoolean("success");

            if (status) {

                requestToken = response.getString("request_token");
                tokenization = true;
                validateToken(requestToken);


            } else {

                progressDialog.dismiss();
                CustomToast.show(this, "Failed to login", false);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void validateToken(final String requestToken) {

        String url = "https://www.themoviedb.org/authenticate/" + requestToken;
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorAccent));
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(url));

    }


    @Override
    protected void onResume() {
        super.onResume();

        if (tokenization && requestToken!=null){

            final String SESSION_QUERY = "https://api.themoviedb.org/3/authentication/session/new?api_key=b640f55eb6ecc47b3433cfe98d0675b1&request_token=" + requestToken;
            querySession(SESSION_QUERY);
        }

    }


    private void querySession(String session_query) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, session_query, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        parseSession(response);
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("webi", "Volley Error: " + error.getCause());

            }
        });

        tmdbrequestQueue.add(jsonObjectRequest);


    }

    private void parseSession(JSONObject response) {

        try {

            boolean status = response.getBoolean("success");

            if (status) {

                String session_id = response.getString("session_id");
                progressDialog.dismiss();

                Intent resultIntent = new Intent();
                resultIntent.putExtra("session_id", session_id);
                setResult(RESULT_OK, resultIntent);

                finish();

            } else {

                progressDialog.dismiss();
                CustomToast.show(this, "Failed to login", false);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
