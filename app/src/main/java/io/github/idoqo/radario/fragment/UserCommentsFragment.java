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
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import io.github.idoqo.radario.R;
import io.github.idoqo.radario.UserProfileActivity;
import io.github.idoqo.radario.adapter.UserReplyAdapter;
import io.github.idoqo.radario.helpers.ApiHelper;
import io.github.idoqo.radario.helpers.HttpRequestBuilderHelper;
import io.github.idoqo.radario.model.User;
import io.github.idoqo.radario.model.UserReply;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;


public class UserCommentsFragment extends Fragment {
    public UserCommentsFragment(){

    }
    private ArrayList<UserReply> userReplies = new ArrayList<>();
    private UserReplyAdapter replyAdapter;
    private RepliesFetcherTask fetcherTask;
    private RecyclerView repliesListView;
    private HttpUrl commentsUrl;
    private OkHttpClient okHttpClient;

    private User user = null;

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

            commentsUrl = HttpRequestBuilderHelper.buildUserUrl(username,
                    HttpRequestBuilderHelper.USER_FILTER_REPLIES);
            fetcherTask = new RepliesFetcherTask(commentsUrl);
            fetcherTask.execute(0, 1);
        } else {
            //what the actual fuck?
            Snackbar.make(repliesListView, "Where is your god now? ¯\\_(ツ)_/¯", Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_user_replies, container, false);
        repliesListView = (RecyclerView) contentView.findViewById(R.id.user_replies_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        replyAdapter = new UserReplyAdapter(getActivity(), userReplies);
        repliesListView.setAdapter(replyAdapter);
        repliesListView.setLayoutManager(layoutManager);

        return contentView;
    }

    private class RepliesFetcherTask extends AsyncTask<Integer, Void, ArrayList<UserReply>>
    {
        private HttpUrl userLikesUrl;

        public RepliesFetcherTask(HttpUrl url){
            super();
            userLikesUrl = url;
        }

        @Override
        protected ArrayList<UserReply> doInBackground(Integer... integers) {
            Log.e("UserCommentsFragment", "doInBackground: "+ userLikesUrl);
            /*String filename = "mark_replies.json";
            String jsonString = Utils.loadJsonFromAsset(getActivity(), filename);*/
            String jsonString;
            try {
                jsonString = ApiHelper.GET(okHttpClient, userLikesUrl);
            } catch (IOException ioe) {
                Snackbar.make(repliesListView, "Failed to retrieve data", Snackbar.LENGTH_LONG)
                        .show();
                jsonString = null;
            }
            ArrayList<UserReply> replies = new ArrayList<>();
            if (jsonString != null) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    JsonNode response = mapper.readTree(jsonString);
                    JsonNode repliesPath = response.path("user_actions");
                    Iterator<JsonNode> repliesIterator = repliesPath.elements();

                    while (repliesIterator.hasNext()) {
                        UserReply reply = mapper.readValue(repliesIterator.next().traverse(), UserReply.class);
                        replies.add(reply);
                    }
                } catch (Exception e) {
                    Log.i("UserCommentsFragment", e.getMessage());
                }
            }
            return replies;
        }

        public void onPostExecute(ArrayList<UserReply> result) {
            super.onPostExecute(result);
            userReplies = result;
            replyAdapter.setData(result);
            Toast.makeText(getActivity(), "loaded results: "+result.size(), Toast.LENGTH_SHORT).show();
        }
    }
}
