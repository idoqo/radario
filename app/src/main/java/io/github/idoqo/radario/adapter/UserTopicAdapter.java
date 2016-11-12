package io.github.idoqo.radario.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import io.github.idoqo.radario.R;
import io.github.idoqo.radario.model.Category;
import io.github.idoqo.radario.model.Topic;
import io.github.idoqo.radario.model.User;

//todo this should be the ONLY topic adapter since it implements the recycler view Adapter...
public class UserTopicAdapter extends RecyclerView.Adapter<UserTopicAdapter.TopicViewHolder>{

    private Context context;
    private ArrayList<Topic> topics;

    public UserTopicAdapter(Context c, ArrayList<Topic> ts){
        super();
        context = c;
        topics = ts;
    }

    public Context getContext(){
        return context;
    }

    @Override
    public TopicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View topicView = inflater.inflate(R.layout.topic_item, parent, false);
        return new TopicViewHolder(topicView);
    }

    public void setData(ArrayList<Topic> data){
        topics = data;
    }

    public void onBindViewHolder(TopicViewHolder holder, int position){
        Topic topic = topics.get(position);
        holder.title.setText(topic.getTitle());
//        holder.poster.setText(topic.getPosterUsername());
        holder.category.setText(Category.getnameFromId(topic.getCategory()));
        holder.relativeTime.setText("30 minutes");
    }

    public int getItemCount(){
        return topics.size();
    }

    static class TopicViewHolder extends RecyclerView.ViewHolder{
        TextView poster, category, relativeTime, title;
        ImageView likeButton, commentButton, shareButton;

        public TopicViewHolder(View itemView) {
            super(itemView);

            poster = (TextView) itemView.findViewById(R.id.topic_poster_username);
            category = (TextView) itemView.findViewById(R.id.topic_category);
            relativeTime = (TextView) itemView.findViewById(R.id.topic_posted_time);
            title = (TextView) itemView.findViewById(R.id.topic_title);
            likeButton = (ImageView) itemView.findViewById(R.id.action_like_topic);
            likeButton = (ImageView) itemView.findViewById(R.id.action_topic_comments);
            likeButton = (ImageView) itemView.findViewById(R.id.action_share_topic);
        }
    }
}
