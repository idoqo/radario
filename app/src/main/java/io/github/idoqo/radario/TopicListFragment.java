package io.github.idoqo.radario;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.github.idoqo.radario.adapter.TopicAdapter;
import io.github.idoqo.radario.lib.EndlessScrollListView;
import io.github.idoqo.radario.lib.EndlessScrollListener;
import io.github.idoqo.radario.lib.EndlessScrollListenerInterface;
import io.github.idoqo.radario.model.Category;
import io.github.idoqo.radario.model.Topic;
import io.github.idoqo.radario.model.User;


public class TopicListFragment extends Fragment implements EndlessScrollListenerInterface {
    private static final String LOG_TAG = "TopicListFragment";

    private EndlessScrollListView topicsListView;
    private EndlessScrollListener scrollListener;
    private TopicAdapter topicAdapter;
    private TopicsFetcherTask fetcherTask;
    private boolean executing = false;

    //the current page to be loaded, starts at 1
    private int currentPage = 1;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_topic_list, container, false);

        topicsListView = (EndlessScrollListView) view.findViewById(R.id.topics_list);
        scrollListener = new EndlessScrollListener(this);
        topicAdapter = new TopicAdapter(getActivity());

        topicsListView.setListener(scrollListener);
        topicsListView.setAdapter(topicAdapter);

        //View loaderView = view.findViewById(R.id.frg_topics_loading_view);

        topicsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    Topic clicked = (Topic) adapterView.getItemAtPosition(i);
                    Intent viewThreadIntent = new Intent(getActivity(), TopicDiscussionActivity.class);
                    viewThreadIntent.putExtra(TopicDiscussionActivity.TOPIC_TITLE_EXTRA, clicked.getTitle());
                    viewThreadIntent.putExtra(TopicDiscussionActivity.TOPIC_ID_EXTRA, clicked.getId());
                    viewThreadIntent.putExtra(TopicDiscussionActivity.TOPIC_CATEGORY_EXTRA,
                            Category.getnameFromId(clicked.getCategory()));
                    viewThreadIntent.putExtra(TopicDiscussionActivity.TOPIC_OP_EXTRA, 1);

                    startActivity(viewThreadIntent);
                } catch (IndexOutOfBoundsException oob) {
                    //index out of bounds are thrown if the loading view is clicked
                    //not my fault asshole
                }
            }
        });
        return view;
    }

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //since nothing is in our list yet, the initial list count is zero
        fetcherTask = new TopicsFetcherTask();
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

    private class TopicsFetcherTask extends AsyncTask<Integer, Void, ArrayList<Topic>> {
        protected ArrayList<Topic> doInBackground (Integer... params) {
            //simulate delay
            try {
                Thread.sleep(3000);
            } catch (Exception e) {
                Log.i(LOG_TAG, e.getMessage());
            }

            ArrayList<Topic> loadedTopics = new ArrayList<>();
            //the next page to be loaded
            int pageToLoad = params[1];

            String filename = "latest"+pageToLoad+".json";
            Log.i(LOG_TAG, "Loading file "+filename+" from assets");
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
            if (result.size() > 0) {
                Toast.makeText(getContext(), "Loaded "+String.valueOf(result.size()) + "items",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "No more items to load", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
