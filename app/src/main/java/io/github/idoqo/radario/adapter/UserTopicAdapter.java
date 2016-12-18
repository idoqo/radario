package io.github.idoqo.radario.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import io.github.idoqo.radario.R;
import io.github.idoqo.radario.TopicDiscussionActivity;
import io.github.idoqo.radario.helpers.DateTimeHelper;
import io.github.idoqo.radario.model.Category;
import io.github.idoqo.radario.model.UserAction;

public class UserTopicAdapter extends RecyclerView.Adapter<UserTopicAdapter.TopicViewHolder>{

    private Context context;
    private ArrayList<UserAction> topics;

    public UserTopicAdapter(Context c, ArrayList<UserAction> ts){
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

    public void setData(ArrayList<UserAction> data){
        topics = data;
    }

    public void onBindViewHolder(TopicViewHolder holder, int position){
        final UserAction topic = topics.get(position);
        holder.title.setText(topic.getParentTopic());
        holder.category.setText(Category.getnameFromId(topic.getCategoryId()));
        String  timeCount;
        String timeQualifier;
        try {
            Date now = new Date();
            Date topicCreation = topic.getCreatedAtAsDate();
            String[] cau = DateTimeHelper.getCountAndUnit(topicCreation, now);
            timeCount = cau[0];
            timeQualifier = cau[1];
        } catch (ParseException pe) {
            timeCount = "long";
            timeQualifier = "long";
        }

        holder.relativeTime.setText(context.getResources().getString(R.string.relative_time_past,
                timeCount, timeQualifier));

        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent threadIntent = new Intent(context, TopicDiscussionActivity.class);
                threadIntent.putExtra(TopicDiscussionActivity.TOPIC_TITLE_EXTRA, topic.getParentTopic());
                threadIntent.putExtra(TopicDiscussionActivity.TOPIC_ID_EXTRA, topic.getTopicId());
                threadIntent.putExtra(TopicDiscussionActivity.TOPIC_CATEGORY_EXTRA,
                        Category.getnameFromId(topic.getCategoryId()));
                threadIntent.putExtra(TopicDiscussionActivity.TOPIC_OP_EXTRA, topic.getUsername());
                threadIntent.putExtra(TopicDiscussionActivity.TOPIC_LIKE_COUNT_EXTRA,
                        0);
                threadIntent.putExtra(TopicDiscussionActivity.TOPIC_COMMENT_COUNT_EXTRA,
                        0);
                threadIntent.putExtra(TopicDiscussionActivity.TOPIC_RELATIVE_TIME_EXTRA,
                        "");

                context.startActivity(threadIntent);
            }
        });
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
