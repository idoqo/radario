package io.github.idoqo.radario;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.w3c.dom.Text;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.github.idoqo.radario.adapter.CommentsAdapter;
import io.github.idoqo.radario.lib.EndlessScrollListView;
import io.github.idoqo.radario.lib.EndlessScrollListener;
import io.github.idoqo.radario.model.Comment;

public class TopicDiscussionActivity extends AppCompatActivity {

    private static final String LOG_TAG = "TopicDiscussionActivity";
    public static final String TOPIC_TITLE_EXTRA = "topic_title";
    public static final String TOPIC_ID_EXTRA = "topic_id";
    public static final String TOPIC_OP_EXTRA = "topic_poster";
    public static final String TOPIC_CATEGORY_EXTRA = "topic_category";

    private ListView threadsListView;
    private CommentsAdapter commentsAdapter;
    private ArrayList<Comment> commentList;

    private CommentsFetcherTask commentsFetcherTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_discussion);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        commentList = new ArrayList<>();
        commentsAdapter = new CommentsAdapter(this, commentList);
        threadsListView = (ListView) findViewById(R.id.discussion_top_level_view);
        threadsListView.setAdapter(commentsAdapter);
        initThread();
    }

    private void initThread(){
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String postTitle = extras.getString(TOPIC_TITLE_EXTRA);
            //int postId = extras.getInt(TOPIC_ID_EXTRA);
            int postId = 1;
            String category = extras.getString(TOPIC_CATEGORY_EXTRA);
            Integer originalPoster = extras.getInt(TOPIC_OP_EXTRA);

            View headerView = LayoutInflater.from(this).inflate(R.layout.topic_discussion_header, null);
            TextView titleView = (TextView) headerView.findViewById(R.id.active_topic_title);
            TextView categoryView = (TextView) headerView.findViewById(R.id.active_topic_category);
            titleView.setText(postTitle);
            categoryView.setText(category);

            threadsListView.addHeaderView(headerView);

            commentsFetcherTask = new CommentsFetcherTask();
            //pass the topic id to be used in fetching the comments from server
            commentsFetcherTask.execute(postId);
        }
    }

    private class CommentsFetcherTask extends AsyncTask<Integer, Void, ArrayList<Comment>> {
        public ArrayList<Comment> doInBackground(Integer... params) {
            ArrayList<Comment> loadedComments = new ArrayList<>();
            int topicId = params[0];
            String jsonString = Utils.loadJsonFromAsset(TopicDiscussionActivity.this, "7769.json");
            if (jsonString != null) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    JsonNode postsNode = mapper.readTree(jsonString);
                    JsonNode postStreamNode = postsNode.path("post_stream");
                    JsonNode posts = postStreamNode.path("posts");
                    Iterator<JsonNode> nodeIterator = posts.elements();

                    while (nodeIterator.hasNext()) {
                        Comment comment = mapper.readValue(nodeIterator.next().traverse(), Comment.class);
                        loadedComments.add(comment);
                    }
                } catch (Exception e) {
                    Log.i(LOG_TAG, e.getMessage());
                }
            }
            return Utils.getCommentsAsThread(loadedComments);
        }

        protected void onPostExecute(ArrayList<Comment> result){
            //threadsListView.appendItems(result);
            for (Comment comment : result){
                commentList.add(comment);
            }
            commentsAdapter.notifyDataSetChanged();
            String msg;
            if (result.size() <= 0) {
                msg = "Nothing to load";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                Log.i(LOG_TAG, msg);
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.topic_discussion_menu, menu);
        return true;
    }

}
