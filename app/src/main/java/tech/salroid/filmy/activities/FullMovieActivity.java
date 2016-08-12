package tech.salroid.filmy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.salroid.filmy.R;
import tech.salroid.filmy.custom_adapter.CharacterDetailsActivityAdapter;
import tech.salroid.filmy.data_classes.CharacterDetailsData;
import tech.salroid.filmy.parser.CharacterDetailActivityParseWork;

/*
 * Filmy Application for Android
 * Copyright (c) 2016 Sajal Gupta (http://github.com/salroid).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class FullMovieActivity extends AppCompatActivity implements CharacterDetailsActivityAdapter.ClickListener {

    @BindView(R.id.full_movie_recycler)
    RecyclerView full_movie_recycler;
    private String movie_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_movie);
        ButterKnife.bind(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        full_movie_recycler.setLayoutManager(new LinearLayoutManager(FullMovieActivity.this));


        Intent intent = getIntent();
        if (intent != null) {
            movie_result = intent.getStringExtra("cast_json");
            if (getSupportActionBar() != null)
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