package io.github.idoqo.radario;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.github.idoqo.radario.fragment.SearchResponseFragment;

public class SearchResultsActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private String query;

    public static final String SEARCH_QUERY = "query";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        handleIntent(getIntent());
    }

    private void setupViewPager(ViewPager vp){
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        Bundle args = new Bundle();
        args.putString(SEARCH_QUERY, query);

        SearchResponseFragment postSearchFrag = new SearchResponseFragment();
        postSearchFrag.setArguments(args);

        SearchResponseFragment topicsSearchFrag = new SearchResponseFragment();
        topicsSearchFrag.setArguments(args);

        SearchResponseFragment userSearchFrag = new SearchResponseFragment();
        userSearchFrag.setArguments(args);

        pagerAdapter.addFragment(topicsSearchFrag, "Topics");
        pagerAdapter.addFragment(postSearchFrag, "Comments");
        pagerAdapter.addFragment(userSearchFrag, "Users");
        vp.setAdapter(pagerAdapter);
    }

    protected void onNewIntent(Intent intent){
        handleIntent(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        //associate searchable config with the search view
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            if (query != null) {
                setupViewPager(viewPager);
                tabLayout.setupWithViewPager(viewPager);
            } else {
                Toast.makeText(this, "nothing", Toast.LENGTH_LONG).show();
            }
        } else {
            //show search box or some poor joke.
            Toast.makeText(this, "nothing", Toast.LENGTH_LONG).show();
        }
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(android.support.v4.app.FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        public int getCount() {
            return fragmentList.size();
        }

        public void addFragment(Fragment fragment, String title){
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }

        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }
}
