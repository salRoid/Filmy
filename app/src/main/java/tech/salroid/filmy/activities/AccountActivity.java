package tech.salroid.filmy.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.salroid.filmy.R;
import tech.salroid.filmy.network_stuff.TmdbVolleySingleton;

public class AccountActivity extends AppCompatActivity {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.logo)
    TextView logo;
    @BindView(R.id.header)
    FrameLayout loginHeader;
    @BindView(R.id.username)
    TextView tvUserName;


    TmdbVolleySingleton tmdbVolleySingleton = TmdbVolleySingleton.getInstance();
    RequestQueue tmdbrequestQueue = tmdbVolleySingleton.getRequestQueue();
    private final int REQUEST_CODE = 000;
    private boolean logged_in;
    private String PREF_NAME = "SESSION_PREFERENCE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        ButterKnife.bind(this);


        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(" ");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/canaro_extra_bold.otf");
        logo.setTypeface(typeface);


        SharedPreferences sp = getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        String session_id = sp.getString("session","");
        String username = sp.getString("username","Not logged in");

        loginHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!logged_in) {

                    Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
                    startActivityForResult(intent, REQUEST_CODE);

                }

            }
        });

        tvUserName.setText(username);
        getProfile(session_id);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item.getItemId() == android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {

            //save this session_id
            //this session id can be used next time to so that logged in is not
            //required next time. ( if session is not expired)

            String session_id = data.getStringExtra("session_id");
            logged_in = true;

            SharedPreferences sp = getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("session",session_id);
            editor.apply();

            getProfile(session_id);

        }

    }

    private void getProfile(String session_id) {


        String PROFILE_URI = "https://api.themoviedb.org/3/account?api_key=b640f55eb6ecc47b3433cfe98d0675b1&session_id=" + session_id;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, PROFILE_URI, null,
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

            String username = response.getString("username");
            if (username != null) {

                tvUserName.setText(username);
                SharedPreferences sp = getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("username",username);
                editor.apply();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
