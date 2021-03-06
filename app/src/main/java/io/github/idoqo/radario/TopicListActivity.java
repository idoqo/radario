package io.github.idoqo.radario;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import io.github.idoqo.radario.helpers.CurrentUserHelper;
import io.github.idoqo.radario.model.CurrentUser;
import io.github.idoqo.radario.service.PullNotificationService;
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
    private CircleImageView avatarView;
    private TextView usernameTV;
    private TextView notificationCountView;
    private RelativeLayout notificationCountWrap;
    private SharedPreferences loginData;
    private CurrentUserHelper userHelper;

    public static final int LOGIN_REQUEST_CODE = 1;

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

        userHelper = new CurrentUserHelper(okHttpClient, this);

        initNotificationService();

        TopicListFragment fragment = new TopicListFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.topic_list_main_content, fragment)
                .commit();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                drawerLayout.closeDrawers();
                triggerTopicsReload(item);
                return true;
            }
        });

        View navHeaderView = navigationView.getHeaderView(0);
        usernameTV = (TextView) navHeaderView.findViewById(R.id.logged_username);
        avatarView = (CircleImageView) navHeaderView.findViewById(R.id.profile_image);
        notificationCountWrap = (RelativeLayout) navHeaderView.findViewById(R.id.count_wrapper);
        notificationCountView = (TextView) navHeaderView.findViewById(R.id.pending_notification_count);

        prepareNavHeader();
        usernameTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (savedCookies != null && savedCookies.contains("_t=")) {
                    String username = usernameTV.getText().toString();
                    Intent profileIntent = new Intent(TopicListActivity.this, UserProfileActivity.class);
                    profileIntent.putExtra(UserProfileActivity.EXTRA_USERNAME, username);
                    startActivity(profileIntent);
                } else {
                    promptForLogin();
                }
                drawerLayout.closeDrawers();
            }
        });
    }

    private void prepareNavHeader(){
        loggedUser = userHelper.lazyLoadUser();
        String textToShow;
        int notificationCount;
        if (loggedUser.getUsername() != null) {
            textToShow = loggedUser.getUsername();
            notificationCount = loggedUser.getTotalUnreadNotifications();
            Picasso.with(this)
                    .load(loggedUser.getAvatarUrl(150))
                    .placeholder(R.drawable.default_user)
                    .into(avatarView);

        } else {
            textToShow = getResources().getString(R.string.no_logged_user);
            notificationCount = 0;
        }
        usernameTV.setText(textToShow);
        if (notificationCount < 1) {
            //hide the bubble
            notificationCountWrap.setVisibility(View.GONE);
        } else if (notificationCount > 99) {
            if (notificationCountWrap.getVisibility() == View.GONE) {
                notificationCountWrap.setVisibility(View.VISIBLE);
            }
            notificationCountView.setText("99+");
        } else {
            if (notificationCountWrap.getVisibility() == View.GONE) {
                notificationCountWrap.setVisibility(View.VISIBLE);
            }
            notificationCountView.setText(String.valueOf(notificationCount));
        }
        avatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent notificationIntent = new Intent(TopicListActivity.this, NotificationActivity.class);
                startActivity(notificationIntent);
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(Gravity.LEFT);
                break;
            case R.id.action_update:
                launchUpdateActivity();
                break;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;
            case R.id.action_sort_posts:
                showPostSortDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showPostSortDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sort Topics");
    }

    private void launchUpdateActivity(){
        Intent intent = new Intent(this, UpdateActivity.class);
        startActivity(intent);
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

    private void triggerTopicsReload(MenuItem item){
        TopicListFragment topicListFragment = new TopicListFragment();
        Bundle extras = new Bundle();
        String itemString;
        switch (item.getItemId()) {
            case R.id.menu_cat_ama:
                itemString = "ama";
                break;
            case R.id.menu_cat_design:
                itemString = "design";
                break;
            case R.id.menu_cat_events:
                itemString = "events";
                break;
            case R.id.menu_cat_everything:
                itemString = "everything";
                break;
            case R.id.menu_cat_jobs:
                itemString = "jobs";
                break;
            case R.id.menu_cat_meta:
                itemString = "meta";
                break;
            case R.id.menu_cat_products:
                default:
                itemString = "products";
        }
        Log.e(TAG, "triggerTopicReload: "+itemString);
        extras.putString(TopicListFragment.TOPICS_CATEGORY_TO_LOAD, itemString);
        topicListFragment.setArguments(extras);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.topic_list_main_content, topicListFragment)
                .commit();
    }

    private void promptForLogin(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(getResources().getString(R.string.login));
        dialogBuilder.setMessage(getResources().getString(R.string.login_advantage));
        dialogBuilder.setIcon(getResources().getDrawable(R.drawable.ic_unlock_black));
        dialogBuilder.setCancelable(true);

        dialogBuilder.setPositiveButton(getResources().getString(R.string.login),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent loginIntent = new Intent(TopicListActivity.this, LoginActivity.class);
                        startActivityForResult(loginIntent, LOGIN_REQUEST_CODE);
                    }
                });
        dialogBuilder.setNegativeButton(getResources().getString(R.string.dismiss),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_REQUEST_CODE) {
            if (resultCode == RESULT_OK){
                finish();
                startActivity(getIntent());
            }
        }
    }

    private void initNotificationService(){
        Log.i("TopicListActivity", "starting pull notification service");
        Intent service = new Intent(this, PullNotificationService.class);
        startService(service);
    }
}
