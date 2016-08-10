package tech.salroid.filmy.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.salroid.filmy.FilmyIntro;
import tech.salroid.filmy.R;
import tech.salroid.filmy.custom_adapter.MyPagerAdapter;
import tech.salroid.filmy.fragment.InTheaters;
import tech.salroid.filmy.fragment.SearchFragment;
import tech.salroid.filmy.fragment.Trending;
import tech.salroid.filmy.fragment.UpComing;
import tech.salroid.filmy.services.FilmyJobScheduler;
import tech.salroid.filmy.utility.Network;
import tr.xip.errorview.ErrorView;


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
    @BindView(R.id.error_view)
    ErrorView mErrorView;


    private Trending trendingFragment;
    private SearchFragment searchFragment;
    private boolean cantProceed;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            int statusCode = intent.getIntExtra("message", 00);

            Toast.makeText(context, "Failed to get latest movies.", Toast.LENGTH_SHORT).show();

            cantProceed(statusCode);

        }
    };

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(" ");

        introLogic();

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/canaro_extra_bold.otf");
        logo.setTypeface(typeface);

        mErrorView.setConfig(ErrorView.Config.create()
                .title(getString(R.string.error_title_damn))
                .titleColor(ContextCompat.getColor(this, R.color.dark))
                .subtitle("Unable to fetch movies.\nCheck internet connection then try again.")
                .retryText(getString(R.string.error_view_retry))
                .build());


        mErrorView.setOnRetryListener(new ErrorView.RetryListener() {
            @Override
            public void onRetry() {

                if (Network.isNetworkConnected(MainActivity.this)) {

                    fetchingFromNetwork = true;
                    setScheduler();

                }

                canProceed();

            }
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

        FilmyJobScheduler filmyJobScheduler = new FilmyJobScheduler(this);
        filmyJobScheduler.createJob();

    }

    public void cantProceed(final int status) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                cantProceed = true;

                tabLayout.setVisibility(View.GONE);
                viewPager.setVisibility(View.GONE);
                mErrorView.setError(status);
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
        adapter.addFragment(trendingFragment, "TRENDING");
        adapter.addFragment(inTheatersFragment, "IN THEATERS");
        adapter.addFragment(upComingFragment, "UPCOMING");
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
        MenuItem item = menu.findItem(R.id.action_search);
        materialSearchView.setMenuItem(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {

            startActivity(new Intent(this, SearchFragment.class));
        }

        if (id == R.id.ic_setting) {
            startActivity(new Intent(this, SettingsActivity.class));

        }

        if (id == R.id.ic_collection) {
            startActivity(new Intent(this, SavedMovies.class));
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