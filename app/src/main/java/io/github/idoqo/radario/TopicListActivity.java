package io.github.idoqo.radario;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

public class TopicListActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_list);
        toolbar = (Toolbar) findViewById(R.id.topic_list_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

        navigationView = (NavigationView) findViewById(R.id.topic_list_nav_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        TopicListFragment fragment = new TopicListFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.topic_list_main_content, fragment)
                .commit();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                drawerLayout.closeDrawers();
                return true;
            }
        });

        View navHeaderView = navigationView.getHeaderView(0);
        final TextView usernameTV = (TextView) navHeaderView.findViewById(R.id.logged_username);
        usernameTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameTV.getText().toString();
                Intent profileIntent = new Intent(TopicListActivity.this, UserProfileActivity.class);
                profileIntent.putExtra(UserProfileActivity.EXTRA_USERNAME, username);
                startActivity(profileIntent);
                drawerLayout.closeDrawers();
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(Gravity.LEFT);
                break;
        }
        return super.onOptionsItemSelected(item);
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
}
