package io.github.idoqo.radario;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import io.github.idoqo.radario.adapter.CommentsAdapter;
import io.github.idoqo.radario.helpers.ApiHelper;
import io.github.idoqo.radario.helpers.HttpRequestBuilderHelper;
import io.github.idoqo.radario.model.Comment;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class TopicDiscussionActivity extends AppCompatActivity {

    private static final String LOG_TAG = "TopicDiscussionActivity";
    public static final String TOPIC_TITLE_EXTRA = "topic_title";
    public static final String TOPIC_ID_EXTRA = "topic_id";
    public static final String TOPIC_OP_EXTRA = "topic_poster";
    public static final String TOPIC_CATEGORY_EXTRA = "topic_category";
    public static final String TOPIC_LIKE_COUNT_EXTRA = "like_count";
    public static final String TOPIC_COMMENT_COUNT_EXTRA = "comment_count";
    public static final String TOPIC_RELATIVE_TIME_EXTRA = "posted_since";

    private ListView threadsListView;
    private CommentsAdapter commentsAdapter;
    private ArrayList<Comment> commentList;

    private OkHttpClient okHttpClient;

    private boolean executing = false;
    private AVLoadingIndicatorView loadingIndicator;
    private SwipeRefreshLayout commentsRefresher;

    private CommentsFetcherTask commentsFetcherTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_discussion);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        okHttpClient = new OkHttpClient();

        commentList = new ArrayList<>();
        commentsAdapter = new CommentsAdapter(this, commentList);
        threadsListView = (ListView) findViewById(R.id.discussion_top_level_view);
        threadsListView.setAdapter(commentsAdapter);
        commentsRefresher = (SwipeRefreshLayout) findViewById(R.id.refresh_comments);
        loadingIndicator = (AVLoadingIndicatorView) findViewById(R.id.comments_loading_indicator);

        commentsRefresher.setOnRefreshListener(onCommentsRefresh());
        Bundle extras = getIntent().getExtras();
        initHeaderView(extras);
        loadThread();
    }

    private SwipeRefreshLayout.OnRefreshListener onCommentsRefresh(){
        return new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadThread();
                commentsRefresher.setRefreshing(false);
            }
        };
    }

    private void initHeaderView(Bundle extras) {
        if (extras != null) {
            String title = extras.getString(TOPIC_TITLE_EXTRA);
            //id to use when requesting for the comments via http
            //int id = extras.getInt(TOPIC_ID_EXTRA);
            int id = 1;
            String category = extras.getString(TOPIC_CATEGORY_EXTRA);
            String poster = extras.getString(TOPIC_OP_EXTRA);
            String timePosted = extras.getString(TOPIC_RELATIVE_TIME_EXTRA);
            int likeCount = extras.getInt(TOPIC_LIKE_COUNT_EXTRA);
            int commentCount = extras.getInt(TOPIC_COMMENT_COUNT_EXTRA);

            View headerView = LayoutInflater.from(this).inflate(R.layout.topic_discussion_header, null);
            TextView titleView = (TextView) headerView.findViewById(R.id.active_topic_title);
            TextView categoryView = (TextView) headerView.findViewById(R.id.active_topic_category);
            TextView likeCountView = (TextView) headerView.findViewById(R.id.number_of_likes);
            TextView commentCountView = (TextView) headerView.findViewById(R.id.number_of_comments);
            TextView timePostedView = (TextView) headerView.findViewById(R.id.posted_since);
            TextView postedByView = (TextView) headerView.findViewById(R.id.topic_poster);

            titleView.setText(title);
            categoryView.setText(category);
            timePostedView.setText(timePosted);
            postedByView.setText(poster);

            String likesQualifier = (likeCount <= 1) ? "like" : "likes";
            String commentsQualifier = (commentCount <= 1) ? "comment" : "comments";

            likeCountView.setText(getResources().getString(R.string.item_like_count,
                    likeCount, likesQualifier));
            commentCountView.setText(getResources().getString(R.string.item_comment_count,
                    commentCount, commentsQualifier));

            threadsListView.addHeaderView(headerView);
        }
    }

    private void loadThread(){
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            executing = true;
            loadingIndicator.setVisibility(View.VISIBLE);
            commentsFetcherTask = new CommentsFetcherTask();
            int postId = extras.getInt(TOPIC_ID_EXTRA);
            //pass the topic id to be used in fetching the comments from server
            commentsFetcherTask.execute(postId);
        }
    }

    private class CommentsFetcherTask extends AsyncTask<Integer, Void, ArrayList<Comment>> {
        public ArrayList<Comment> doInBackground(Integer... params) {
            ArrayList<Comment> loadedComments = new ArrayList<>();
            int topicId = params[0];
            //String jsonString = Utils.loadJsonFromAsset(TopicDiscussionActivity.this, "5272.json");
            String jsonString;
            try {
                HttpUrl commentsUrl = HttpRequestBuilderHelper.buildTopicCommentsUrl(topicId);
                jsonString = ApiHelper.GET(okHttpClient, commentsUrl);
            } catch (IOException ioe) {
                jsonString = null;
                Snackbar.make(threadsListView, "Failed to retrieve data", Snackbar.LENGTH_SHORT)
                        .show();
//                Log.e(LOG_TAG, ioe.getMessage());
            }

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
            executing = false;
            loadingIndicator.setVisibility(View.GONE);
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
