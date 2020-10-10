package tech.salroid.filmy.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.salroid.filmy.FilmyIntro;
import tech.salroid.filmy.R;
import tech.salroid.filmy.custom_adapter.MyPagerAdapter;
import tech.salroid.filmy.customs.CustomToast;
import tech.salroid.filmy.fragment.InTheaters;
import tech.salroid.filmy.fragment.SearchFragment;
import tech.salroid.filmy.fragment.Trending;
import tech.salroid.filmy.fragment.UpComing;
import tech.salroid.filmy.network_stuff.FirstFetch;
import tech.salroid.filmy.utility.Network;
import tr.xip.errorview.ErrorView;

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

public class MainActivity extends AppCompatActivity {

    public boolean fetchingFromNetwork;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.search_view)
    MaterialSearchView materialSearchView;
    @BindView(R.id.logo)
    TextView logo;
    @BindView(R.id.toolbarScroller)
    FrameLayout toolbarScroller;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.main_error_view)
    ErrorView mErrorView;

    private Trending trendingFragment;
    private SearchFragment searchFragment;
    private boolean cantProceed;
    private boolean nightMode;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            int statusCode = intent.getIntExtra("message", 0);
            CustomToast.show(context, "Failed to get latest movies.", true);
            cantProceed(statusCode);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        nightMode = sp.getBoolean("dark", false);
        if (nightMode)
            setTheme(R.style.AppTheme_Base_Dark);
        else
            setTheme(R.style.AppTheme_Base);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(" ");

        introLogic();

        if (nightMode)
            allThemeLogic();

        mErrorView
                .setTitle(getString(R.string.error_title_damn))
                .setTitleColor(ContextCompat.getColor(this, R.color.dark))
                .setSubtitle(getString(R.string.error_details))
                .setRetryText(getString(R.string.error_retry));


        mErrorView.setRetryListener(() -> {
            if (Network.isNetworkConnected(MainActivity.this)) {
                fetchingFromNetwork = true;
                setScheduler();
            }
            canProceed();
        });


        mErrorView.setVisibility(View.GONE);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        materialSearchView.setVoiceSearch(true);
        materialSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Do some magic

                getSearchedResult(query);
                searchFragment.showProgress();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                //getSearchedResult(newText);
                return true;

            }
        });

        materialSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic

                tabLayout.setVisibility(View.GONE);

                disableToolbarScrolling();

                searchFragment = new SearchFragment();
                getSupportFragmentManager().
                        beginTransaction().
                        replace(R.id.search_container, searchFragment)
                        .commit();

            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic


                getSupportFragmentManager()
                        .beginTransaction()
                        .remove(searchFragment)
                        .commit();

                if (!cantProceed)
                    tabLayout.setVisibility(View.VISIBLE);

                enableToolbarScrolling();

            }
        });


        if (Network.isNetworkConnected(this)) {
            fetchingFromNetwork = true;
            setScheduler();
        }


    }


    private void allThemeLogic() {

        tabLayout.setTabTextColors(Color.parseColor("#bdbdbd"), Color.parseColor("#e0e0e0"));
        tabLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.colorDarkThemePrimary));
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#bdbdbd"));
        logo.setTextColor(Color.parseColor("#E0E0E0"));
        materialSearchView.setBackgroundColor(getResources().getColor(R.color.colorDarkThemePrimary));
        materialSearchView.setBackIcon(ContextCompat.getDrawable(this,R.drawable.ic_action_navigation_arrow_back_inverted));
        materialSearchView.setCloseIcon(ContextCompat.getDrawable(this,R.drawable.ic_action_navigation_close_inverted));
        materialSearchView.setTextColor(Color.parseColor("#ffffff"));

    }


    private void introLogic() {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

                if (isFirstStart) {

                    Intent i = new Intent(MainActivity.this, FilmyIntro.class);
                    startActivity(i);
                    SharedPreferences.Editor e = getPrefs.edit();
                    e.putBoolean("firstStart", false);
                    e.apply();
                }
            }
        });
        t.start();

    }

    private void setScheduler() {

        FirstFetch firstFetch = new FirstFetch(this);
        firstFetch.start();

    }

    public void cantProceed(final int status) {

        new Handler().postDelayed(() -> {

            if (trendingFragment != null && !trendingFragment.isShowingFromDatabase) {
                cantProceed = true;
                tabLayout.setVisibility(View.GONE);
                viewPager.setVisibility(View.GONE);
                //mErrorView.setError(status);
                mErrorView.setVisibility(View.VISIBLE);
                //disable toolbar scrolling
                disableToolbarScrolling();
            }
        }, 1000);

    }

    public void canProceed() {

        cantProceed = false;

        tabLayout.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.VISIBLE);
        mErrorView.setVisibility(View.GONE);

        if (trendingFragment != null) {

            trendingFragment.retryLoading();

        }

        enableToolbarScrolling();

    }

    private void disableToolbarScrolling() {
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbarScroller.getLayoutParams();
        params.setScrollFlags(0);
    }

    private void enableToolbarScrolling() {

        AppBarLayout.LayoutParams params =
                (AppBarLayout.LayoutParams) toolbarScroller.getLayoutParams();
        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);

    }

    private void setupViewPager(ViewPager viewPager) {


        trendingFragment = new Trending();
        InTheaters inTheatersFragment = new InTheaters();
        UpComing upComingFragment = new UpComing();

        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(trendingFragment, getString(R.string.trending));
        adapter.addFragment(inTheatersFragment, getString(R.string.theatres));
        adapter.addFragment(upComingFragment, getString(R.string.upcoming));
        viewPager.setAdapter(adapter);

    }

    private void getSearchedResult(String query) {

        if (searchFragment != null) {
            searchFragment.getSearchedResult(query);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem itemSearch = menu.findItem(R.id.action_search);
        MenuItem itemAccount = menu.findItem(R.id.ic_collections);

        if (nightMode) {
            itemSearch.setIcon(R.drawable.ic_action_action_search);
            itemAccount.setIcon(R.drawable.ic_action_collections_bookmark2);
        }

        materialSearchView.setMenuItem(itemSearch);
        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {

            case R.id.action_search:
                startActivity(new Intent(this, SearchFragment.class));
                break;
            case R.id.ic_setting:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.ic_collections:
                startActivity(new Intent(this, CollectionsActivity.class));
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    materialSearchView.setQuery(searchWrd, false);
                }
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (materialSearchView.isSearchOpen()) {
            materialSearchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean nightModeNew = sp.getBoolean("dark", false);

        if (nightMode != nightModeNew)
            recreate();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("fetch-failed"));

    }

    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }


}