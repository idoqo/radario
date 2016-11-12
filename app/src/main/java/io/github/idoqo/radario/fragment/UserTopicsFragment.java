package io.github.idoqo.radario.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.github.idoqo.radario.R;
import io.github.idoqo.radario.TopicListFragment;
import io.github.idoqo.radario.Utils;
import io.github.idoqo.radario.adapter.UserTopicAdapter;
import io.github.idoqo.radario.model.Topic;
import io.github.idoqo.radario.model.User;


public class UserTopicsFragment extends Fragment {
    private TopicsFetcherTask fetcherTask;
    private UserTopicAdapter topicAdapter;
    private RecyclerView topicsList;
    private int currentPage = 1;
    private LinearLayoutManager layoutManager;
    private ArrayList<Topic> userTopics;

    private TextView emptyTopicsView;

    private static final String LOG_TAG = "UserTopicsFragment";

    public UserTopicsFragment(){

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //since nothing is in our list yet, the initial list count is zero
        fetcherTask = new TopicsFetcherTask();
        fetcherTask.execute(0, currentPage);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View content = inflater.inflate(R.layout.fragment_user_topics, container, false);
        topicsList = (RecyclerView) content.findViewById(R.id.user_topics_list);
        emptyTopicsView = (TextView) content.findViewById(R.id.empty_user_topics);

        layoutManager = new LinearLayoutManager(getActivity());
        topicsList.setHasFixedSize(true);
        topicsList.setLayoutManager(layoutManager);

        if (userTopics == null) {
            userTopics = new ArrayList<>();
        }
        if (topicAdapter == null) {
            topicAdapter = new UserTopicAdapter(getActivity(), userTopics);
        }
        topicsList.setAdapter(topicAdapter);

        return content;
    }

    private class TopicsFetcherTask extends AsyncTask<Integer, Void, ArrayList<Topic>>
    {
        protected ArrayList<Topic> doInBackground(Integer... params) {
            //simulate delay
            try {
                Thread.sleep(3000);
            } catch (Exception e) {
                Log.i(LOG_TAG, e.getMessage());
            }

            ArrayList<Topic> loadedTopics = new ArrayList<>();
            //the next page to be loaded
            int pageToLoad = params[1];

            String filename = "latest" + pageToLoad + ".json";
            Log.i(LOG_TAG, "Loading file " + filename + " from assets");
            String jsonString = Utils.loadJsonFromAsset(getActivity(), filename);
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
                        User info = participants.get(topic.getPoster().getUserId());
                        String username = info.getUsername();

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
            if (result.size() > 0) {
                emptyTopicsView.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Loaded " + String.valueOf(result.size()) + "items",
                        Toast.LENGTH_SHORT).show();
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
