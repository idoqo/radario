package io.github.idoqo.radario;

import android.app.FragmentManager;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.github.idoqo.radario.fragment.UserCommentsFragment;
import io.github.idoqo.radario.fragment.UserLikesFragment;
import io.github.idoqo.radario.fragment.UserTopicsFragment;
import io.github.idoqo.radario.url.RadarUrlParser;

public class UserProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public static final String EXTRA_USERNAME = "username";
    public static final String EXTRA_FULLNAME = "fullname";
    public static final String EXTRA_AVATAR_URL = "avatar_url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prepareHeaderView();
    }

    private void prepareHeaderView(){
        TextView fullNameTextView = (TextView) findViewById(R.id.profile_full_name);
        TextView usernameTextView = (TextView) findViewById(R.id.profile_username);
        CircleImageView imageView = (CircleImageView) findViewById(R.id.user_avatar);

        //if the activity was launched from a link embedded in a text, it will be
        //launched with a uri, else it is launched with extras.
        Uri callerUri = getIntent().getData();
        Bundle extras = getIntent().getExtras();

        String username = "";
        String fullName = "Okoko Michaels";
        //todo load the user's avatar in a background task

        if (callerUri != null) {
            username = callerUri.getQueryParameter(RadarUrlParser.KEY_USERNAME_QUERY);
        } else if (extras != null) {
            username = extras.getString(EXTRA_USERNAME);
            fullName = extras.getString(EXTRA_FULLNAME, "Okoko Michaels");
        } else {
            //if there is no way to get the username, cancel the activity
            //and go back to the parent activity since we can't get any other user info
            finish();
        }

        fullNameTextView.setText(fullName);
        usernameTextView.setText(username);
    }

    private void setupViewPager(ViewPager vp) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new UserTopicsFragment(), "Topics");
        adapter.addFragment(new UserCommentsFragment(), "Comments");
        adapter.addFragment(new UserLikesFragment(), "Likes");
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
