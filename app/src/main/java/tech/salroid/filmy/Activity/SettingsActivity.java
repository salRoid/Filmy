package tech.salroid.filmy.Activity;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import tech.salroid.filmy.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction().
                replace(R.id.container,
                        new MyPreferenceFragment()).commit();



    }

    public static class MyPreferenceFragment extends PreferenceFragment {

        private SwitchPreference imagePref;
        private CheckBoxPreference cache_pref;


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
                        quality = "medium";
                    } else {
                        quality = "thumb";
                    }


                    my_prefrence.putString("image_quality", quality);
                    my_prefrence.commit();

                    return true;
                }
            });

            cache_pref = (CheckBoxPreference) findPreference("pref_sync");
            cache_pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {

                    CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preference;

                    if (!checkBoxPreference.isChecked()) {

                        my_prefrence.putBoolean("cache",true);
                        my_prefrence.commit();
                    }

                    return true;
                }
            });

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
