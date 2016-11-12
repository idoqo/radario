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
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Iterator;

import io.github.idoqo.radario.R;
import io.github.idoqo.radario.Utils;
import io.github.idoqo.radario.adapter.UserReplyAdapter;
import io.github.idoqo.radario.model.User;
import io.github.idoqo.radario.model.UserReply;


public class UserCommentsFragment extends Fragment {
    public UserCommentsFragment(){

    }
    private ArrayList<UserReply> userReplies = new ArrayList<>();
    private UserReplyAdapter replyAdapter;
    private RepliesFetcherTask fetcherTask;
    private RecyclerView repliesListView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fetcherTask = new RepliesFetcherTask();
        fetcherTask.execute(0, 1);
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
        @Override
        protected ArrayList<UserReply> doInBackground(Integer... integers) {
            String filename = "mark_replies.json";
            String jsonString = Utils.loadJsonFromAsset(getActivity(), filename);
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
            Toast.makeText(getActivity(), "loaded results", Toast.LENGTH_SHORT).show();
        }
    }
}
