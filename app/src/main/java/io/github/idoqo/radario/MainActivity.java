package io.github.idoqo.radario;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import io.github.idoqo.radario.model.Topic;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initTopics();
    }

    private void initTopics(){
        String jsonString = Utils.loadJsonFromAsset(this, "topic.json");
        String msg = "";
        ObjectMapper mapper = new ObjectMapper();
        try{
            Topic topic = mapper.readValue(jsonString, Topic.class);
            msg = topic.getTitle();
        } catch (JsonParseException jpe){
            msg = jpe.getMessage();
        } catch (JsonMappingException jpe){
            msg = jpe.getMessage();
        } catch (IOException ioe){
            msg = ioe.getMessage();
        }
        TextView tv = new TextView(this);
        tv.setText(msg);
        LinearLayout rootLayout = (LinearLayout)findViewById(R.id.content_main);
        rootLayout.addView(tv);
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
