package tech.salroid.filmy.activities;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.salroid.filmy.R;

/*
 * Filmy Application for Android
 * Copyright (c) 2016 Ramankit Singh (http://github.com/webianks).
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


public class License extends AppCompatActivity {

    @BindView(R.id.glide)
    TextView glide;
    @BindView(R.id.materialsearcview)
    TextView material;
    @BindView(R.id.circularimageview)
    TextView circularimageview;
    @BindView(R.id.tatarka)
    TextView tatarka;
    @BindView(R.id.error)
    TextView error;
    @BindView(R.id.appintro)
    TextView appintro;
    @BindView(R.id.butterknife)
    TextView butterknife;
    @BindView(R.id.crashlytics)
    TextView crashlytics;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.logo)
    TextView logo;


    private boolean nightMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        nightMode = sp.getBoolean("dark", false);
        if (nightMode)
            setTheme(R.style.AppTheme_Base_Dark);
        else
            setTheme(R.style.AppTheme_Base);

        setContentView(R.layout.activity_license);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        Typeface typeface =  ResourcesCompat.getFont(this,R.font.rubik);
        logo.setTypeface(typeface);

        if (nightMode)
            allThemeLogic();

        if (Build.VERSION.SDK_INT >= 24)
          v24Setup();
        else
            normalSetup();
    }

    private void normalSetup() {

        glide.setText(Html.fromHtml(getString(R.string.glide)));
        material.setText(Html.fromHtml(getString(R.string.materialsearch)));
        circularimageview.setText(Html.fromHtml(getString(R.string.circularimageview)));
        tatarka.setText(Html.fromHtml(getString(R.string.tatarka)));
        error.setText(Html.fromHtml(getString(R.string.errorview)));
        appintro.setText(Html.fromHtml(getString(R.string.appintro)));
        butterknife.setText(Html.fromHtml(getString(R.string.butterknife)));
        crashlytics.setText(Html.fromHtml(getString(R.string.crashlytics)));

    }

    @TargetApi(Build.VERSION_CODES.N)
    private void v24Setup() {

        glide.setText(Html.fromHtml(getString(R.string.glide), Html.FROM_HTML_MODE_LEGACY));
        material.setText(Html.fromHtml(getString(R.string.materialsearch), Html.FROM_HTML_MODE_LEGACY));
        circularimageview.setText(Html.fromHtml(getString(R.string.circularimageview), Html.FROM_HTML_MODE_LEGACY));
        tatarka.setText(Html.fromHtml(getString(R.string.tatarka), Html.FROM_HTML_MODE_LEGACY));
        error.setText(Html.fromHtml(getString(R.string.errorview), Html.FROM_HTML_MODE_LEGACY));
        appintro.setText(Html.fromHtml(getString(R.string.appintro), Html.FROM_HTML_MODE_LEGACY));
        butterknife.setText(Html.fromHtml(getString(R.string.butterknife), Html.FROM_HTML_MODE_LEGACY));
        crashlytics.setText(Html.fromHtml(getString(R.string.crashlytics), Html.FROM_HTML_MODE_LEGACY));
    }

    private void allThemeLogic() {
        logo.setTextColor(Color.parseColor("#bdbdbd"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean nightModeNew = sp.getBoolean("dark", false);
        if (nightMode!=nightModeNew)
            recreate();
    }
}
