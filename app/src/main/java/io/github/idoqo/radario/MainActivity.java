package io.github.idoqo.radario;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.github.idoqo.radario.adapter.TopicAdapter;
import io.github.idoqo.radario.lib.EndlessScrollListView;
import io.github.idoqo.radario.lib.EndlessScrollListener;
import io.github.idoqo.radario.lib.EndlessScrollListenerInterface;
import io.github.idoqo.radario.model.Topic;

public class MainActivity extends AppCompatActivity implements EndlessScrollListenerInterface{

    private static final String LOG_TAG = "MainActivity";

    private EndlessScrollListView topicsListView;
    private LinearLayout rootLayout;
    private TopicAdapter topicAdapter;
    private EndlessScrollListener scrollListener;
    private TopicFetcherTask fetcherTask;
    private boolean executing = false;

    private int currentPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rootLayout = (LinearLayout)findViewById(R.id.content_main);
        topicsListView = (EndlessScrollListView)findViewById(R.id.topic_list_view);
        scrollListener = new EndlessScrollListener(this);
        topicAdapter = new TopicAdapter(this);

        topicsListView.setListener(scrollListener);
        topicsListView.setAdapter(topicAdapter);
        initTopics();
    }

    private void initTopics(){
        String jsonString = Utils.loadJsonFromAsset(this, "latest1.json");
        String msg = "";
        ObjectMapper mapper = new ObjectMapper();
        try{
            JsonNode response = mapper.readTree(jsonString);
            JsonNode tps = response.path("topic_list");
            JsonNode topicsNode = tps.path("topics");
            Iterator<JsonNode> nodeIterator = topicsNode.elements();
            ArrayList<Topic> initialTops = new ArrayList<>();

            while (nodeIterator.hasNext()){
                Topic topic = mapper.readValue(nodeIterator.next().traverse(), Topic.class);
                initialTops.add(topic);
            }
            //List<Topic> topics = mapper.readValue(jsonString, new TypeReference<List<Topic>>(){});
            topicsListView.appendItems(initialTops);
        } catch (JsonParseException jpe){
            msg = jpe.getMessage();
        } catch (JsonMappingException jpe){
            msg = jpe.getMessage();
        } catch (IOException ioe){
            msg = ioe.getMessage();
        }
        Log.e("MainActivity", msg);
    }

    public void onListEnd() {
        if (!executing) {
            currentPage++;
            Toast.makeText(this, "nearing end of list...", Toast.LENGTH_SHORT).show();
            Log.i(LOG_TAG, "nearing end of list...");
            Log.i(LOG_TAG, "current count is "+topicsListView.getRealCount());
            executing = true;
            fetcherTask = new TopicFetcherTask();
            fetcherTask.execute(topicsListView.getRealCount(), currentPage);
        }
    }

    public void onScrollCalled(int firstVisibleItem, int visibleItemCount, int totalItemCount){

    }

    private class TopicFetcherTask extends AsyncTask<Integer, Void, ArrayList<Topic>>
    {

         // currently, two elements are needed to be passed as params, first should be the current
         // number of elements in the list view, second should be the page variable i.e, the next
         // page to be requested...
        protected ArrayList<Topic> doInBackground (Integer... params){
            try{
                Thread.sleep(3000);
            } catch (Exception e){
                Log.i(LOG_TAG, e.getMessage());
            }
            Log.i(LOG_TAG, "Current params[0] value... "+params[0]);
            ArrayList<Topic> followUpTops = new ArrayList<>();
            int page = params[1];
                String fname = "latest"+page+".json";
                Log.i(LOG_TAG, "Loading file: "+fname);
                String jsonString = Utils.loadJsonFromAsset(MainActivity.this, fname);
                if (jsonString != null) {

                ObjectMapper mapper = new ObjectMapper();
                try {
                    JsonNode response = mapper.readTree(jsonString);
                    JsonNode tps = response.path("topic_list");
                    JsonNode topicsNode = tps.path("topics");
                    Iterator<JsonNode> nodeIterator = topicsNode.elements();

                    while (nodeIterator.hasNext()) {
                        Topic topic = mapper.readValue(nodeIterator.next().traverse(), Topic.class);
                        followUpTops.add(topic);
                    }
                } catch (Exception jpe) {
                    Log.i(LOG_TAG, jpe.getMessage());
                }
            }
            return followUpTops;
        }

        protected void onPostExecute(ArrayList<Topic> result){
            topicsListView.appendItems(result);
            executing = false;
            if (result.size() > 0) {
                Toast.makeText(getApplicationContext(), "Loaded " + String.valueOf(result.size()) + " items", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "No more items to load", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
