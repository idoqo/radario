package io.github.idoqo.radario;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.github.idoqo.radario.fragment.UserCommentsFragment;
import io.github.idoqo.radario.fragment.UserLikesFragment;
import io.github.idoqo.radario.fragment.UserTopicsFragment;
import io.github.idoqo.radario.model.User;
import io.github.idoqo.radario.model.UserLike;
import io.github.idoqo.radario.url.RadarUrlParser;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UserProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public static final String EXTRA_USERNAME = "username";
    public static final String EXTRA_FULLNAME = "fullname";
    public static final String EXTRA_AVATAR_URL = "avatar_url";

    private String username = null;
    private int userID;
    private String fullName;
    private String avatarUrl;

    private OkHttpClient okHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        initHttpClient();
        initUserDetails();
        prepareHeaderView();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initHttpClient(){
        SharedPreferences loginData = getApplicationContext()
                .getSharedPreferences(LoginActivity.PREFERENCE_LOGIN_DATA,
                        Context.MODE_PRIVATE);
        final String savedCookies = loginData.getString(LoginActivity.COOKIE_FULL_STRING, null);

        okHttpClient = new OkHttpClient().newBuilder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        final Request original = chain.request();
                        //savedCookies being null causes the client to crash so set a non-null
                        //string to use in-case.
                        String cookie = (savedCookies != null) ? savedCookies : "ddd";
                        final Request authorized = original.newBuilder()
                                .addHeader("Cookie", cookie)
                                .build();
                        return chain.proceed(authorized);
                    }
                }).build();
    }

    public OkHttpClient getOkHttpClient(){
        return okHttpClient;
    }

    private void initUserDetails(){
        //if the activity was launched from a link embedded in a text, it will be
        //launched with a uri, else it is launched with extras.
        Uri callerUri = getIntent().getData();
        Bundle extras = getIntent().getExtras();

        fullName = "Okoko Michaels";
        //todo load the user's avatar in a background task

        if (callerUri != null) {
            username = callerUri.getQueryParameter(RadarUrlParser.KEY_USERNAME_QUERY);
        } else if (extras != null) {
            username = extras.getString(EXTRA_USERNAME);
            fullName = extras.getString(EXTRA_FULLNAME, "Okoko Michaels");
        }
        if (username == null) {
            this.finish();
        }
    }

    private void prepareHeaderView(){
        TextView fullNameTextView = (TextView) findViewById(R.id.profile_full_name);
        TextView usernameTextView = (TextView) findViewById(R.id.profile_username);
        CircleImageView imageView = (CircleImageView) findViewById(R.id.user_avatar);

        fullNameTextView.setText(fullName);
        usernameTextView.setText(username);
    }

    private void setupViewPager(ViewPager vp) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        //prepare a bundle containing the user info to be shared to the fragments
        Bundle arguments = new Bundle();
        arguments.putString(EXTRA_FULLNAME, fullName);
        arguments.putString(EXTRA_USERNAME, username);
        arguments.putString(EXTRA_AVATAR_URL, avatarUrl);

        UserTopicsFragment userTopicsFragment = new UserTopicsFragment();
        userTopicsFragment.setArguments(arguments);

        UserCommentsFragment userCommentsFragment = new UserCommentsFragment();
        userCommentsFragment.setArguments(arguments);

        UserLikesFragment userLikesFragment = new UserLikesFragment();
        userLikesFragment.setArguments(arguments);

        adapter.addFragment(userTopicsFragment, "Topics");
        adapter.addFragment(userCommentsFragment, "Comments");
        adapter.addFragment(userLikesFragment, "Likes");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
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
