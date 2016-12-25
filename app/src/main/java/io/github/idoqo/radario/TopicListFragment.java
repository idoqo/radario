package io.github.idoqo.radario;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.github.idoqo.radario.adapter.TopicAdapter;
import io.github.idoqo.radario.helpers.ApiHelper;
import io.github.idoqo.radario.helpers.HttpRequestBuilderHelper;
import io.github.idoqo.radario.lib.EndlessScrollListView;
import io.github.idoqo.radario.lib.EndlessScrollListener;
import io.github.idoqo.radario.lib.EndlessScrollListenerInterface;
import io.github.idoqo.radario.model.Notification;
import io.github.idoqo.radario.model.Topic;
import io.github.idoqo.radario.model.User;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static io.github.idoqo.radario.helpers.HttpRequestBuilderHelper.RADAR_URL_HOST;
import static io.github.idoqo.radario.helpers.HttpRequestBuilderHelper.RADAR_URL_PAGE_QUERY;
import static io.github.idoqo.radario.helpers.HttpRequestBuilderHelper.RADAR_URL_SCHEME;


public class TopicListFragment extends Fragment implements EndlessScrollListenerInterface {
    private static final String LOG_TAG = "TopicListFragment";

    public static final String TOPICS_CATEGORY_TO_LOAD = "topics_url";

    private OkHttpClient okHttpClient;

    private EndlessScrollListView topicsListView;
    private EndlessScrollListener scrollListener;
    private SwipeRefreshLayout refreshTopicLayout;
    private FloatingActionButton createTopicButton;

    private TopicAdapter topicAdapter;
    private TopicsFetcherTask fetcherTask;
    private boolean executing = false;
    private HttpUrl preferredUrl;

    //the current page to be loaded, starts at 0
    private int currentPage = 0;

    private SharedPreferences loginData;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_topic_list, container, false);

        topicsListView = (EndlessScrollListView) view.findViewById(R.id.topics_list);
        refreshTopicLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_topic_list);
        createTopicButton = (FloatingActionButton) view.findViewById(R.id.add_topic_fab);
        scrollListener = new EndlessScrollListener(this);
        topicAdapter = new TopicAdapter(getActivity());

        topicsListView.setListener(scrollListener);
        topicsListView.setAdapter(topicAdapter);

        refreshTopicLayout.setColorSchemeResources(R.color.blue, R.color.colorAccent,
                R.color.cyan, R.color.pink);
        refreshTopicLayout.setOnRefreshListener(refreshTopicList());
        createTopicButton.setOnClickListener(createTopicClicked());

        return view;
    }

    private View.OnClickListener createTopicClicked(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Ogbeni calm down", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private SwipeRefreshLayout.OnRefreshListener refreshTopicList(){
        return new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //reset the current page counter so as to load the latest topics
                currentPage = 0;
                fetcherTask = new TopicsFetcherTask(preferredUrl);
                //also, pass 0 as the first param(number of items in the list view)
                fetcherTask.execute(0, currentPage);
                refreshTopicLayout.setRefreshing(false);
            }
        };
    }

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        loginData = getActivity().getApplicationContext()
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
        loadTopics(0, currentPage);
    }

    private void loadTopics(int itemCount, int page){
        Bundle args = getArguments();
        if (args != null && args.containsKey(TOPICS_CATEGORY_TO_LOAD)) {
            Log.e(LOG_TAG, "loadTopics: args is not null...");
            String category = args.getString(TOPICS_CATEGORY_TO_LOAD);
            preferredUrl = new HttpUrl.Builder()
                    .scheme(RADAR_URL_SCHEME)
                    .host(RADAR_URL_HOST)
                    .addPathSegment("c")
                    .addPathSegment(category+".json")
                    .addQueryParameter(RADAR_URL_PAGE_QUERY, String.valueOf(page))
                    .build();
            Log.e(LOG_TAG, "loadTopics: "+preferredUrl.toString());
        }
        fetcherTask = new TopicsFetcherTask(preferredUrl);
        fetcherTask.execute(itemCount, currentPage);
    }

    public void onListEnd() {
        if (!executing) {
            currentPage++;
            executing = true;
            loadTopics(topicsListView.getRealCount(), currentPage);
        }
    }

    @Override
    public void onScrollCalled(int firstVisibleItem, int visibleItemCount, int totalItemCount) {}

    private class TopicsFetcherTask extends AsyncTask<Integer, Void, ArrayList<Topic>> {
        //url for loading topics other than the latest
        private HttpUrl topicsUrl = null;

        public TopicsFetcherTask(HttpUrl url){
            super();
            topicsUrl = url;
        }

        protected ArrayList<Topic> doInBackground (Integer... params) {
            ArrayList<Topic> loadedTopics = new ArrayList<>();
            //the next page to be loaded
            int pageToLoad = params[1];

            String jsonString;
            try {
                //if no url was set, default to loading the latest topics
                if (topicsUrl == null){
                    topicsUrl = HttpRequestBuilderHelper.buildTopicUrlWithPage(pageToLoad);
                }
                Log.e(LOG_TAG, "doInBackground"+topicsUrl.toString());
                jsonString = ApiHelper.GET(okHttpClient, topicsUrl);
            } catch (IOException ioe) {
                jsonString = null;
                if (topicsListView != null) {
                    Snackbar.make(topicsListView, "Failed to retrieve data", Snackbar.LENGTH_SHORT)
                            .show();
                }
            }
            if (jsonString != null) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    JsonNode response = mapper.readTree(jsonString);
                    //first, make a map of the participating users on this page which is returned in
                    //the first node from the json response
                    Map<Integer, User> participants = new HashMap<>();
                    JsonNode usersContainer = response.path("users");
                    Iterator<JsonNode> userNodeIterator = usersContainer.elements();

                    while (userNodeIterator.hasNext()) {
                        User user = mapper.readValue(userNodeIterator.next().traverse(), User.class);
                        //use the id as a key for better querying
                        participants.put(user.getId(), user);
                    }
                    JsonNode topicsContainer = response.path("topic_list");
                    JsonNode topicsNode = topicsContainer.path("topics");
                    Iterator<JsonNode> nodeIterator = topicsNode.elements();

                    while (nodeIterator.hasNext()) {
                        Topic topic = mapper.readValue(nodeIterator.next().traverse(), Topic.class);
                        //todo simply set the poster object and let callers ask for what they need
                     //   topic.setPosterUsername(participants.get(topic.getPosterId()).getUsername());
                        User info = participants.get(topic.getPoster().getUserId());
                        String username = info.getUsername();

                        //topic.setPosterUsername(String.valueOf(topic.getPoster().getUserId()));
                        topic.setPosterUsername(username);
                        loadedTopics.add(topic);
                    }
                } catch (Exception e) {
                    Log.i(LOG_TAG, e.getMessage());
                }
            }
            return loadedTopics;
        }

        protected void onPostExecute(ArrayList<Topic> result) {
            super.onPostExecute(result);
            topicsListView.appendItems(result);
            executing = false;
        }
    }
}
