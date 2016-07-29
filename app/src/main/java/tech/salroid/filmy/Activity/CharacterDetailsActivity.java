package tech.salroid.filmy.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;

import tech.salroid.filmy.DataClasses.CharacterDetailsData;
import tech.salroid.filmy.Datawork.CharacterDetailActivityParseWork;
import tech.salroid.filmy.CustomAdapter.CharacterDetailsActivityAdapter;
import tech.salroid.filmy.R;
import tech.salroid.filmy.DataClasses.MovieData;
import tech.salroid.filmy.Network.VolleySingleton;

public class CharacterDetailsActivity extends AppCompatActivity implements CharacterDetailsActivityAdapter.ClickListener {

    private String character_id;
    private ImageView character_small;
    Context co=this;
    private RecyclerView char_recycler;
    private String character_title=null,movie_json=null;
    private TextView more;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_cast);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        more=(TextView)findViewById(R.id.more);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(movie_json.equals(" ")&& character_title.equals(" "))){
                    Intent intent = new Intent(CharacterDetailsActivity.this, FullMovieActivity.class);
                    intent.putExtra("cast_json",movie_json);
                    intent.putExtra("toolbar_title",character_title);
                    startActivity(intent);

                }


            }
        });

        char_recycler = (RecyclerView) findViewById(R.id.character_movies);
        char_recycler.setLayoutManager(new LinearLayoutManager(CharacterDetailsActivity.this));
        char_recycler.setNestedScrollingEnabled(false);


        Intent intent = getIntent();
        if (intent != null) {
            character_id = intent.getStringExtra("id");
        }

        character_small=(ImageView)findViewById(R.id.cast_img_small);

        getDetailedMovieAndCast();

    }


    @Override
    protected void onResume() {
        super.onResume();


    }

    private void getDetailedMovieAndCast() {



        VolleySingleton volleySingleton = VolleySingleton.getInstance();
        RequestQueue requestQueue  = volleySingleton.getRequestQueue();


        final String BASE_URL_PERSON_DETAIL = "https://api.trakt.tv/people/"+character_id+"?extended=full,images";
        final String BASE_URL_PEOPLE_MOVIES = "https://api.trakt.tv/people/"+character_id+"/movies?extended=images,full";

        JsonObjectRequest personDetailRequest = new JsonObjectRequest(Request.Method.GET, BASE_URL_PERSON_DETAIL , null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        personDetailsParsing(response.toString());


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("webi", "Volley Error: "+error.getCause());

            }
        }
        );

        JsonObjectRequest personMovieDetailRequest = new JsonObjectRequest(Request.Method.GET, BASE_URL_PEOPLE_MOVIES , null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        movie_json=response.toString();
                        more.setVisibility(View.VISIBLE);
                        cast_parseOutput(response.toString());

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("webi", "Volley Error: "+error.getCause());

            }
        }
        );



        requestQueue.add(personDetailRequest);
        requestQueue.add(personMovieDetailRequest);



    }




    @Override
    public void itemClicked(CharacterDetailsData setterGetterchar, int position) {
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra("id",setterGetterchar.getChar_id());
        intent.putExtra("activity",false);
        startActivity(intent);
    }



  void  personDetailsParsing(String detailsResult){


      try {
          JSONObject jsonObject=new JSONObject(detailsResult);

          String char_name= jsonObject.getString("name");
          String char_face=jsonObject.getJSONObject("images").getJSONObject("headshot").getString("thumb");
          //String char_banner=jsonObject.getJSONObject("images").getJSONObject("poster").getString("medium");
          String char_desc=jsonObject.getString("biography");
          String char_birthday=jsonObject.getString("birthday");
          String char_birthplace=jsonObject.getString("birthplace");


          character_title=char_name;

          TextView ch_name = (TextView) findViewById(R.id.actor);
          TextView ch_desc = (TextView) findViewById(R.id.desc);
          TextView ch_birth = (TextView) findViewById(R.id.birth);
          TextView ch_place = (TextView) findViewById(R.id.birth_place);


          ch_name.setText(char_name);
          ch_birth.setText(char_birthday);
          ch_desc.setText(char_desc);
          ch_place.setText(char_birthplace);

          /*Glide.with(co)
                  .load(char_banner)
                    .fitCenter()
                  .into(character_banner);*/

          Glide.with(co)
                  .load(char_face)
                  .fitCenter()
                  .into(character_small);

      } catch (JSONException e) {
          e.printStackTrace();
      }
    }



    private void cast_parseOutput(String cast_result) {
        CharacterDetailActivityParseWork par= new CharacterDetailActivityParseWork(this,cast_result);
        List<CharacterDetailsData> char_list=par.char_parse_cast();
        Boolean size=true;
        CharacterDetailsActivityAdapter char_adapter= new CharacterDetailsActivityAdapter(this,char_list,size);
        char_adapter.setClickListener(this);
        char_recycler.setAdapter(char_adapter);


    }

}
