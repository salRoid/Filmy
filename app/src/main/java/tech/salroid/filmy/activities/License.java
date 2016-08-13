package tech.salroid.filmy.activities;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.salroid.filmy.R;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if (Build.VERSION.SDK_INT >= 24) {
            glide.setText(Html.fromHtml(getString(R.string.glide), Html.FROM_HTML_MODE_LEGACY));
            material.setText(Html.fromHtml(getString(R.string.materialsearch), Html.FROM_HTML_MODE_LEGACY));
            circularimageview.setText(Html.fromHtml(getString(R.string.circularimageview), Html.FROM_HTML_MODE_LEGACY));
            tatarka.setText(Html.fromHtml(getString(R.string.tatarka), Html.FROM_HTML_MODE_LEGACY));
            error.setText(Html.fromHtml(getString(R.string.errorview), Html.FROM_HTML_MODE_LEGACY));
            appintro.setText(Html.fromHtml(getString(R.string.appintro), Html.FROM_HTML_MODE_LEGACY));
            butterknife.setText(Html.fromHtml(getString(R.string.butterknife), Html.FROM_HTML_MODE_LEGACY));
            crashlytics.setText(Html.fromHtml(getString(R.string.crashlytics), Html.FROM_HTML_MODE_LEGACY));
        } else {
            glide.setText(Html.fromHtml(getString(R.string.glide)));
            material.setText(Html.fromHtml(getString(R.string.materialsearch)));
            circularimageview.setText(Html.fromHtml(getString(R.string.circularimageview)));
            tatarka.setText(Html.fromHtml(getString(R.string.tatarka)));
            error.setText(Html.fromHtml(getString(R.string.errorview)));
            appintro.setText(Html.fromHtml(getString(R.string.appintro)));
            butterknife.setText(Html.fromHtml(getString(R.string.butterknife)));
            crashlytics.setText(Html.fromHtml(getString(R.string.crashlytics)));
        }



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
