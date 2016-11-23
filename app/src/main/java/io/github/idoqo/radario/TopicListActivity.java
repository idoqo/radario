package io.github.idoqo.radario;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.w3c.dom.Text;

import java.io.IOException;

import io.github.idoqo.radario.helpers.ApiHelper;
import io.github.idoqo.radario.helpers.CurrentUserHelper;
import io.github.idoqo.radario.helpers.HttpRequestBuilderHelper;
import io.github.idoqo.radario.model.CurrentUser;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class TopicListActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private CurrentUser loggedUser = null;
    private OkHttpClient okHttpClient;
    private TextView usernameTV;
    private SharedPreferences loginData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_list);
        toolbar = (Toolbar) findViewById(R.id.topic_list_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

        loginData = getSharedPreferences(LoginActivity.PREFERENCE_LOGIN_DATA, MODE_PRIVATE);

        navigationView = (NavigationView) findViewById(R.id.topic_list_nav_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView.setItemIconTintList(null);

        final String savedCookies = loginData.getString(LoginActivity.COOKIE_FULL_STRING, null);

        okHttpClient = new OkHttpClient().newBuilder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        final Request original = chain.request();
                        String cookie = (savedCookies != null) ? savedCookies : "ddd";
                        final Request authorized = original.newBuilder()
                                .addHeader("Cookie", cookie)
                                .build();
                        return chain.proceed(authorized);
                    }
                }).build();

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
        usernameTV = (TextView) navHeaderView.findViewById(R.id.logged_username);
        prepareNavHeader(usernameTV);
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

    private void prepareNavHeader(TextView textView){
        CurrentUserFetcherTask fetcherTask = new CurrentUserFetcherTask();
        fetcherTask.execute();
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

    public class CurrentUserFetcherTask extends AsyncTask<Void, Void, CurrentUser> {

        @Override
        protected CurrentUser doInBackground(Void... voids) {
            String userJsonString;
            CurrentUser currentUser = new CurrentUser();
            /*String filename = "user.json";
            Log.i("TopicListActivity", "Loading file "+filename+" from assets");
            userJsonString = Utils.loadJsonFromAsset(TopicListActivity.this, filename);*/
            try {
                HttpUrl url = HttpRequestBuilderHelper.buildCurrentUserUrl();
                userJsonString = ApiHelper.GET(okHttpClient, url);
                Log.e(TAG, "doInBackground "+userJsonString);
            } catch (IOException ioe) {
                userJsonString = null;
                Log.i(TAG, "doInBackground: " + ioe.getLocalizedMessage());
            }
            if (userJsonString != null) {
                if (userJsonString.isEmpty()) {
                    Log.e("Terrible", "return string is empty");
                    //an empty response from the server means no authenticated user is present.
                    currentUser = null;
                } else {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        JsonNode retval = mapper.readTree(userJsonString);
                        JsonNode currentUserPath = retval.path("current_user");
                        currentUser = mapper.readValue(currentUserPath.traverse(), CurrentUser.class);
                    } catch (IOException ioe) {
                        Log.e("Terrible", ioe.getMessage());
                        //something bad happened
                    }
                }
            }
            return currentUser;
        }

        @Override
        protected void onPostExecute(CurrentUser data) {
            super.onPostExecute(data);
            loggedUser = data;
            if (loggedUser != null) {
                usernameTV.setText(loggedUser.getUsername());
            } else {
                usernameTV.setText("Hello_asshole");
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }
}
