package tech.salroid.filmy.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
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

public class SettingsActivity extends AppCompatActivity {

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

        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }


        Typeface typeface =  ResourcesCompat.getFont(this,R.font.rubik);
        logo.setTypeface(typeface);


        if (nightMode)
            allThemeLogic();

        getFragmentManager().beginTransaction().
                replace(R.id.container,
                        new MyPreferenceFragment()).commit();


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

    public static class MyPreferenceFragment extends PreferenceFragment {

        private SwitchPreference imagePref;
        private CheckBoxPreference darkPref;

        @Override
        public void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference);


            final SharedPreferences.Editor my_prefrence = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();

            imagePref = (SwitchPreference) findPreference("imagequality");
            imagePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    String quality;

                    SwitchPreference switchPreference = (SwitchPreference) preference;

                    if (!switchPreference.isChecked()) {
                        quality = "original";
                    } else {
                        quality = "w1280";
                    }


                    my_prefrence.putString("image_quality", quality);
                    my_prefrence.apply();

                    return true;
                }
            });


            darkPref = (CheckBoxPreference) findPreference("dark");
            darkPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {

                    recreateActivity();

                    return true;
                }
            });

            Preference license = findPreference("license");
            license.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(getActivity(), License.class));
                    return true;
                }
            });


            Preference share = findPreference("Share");
            share.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    String app_share_details = getResources().getString(R.string.app_share_link);
                    if (app_share_details != null) {
                        Intent myIntent = new Intent(Intent.ACTION_SEND);
                        myIntent.setType("text/plain");
                        myIntent.putExtra(Intent.EXTRA_TEXT, "Check out this awesome movie app.\n" + "*filmy*\n" + app_share_details);
                        startActivity(Intent.createChooser(myIntent, "Share with"));
                    }
                    return true;
                }
            });


            Preference about = findPreference("About");
            about.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(getActivity(), AboutActivity.class));
                    return true;
                }
            });

        }

        private void recreateActivity() {
           getActivity().recreate();
        }

    }

}