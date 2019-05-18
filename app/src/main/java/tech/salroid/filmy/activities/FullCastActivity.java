package tech.salroid.filmy.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.salroid.filmy.R;
import tech.salroid.filmy.custom_adapter.CastAdapter;
import tech.salroid.filmy.data_classes.CastDetailsData;
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

public class FullCastActivity extends AppCompatActivity implements CastAdapter.ClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.full_cast_recycler)
    RecyclerView full_cast_recycler;


    private String cast_result;
    private boolean nightMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        nightMode = sp.getBoolean("dark", false);
        if (nightMode)
            setTheme(R.style.AppTheme_Base_Dark);
        else
            setTheme(R.style.AppTheme_Base);
        
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_full_cast);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        full_cast_recycler.setLayoutManager(new LinearLayoutManager(FullCastActivity.this));


        Intent intent = getIntent();
        if (intent != null) {
            cast_result = intent.getStringExtra("cast_json");
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle(intent.getStringExtra("toolbar_title"));
            }
        }


        MovieDetailsActivityParseWork par = new MovieDetailsActivityParseWork(this, cast_result);
        List<CastDetailsData> cast_list = par.parse_cast();
        CastAdapter full_cast_adapter = new CastAdapter(this, cast_list, false);
        full_cast_adapter.setClickListener(this);
        full_cast_recycler.setAdapter(full_cast_adapter);

    }

    @Override
    public void itemClicked(CastDetailsData setterGetter, int position, View view) {
        Intent intent = new Intent(this, CharacterDetailsActivity.class);
        intent.putExtra("id", setterGetter.getCastId());

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {

            Pair<View, String> p1 = Pair.create(view.findViewById(R.id.cast_poster), "profile");
            Pair<View, String> p2 = Pair.create(view.findViewById(R.id.cast_name), "name");

            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(this, p1, p2);
            startActivity(intent, options.toBundle());

        } else {
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean nightModeNew = sp.getBoolean("dark", false);
        if (nightMode!=nightModeNew)
            recreate();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item.getItemId() == android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }
}