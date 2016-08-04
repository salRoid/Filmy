package tech.salroid.filmy.Activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.speech.RecognizerIntent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import java.util.ArrayList;

import tech.salroid.filmy.CustomAdapter.MyPagerAdapter;
import tech.salroid.filmy.Fragments.Popular;
import tech.salroid.filmy.R;
import tech.salroid.filmy.SearchFragment;


public class MainActivity extends AppCompatActivity{

    private Toolbar toolbar;
    private MaterialSearchView materialSearchView;
    private SearchFragment searchFragment;
    TextView logo;



    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(" ");



        logo = (TextView) findViewById(R.id.logo);

        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/canaro_extra_bold.otf");
        logo.setTypeface(typeface);


        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        materialSearchView = (MaterialSearchView) findViewById(R.id.search_view);
        materialSearchView.setVoiceSearch(true);
        materialSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Do some magic

                getSearchedResult(query);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                 getSearchedResult(newText);

                return true;
            }
        });

        materialSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic

                searchFragment = new SearchFragment();
                getSupportFragmentManager().
                        beginTransaction().
                        replace(R.id.fragment_container, searchFragment)
                        .commit();

            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic

                getSupportFragmentManager()
                        .beginTransaction()
                        .remove(searchFragment)
                        .commit();

            }
        });



    }
    private void setupViewPager(ViewPager viewPager) {
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Popular(), "POPULAR");
        adapter.addFragment(new Popular(), "IN THEATER");
        adapter.addFragment(new Popular(), "UPCOMING");
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

        if(id==R.id.ic_setting){
            startActivity(new Intent(this, SettingsActivity.class));

        }

        if(id==R.id.ic_collection){
            startActivity(new Intent(this,SavedMovies.class));
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

}
