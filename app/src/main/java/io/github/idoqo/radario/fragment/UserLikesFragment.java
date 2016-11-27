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
import java.util.ArrayList;
import java.util.Iterator;

import io.github.idoqo.radario.R;
import io.github.idoqo.radario.UserProfileActivity;
import io.github.idoqo.radario.Utils;
import io.github.idoqo.radario.adapter.UserLikeAdapter;
import io.github.idoqo.radario.helpers.ApiHelper;
import io.github.idoqo.radario.helpers.HttpRequestBuilderHelper;
import io.github.idoqo.radario.model.User;
import io.github.idoqo.radario.model.UserAction;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;


public class UserLikesFragment extends Fragment {
    private ArrayList<UserAction> userLikes = new ArrayList<>();
    private UserLikeAdapter likeAdapter;
    private LikesFetcherTask fetcherTask;
    private RecyclerView likesListView;

    private User user;
    private OkHttpClient okHttpClient;
    private HttpUrl likesUrl;

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

            likesUrl = HttpRequestBuilderHelper.buildUserUrl(username,
                    HttpRequestBuilderHelper.USER_FILTER_LIKES);
            fetcherTask = new UserLikesFragment.LikesFetcherTask(likesUrl);
            fetcherTask.execute(0, 1);
        } else {
            //what the actual fuck?
            Snackbar.make(likesListView, "Where is your god now? ¯\\_(ツ)_/¯", Snackbar.LENGTH_LONG)
                    .show();
        }
        fetcherTask = new LikesFetcherTask(likesUrl);
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

    private class LikesFetcherTask extends AsyncTask<Integer, Void, ArrayList<UserAction>>
    {
        HttpUrl userLikesUrl;

        LikesFetcherTask(HttpUrl url){
            super();
            userLikesUrl = url;
        }

        @Override
        protected ArrayList<UserAction> doInBackground(Integer... integers) {
            String jsonString;
            try {
                jsonString = ApiHelper.GET(okHttpClient, userLikesUrl);
            } catch (IOException ioe) {
                Snackbar.make(likesListView, "Failed to retrieve data", Snackbar.LENGTH_LONG)
                        .show();
                jsonString = null;
            }
            ArrayList<UserAction> likes = new ArrayList<>();
            if (jsonString != null) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    JsonNode response = mapper.readTree(jsonString);
                    JsonNode repliesPath = response.path("user_actions");
                    Iterator<JsonNode> repliesIterator = repliesPath.elements();

                    while (repliesIterator.hasNext()) {
                        UserAction reply = mapper.readValue(repliesIterator.next().traverse(), UserAction.class);
                        likes.add(reply);
                    }
                } catch (Exception e) {
                    Log.i("UserCommentsFragment", e.getMessage());
                }
            }
            return likes;
        }

        public void onPostExecute(ArrayList<UserAction> result) {
            super.onPostExecute(result);
            userLikes = result;
            likeAdapter.setData(result);
            if (getActivity() != null) {
                Toast.makeText(getActivity(), "loaded results", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
