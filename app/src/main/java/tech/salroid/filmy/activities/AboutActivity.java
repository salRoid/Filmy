package tech.salroid.filmy.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developers);


        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }


        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/canaro_extra_bold.otf");
        logo.setTypeface(typeface);
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


          case R.id.email:

              Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
              emailIntent.setData(Uri.parse("mailto: webianksc@gmailcom"));
              startActivity(Intent.createChooser(emailIntent, "Send feedback"));

              break;

          case R.id.email2:

              Intent emailIntent2 = new Intent(Intent.ACTION_SENDTO);
              emailIntent2.setData(Uri.parse("mailto: salroid@yahoo.com"));
              startActivity(Intent.createChooser(emailIntent2, "Send feedback"));

              break;

      }


    }


    public void openGithub(View view) {
        String url = "https://github.com/webianks";
        String url2 = "https://github.com/salroid";

        switch (view.getId()){

            case R.id.github:
                viewIntent(url);
                break;
            case R.id.github2:
                viewIntent(url2);
                break;
        }

    }

    private void viewIntent(String url) {

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    public void openGplus(View view) {

        String url = "https://plus.google.com/u/0/+RamankitSinghWebianks";
        String url2 = "https://plus.google.com/u/0/+sajalguptasajju";

        switch (view.getId()){

            case R.id.gplus:
                viewIntent(url);
                break;
            case R.id.gplus2:
                viewIntent(url2);
                break;
        }


    }
}
