package io.github.idoqo.radario;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;

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
import io.github.idoqo.radario.model.Topic;

public class MainActivity extends AppCompatActivity {

    private ListView topicListView;
    private LinearLayout rootLayout;
    private List<Topic> topicList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        topicList = new ArrayList<Topic>();
        rootLayout = (LinearLayout)findViewById(R.id.content_main);
        topicListView = (ListView)findViewById(R.id.topic_list_view);
        initTopics();
    }

    private void initTopics(){
        String jsonString = Utils.loadJsonFromAsset(this, "latest.json");
        String msg = "";
        ObjectMapper mapper = new ObjectMapper();
        try{
            JsonNode response = mapper.readTree(jsonString);
            JsonNode tps = response.path("topic_list");
            JsonNode topicsNode = tps.path("topics");
            Iterator<JsonNode> nodeIterator = topicsNode.elements();

            while (nodeIterator.hasNext()){
                Topic topic = mapper.readValue(nodeIterator.next().traverse(), Topic.class);
                topicList.add(topic);
            }
            //List<Topic> topics = mapper.readValue(jsonString, new TypeReference<List<Topic>>(){});
            topicListView.setAdapter(new TopicAdapter(this, topicList));
        } catch (JsonParseException jpe){
            msg = jpe.getMessage();
        } catch (JsonMappingException jpe){
            msg = jpe.getMessage();
        } catch (IOException ioe){
            msg = ioe.getMessage();
        }
        Log.e("MainActivity", msg);
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
