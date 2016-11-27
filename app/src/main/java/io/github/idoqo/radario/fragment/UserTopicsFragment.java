package io.github.idoqo.radario.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wang.avi.AVLoadingIndicatorView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.github.idoqo.radario.R;
import io.github.idoqo.radario.TopicListFragment;
import io.github.idoqo.radario.UserProfileActivity;
import io.github.idoqo.radario.Utils;
import io.github.idoqo.radario.adapter.UserTopicAdapter;
import io.github.idoqo.radario.helpers.ApiHelper;
import io.github.idoqo.radario.helpers.HttpRequestBuilderHelper;
import io.github.idoqo.radario.model.Topic;
import io.github.idoqo.radario.model.User;
import io.github.idoqo.radario.model.UserAction;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;


public class UserTopicsFragment extends Fragment {
    private TopicsFetcherTask fetcherTask;
    private UserTopicAdapter topicAdapter;
    private RecyclerView topicsList;
    private ArrayList<UserAction> userTopics = new ArrayList<>();

    private User user;
    private HttpUrl userTopicsUrl;
    private OkHttpClient okHttpClient;

    private AVLoadingIndicatorView loadingIndicator;
    private TextView emptyTopicsView;

    private static final String LOG_TAG = "UserTopicsFragment";

    public UserTopicsFragment(){

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            String username = arguments.getString(UserProfileActivity.EXTRA_USERNAME);
            String fullName = arguments.getString(UserProfileActivity.EXTRA_FULLNAME);
            String avatarUrl = arguments.getString(UserProfileActivity.EXTRA_AVATAR_URL);

            user = new User();
            user.setUsername(username);
            user.setAvatarUrl(avatarUrl);
            user.setFullName(fullName);

            okHttpClient = ((UserProfileActivity) getActivity()).getOkHttpClient();

            userTopicsUrl = HttpRequestBuilderHelper.buildUserUrl(username,
                    HttpRequestBuilderHelper.USER_FILTER_TOPICS);
            fetcherTask = new TopicsFetcherTask(userTopicsUrl);
            fetcherTask.execute(0, 1);
        } else {
            //what the actual fuck?
            Snackbar.make(topicsList, "Where is your god now? ¯\\_(ツ)_/¯", Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View content = inflater.inflate(R.layout.fragment_user_topics, container, false);
        topicsList = (RecyclerView) content.findViewById(R.id.user_topics_list);
        emptyTopicsView = (TextView) content.findViewById(R.id.empty_user_topics);

        loadingIndicator = (AVLoadingIndicatorView) content.findViewById(R.id.topics_loading_indicator);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        topicsList.setHasFixedSize(true);
        topicsList.setLayoutManager(layoutManager);
        topicAdapter = new UserTopicAdapter(getActivity(), userTopics);
        topicsList.setAdapter(topicAdapter);

        return content;
    }

    private class TopicsFetcherTask extends AsyncTask<Integer, Void, ArrayList<UserAction>>
    {
        private HttpUrl topicsUrl;

        TopicsFetcherTask(HttpUrl url){
            topicsUrl = url;
        }

        protected ArrayList<UserAction> doInBackground(Integer... params) {
            //todo show loading animation
            ArrayList<UserAction> loadedTopics = new ArrayList<>();
            String jsonString;

            try {
                jsonString = ApiHelper.GET(okHttpClient, topicsUrl);
                Log.e(LOG_TAG, "doInBackground: "+topicsUrl);
            } catch (IOException ioe) {
                jsonString = null;
                Snackbar.make(topicsList, "Failed to retrieve data", Snackbar.LENGTH_LONG).show();
            }
            if (jsonString != null) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    JsonNode response = mapper.readTree(jsonString);
                    JsonNode repliesPath = response.path("user_actions");
                    Iterator<JsonNode> nodeIterator = repliesPath.elements();

                    while (nodeIterator.hasNext()) {
                        UserAction topic = mapper.readValue(nodeIterator.next().traverse(), UserAction.class);
                        loadedTopics.add(topic);
                    }
                } catch (Exception e) {
                    Log.i(LOG_TAG, e.getMessage());
                }
            }
            return loadedTopics;
        }

        protected void onPostExecute(ArrayList<UserAction> result) {
            super.onPostExecute(result);
            if (result.size() > 0) {
                emptyTopicsView.setVisibility(View.GONE);
                loadingIndicator.setVisibility(View.INVISIBLE);
                userTopics = result;
                if (topicAdapter != null) {
                    topicAdapter.setData(result);
                    topicAdapter.notifyDataSetChanged();
                } else {
                    topicAdapter = new UserTopicAdapter(getActivity(), result);
                }
            } else {
                Toast.makeText(getContext(), "No more items to load", Toast.LENGTH_SHORT).show();
                userTopics = result;
                emptyTopicsView.setVisibility(View.VISIBLE);
            }
        }
    }
}
