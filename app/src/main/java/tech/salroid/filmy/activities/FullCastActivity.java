package tech.salroid.filmy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.List;

import tech.salroid.filmy.R;
import tech.salroid.filmy.custom_adapter.MovieDetailsActivityAdapter;
import tech.salroid.filmy.data_classes.MovieDetailsData;
import tech.salroid.filmy.parser.MovieDetailsActivityParseWork;

public class FullCastActivity extends AppCompatActivity implements MovieDetailsActivityAdapter.ClickListener {

    private String cast_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_cast);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        RecyclerView full_cast_recycler = (RecyclerView) findViewById(R.id.full_cast_recycler);
        full_cast_recycler.setLayoutManager(new LinearLayoutManager(FullCastActivity.this));


        Intent intent = getIntent();
        if (intent != null) {
            cast_result = intent.getStringExtra("cast_json");
            if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(intent.getStringExtra("toolbar_title"));
        }


        MovieDetailsActivityParseWork par = new MovieDetailsActivityParseWork(this, cast_result);
        List<MovieDetailsData> cast_list = par.parse_cast();

        MovieDetailsActivityAdapter full_cast_adapter = new MovieDetailsActivityAdapter(this, cast_list, false);
        full_cast_adapter.setClickListener(this);
        full_cast_recycler.setAdapter(full_cast_adapter);


    }

    @Override
    public void itemClicked(MovieDetailsData setterGetter, int position) {
        Intent intent = new Intent(this, CharacterDetailsActivity.class);
        intent.putExtra("id", setterGetter.getCast_id());
        startActivity(intent);
    }
}