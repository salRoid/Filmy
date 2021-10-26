package tech.salroid.filmy.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;

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

public class AboutActivity extends AppCompatActivity {

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

        setContentView(R.layout.activity_developers);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        Typeface typeface = ResourcesCompat.getFont(this,R.font.rubik);
        logo.setTypeface(typeface);

        if (nightMode)
            allThemeLogic();

        Glide.with(this).load(getString(R.string.profile_webianks)).into((ImageView) findViewById(R.id.profile_webianks));
        Glide.with(this).load(getString(R.string.profile_salroid)).into((ImageView) findViewById(R.id.profile_salroid));
        Glide.with(this).load(getString(R.string.banner_webianks)).into((ImageView) findViewById(R.id.banner_webianks));
        Glide.with(this).load(getString(R.string.banner_salroid)).into((ImageView) findViewById(R.id.banner_salroid));

    }

    private void allThemeLogic() {
        logo.setTextColor(Color.parseColor("#bdbdbd"));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void sendEmail(View view) {
      switch (view.getId()){
          case R.id.email_webianks:
              Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
              emailIntent.setData(Uri.parse("mailto: webianks@gmail.com"));
              startActivity(Intent.createChooser(emailIntent, "Send feedback"));
          break;

          case R.id.email_salroid:
              Intent emailIntent2 = new Intent(Intent.ACTION_SENDTO);
              emailIntent2.setData(Uri.parse("mailto: gupta.sajal631@gmail.com"));
              startActivity(Intent.createChooser(emailIntent2, "Send feedback"));
          break;
      }
    }

    public void redirectGithub(View view) {
        switch (view.getId()){
            case R.id.github_webianks:
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                builder.setToolbarColor(ContextCompat.getColor(this, R.color.black));
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(this, Uri.parse(getString(R.string.git_webianks)));
            break;
            case R.id.github_salroid:
                CustomTabsIntent.Builder builder1 = new CustomTabsIntent.Builder();
                builder1.setToolbarColor(ContextCompat.getColor(this, R.color.black));
                CustomTabsIntent customTabsIntent1 = builder1.build();
                customTabsIntent1.launchUrl(this, Uri.parse(getString(R.string.git_salroid)));
            break;
        }
    }

    private void viewIntent(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    public void redirectWebsite(View view) {
        switch (view.getId()){
            case R.id.website_webianks:
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorAccent));
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(this, Uri.parse(getString(R.string.website_webianks)));
            break;
            case R.id.website_salroid:
                CustomTabsIntent.Builder builder1 = new CustomTabsIntent.Builder();
                builder1.setToolbarColor(ContextCompat.getColor(this, R.color.black));
                CustomTabsIntent customTabsIntent1 = builder1.build();
                customTabsIntent1.launchUrl(this, Uri.parse(getString(R.string.website_salroid)));
            break;
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
}