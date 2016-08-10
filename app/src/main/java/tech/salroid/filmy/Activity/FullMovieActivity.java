package tech.salroid.filmy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import java.util.List;
import tech.salroid.filmy.customAdapter.CharacterDetailsActivityAdapter;
import tech.salroid.filmy.dataClasses.CharacterDetailsData;
import tech.salroid.filmy.parsers.CharacterDetailActivityParseWork;
import tech.salroid.filmy.R;

public class FullMovieActivity extends AppCompatActivity implements CharacterDetailsActivityAdapter.ClickListener {


    private String movie_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_movie);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        RecyclerView full_movie_recycler = (RecyclerView) findViewById(R.id.full_movie_recycler);
        full_movie_recycler.setLayoutManager(new LinearLayoutManager(FullMovieActivity.this));


        Intent intent = getIntent();
        if (intent != null) {
            movie_result = intent.getStringExtra("cast_json");
            if (getSupportActionBar()!=null)
            getSupportActionBar().setTitle(intent.getStringExtra("toolbar_title"));
        }


        CharacterDetailActivityParseWork par = new CharacterDetailActivityParseWork(this, movie_result);
        List<CharacterDetailsData> char_list = par.char_parse_cast();
        CharacterDetailsActivityAdapter char_adapter = new CharacterDetailsActivityAdapter(this, char_list, false);
        char_adapter.setClickListener(this);
        full_movie_recycler.setAdapter(char_adapter);

    }


    @Override
    public void itemClicked(CharacterDetailsData setterGetterChar, int position) {
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra("id", setterGetterChar.getChar_id());
        intent.putExtra("network_applicable", true);
        intent.putExtra("activity", false);
        startActivity(intent);

    }


}
