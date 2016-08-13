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
import tech.salroid.filmy.custom_adapter.MovieDetailsActivityAdapter;
import tech.salroid.filmy.data_classes.MovieDetailsData;
import tech.salroid.filmy.parser.MovieDetailsActivityParseWork;

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

public class FullCastActivity extends AppCompatActivity implements MovieDetailsActivityAdapter.ClickListener {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.full_cast_recycler) RecyclerView full_cast_recycler;


    private String cast_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_cast);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        full_cast_recycler.setLayoutManager(new LinearLayoutManager(FullCastActivity.this));


        Intent intent = getIntent();
        if (intent != null) {
            cast_result = intent.getStringExtra("cast_json");
            if(getSupportActionBar()!=null)
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