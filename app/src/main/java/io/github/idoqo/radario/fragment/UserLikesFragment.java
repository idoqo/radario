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
import io.github.idoqo.radario.adapter.UserLikeAdapter;
import io.github.idoqo.radario.model.UserLike;
import io.github.idoqo.radario.model.UserReply;


public class UserLikesFragment extends Fragment {
    private ArrayList<UserLike> userLikes = new ArrayList<>();
    private UserLikeAdapter likeAdapter;
    private LikesFetcherTask fetcherTask;
    private RecyclerView likesListView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fetcherTask = new LikesFetcherTask();
        fetcherTask.execute(0, 1);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_user_likes, container, false);
        likesListView = (RecyclerView) contentView.findViewById(R.id.user_likes_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        likeAdapter = new UserLikeAdapter(getActivity(), userLikes);
        likesListView.setAdapter(likeAdapter);
        likesListView.setLayoutManager(layoutManager);

        return contentView;
    }

    private class LikesFetcherTask extends AsyncTask<Integer, Void, ArrayList<UserLike>>
    {
        @Override
        protected ArrayList<UserLike> doInBackground(Integer... integers) {
            String filename = "mark_likes.json";
            String jsonString = Utils.loadJsonFromAsset(getActivity(), filename);
            ArrayList<UserLike> likes = new ArrayList<>();
            if (jsonString != null) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    JsonNode response = mapper.readTree(jsonString);
                    JsonNode repliesPath = response.path("user_actions");
                    Iterator<JsonNode> repliesIterator = repliesPath.elements();

                    while (repliesIterator.hasNext()) {
                        UserLike reply = mapper.readValue(repliesIterator.next().traverse(), UserLike.class);
                        likes.add(reply);
                    }
                } catch (Exception e) {
                    Log.i("UserCommentsFragment", e.getMessage());
                }
            }
            return likes;
        }

        public void onPostExecute(ArrayList<UserLike> result) {
            super.onPostExecute(result);
            userLikes = result;
            likeAdapter.setData(result);
            Toast.makeText(getActivity(), "loaded results", Toast.LENGTH_SHORT).show();
        }
    }
}
