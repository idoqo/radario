package io.github.idoqo.radario;

import android.content.Intent;
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
import android.widget.AdapterView;
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
import io.github.idoqo.radario.model.Category;
import io.github.idoqo.radario.model.Topic;
import io.github.idoqo.radario.model.UrlResponse;
import io.github.idoqo.radario.model.User;
import io.github.idoqo.radario.url.RadarUrlParser;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class TopicListFragment extends Fragment implements EndlessScrollListenerInterface {
    private static final String LOG_TAG = "TopicListFragment";

    private OkHttpClient okHttpClient;

    private EndlessScrollListView topicsListView;
    private EndlessScrollListener scrollListener;
    private SwipeRefreshLayout refreshTopicLayout;
    private FloatingActionButton createTopicButton;

    private TopicAdapter topicAdapter;
    private TopicsFetcherTask fetcherTask;
    private boolean executing = false;

    //the current page to be loaded, starts at 1
    private int currentPage = 0;

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
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        };
    }

    private SwipeRefreshLayout.OnRefreshListener refreshTopicList(){
        return new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //reset the current page counter so as to load the latest topics
                currentPage = 0;
                fetcherTask = new TopicsFetcherTask();
                //also, pass 0 as the first param(number of items in the list view)
                fetcherTask.execute(0, currentPage);
                refreshTopicLayout.setRefreshing(false);
            }
        };
    }

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        okHttpClient = new OkHttpClient().newBuilder()
            .addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    final Request original = chain.request();
                    final Request authorized = original.newBuilder()
                            .addHeader("Cookie", "_forum_session=cDArUVZvOU4xcjd5VlJReTQ3V1ZMWmRrT3N1MkNQZEJoOXg0U2pGYXFacDFGOVlGajZXRFY3NjBBQXAyNmptckw1cno4ZzF1b1ZYTk0wdTRUbjc0WXc9PS0tRDl5T2thbFBySUlSYnBBekJWdm5tZz09--0626a8be3f55473064509109a6f01b43f15073b5;_t=139d29005eb2b8fa47c33359f0cb5b67")
                            .build();
                    return chain.proceed(authorized);
                }
            }).build();
        fetcherTask = new TopicsFetcherTask();
        //since nothing is in our list yet, the initial list count is zero
        fetcherTask.execute(0, currentPage);
    }

    public void onListEnd() {
        if (!executing) {
            currentPage++;
            Toast.makeText(getContext(), "nearing end of list", Toast.LENGTH_SHORT).show();
            executing = true;
            fetcherTask = new TopicsFetcherTask();
            fetcherTask.execute(topicsListView.getRealCount(), currentPage);
        }
    }

    @Override
    public void onScrollCalled(int firstVisibleItem, int visibleItemCount, int totalItemCount) {}

    public class TopicsFetcherTask extends AsyncTask<Integer, Void, ArrayList<Topic>> {
        private boolean isLogged; //is a user logged in?

        protected ArrayList<Topic> doInBackground (Integer... params) {
            ArrayList<Topic> loadedTopics = new ArrayList<>();
            //the next page to be loaded
            int pageToLoad = params[1];

            /*String filename = "latest"+pageToLoad+".json";
            Log.i(LOG_TAG, "Loading file "+filename+" from assets");
            String jsonString = Utils.loadJsonFromAsset(getActivity(), filename);*/
            HttpUrl topicsUrl = HttpRequestBuilderHelper.buildTopicUrlWithPage(pageToLoad);
            String jsonString;
            try {
                jsonString = ApiHelper.GET(okHttpClient, topicsUrl);
                Log.e(LOG_TAG, jsonString);
            } catch (IOException ioe) {
                jsonString = null;
                Snackbar.make(topicsListView, "Failed to retrieve data", Snackbar.LENGTH_SHORT)
                        .show();
                Log.e(LOG_TAG, ioe.getMessage());
            }
            if (jsonString != null) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    UrlResponse urlResponse = mapper.readValue(jsonString, UrlResponse.class);
                    isLogged = urlResponse.isCanCreateTopic();

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
            String msg;
            if (isLogged) {
                msg = "Can create topic is TRUE";
            } else {
                msg = "Can create topic is FALSE";
            }
            Snackbar.make(topicsListView, msg, Snackbar.LENGTH_INDEFINITE)
                    .show();
            if (result.size() > 0) {
                Toast.makeText(getContext(), "Loaded "+String.valueOf(result.size()) + "items",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "No more items to load", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
