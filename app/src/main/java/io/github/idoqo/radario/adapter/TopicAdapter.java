package io.github.idoqo.radario.adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import io.github.idoqo.radario.R;
import io.github.idoqo.radario.TopicDiscussionActivity;
import io.github.idoqo.radario.UserProfileActivity;
import io.github.idoqo.radario.helpers.DateTimeHelper;
import io.github.idoqo.radario.lib.EndlessScrollAdapter;
import io.github.idoqo.radario.model.Category;
import io.github.idoqo.radario.model.Topic;

public class TopicAdapter extends EndlessScrollAdapter
{
    private ArrayList<Topic> topics;
    private Context context;

    public TopicAdapter(Context context){
        super(context);
        this.context = context;
        topics = new ArrayList<>();
    }

    public ArrayList<Topic> getItems() {return topics;}

    public void removeAll(){
        topics = new ArrayList<>();
    }

    public void addItems(Collection items){
        if(items.size() > 0){
            this.topics.addAll(items);
        } else {
            super.setDoneLoading();
        }
        notifyDataSetChanged();
    }

    public Object getRealItem(int position) {return topics.get(position); }

    public View getRealView(LayoutInflater inflater, int position, View convertView,
                            ViewGroup parent){
        convertView = inflater.inflate(R.layout.topic_item, null);
        TextView titleView = (TextView) convertView.findViewById(R.id.topic_title);
        TextView catView = (TextView)convertView.findViewById(R.id.topic_category);
        TextView opView = (TextView)convertView.findViewById(R.id.topic_poster_username);
        TextView postedTimeView = (TextView) convertView.findViewById(R.id.topic_posted_time);
        TextView numLikesView = (TextView) convertView.findViewById(R.id.number_of_likes);
        TextView numCommentsView = (TextView) convertView.findViewById(R.id.number_of_comments);
        final ImageView likeButton = (ImageView) convertView.findViewById(R.id.action_like_topic);

        Topic tp = topics.get(position);
        titleView.setText(tp.getTitle());
        catView.setText(Category.getnameFromId(tp.getCategory()));
        opView.setText(tp.getPosterUsername());
        String likesQualifier = (tp.getLikeCount() <= 1) ? " like" : " likes";
        String commentsQualifier = (tp.getPostsCount() <= 1) ? " comment" : " comments";
        numLikesView.setText(context.getResources().getString(R.string.item_like_count,
                tp.getLikeCount(), likesQualifier));
        numCommentsView.setText(context.getResources().getString(R.string.item_comment_count,
                tp.getPostsCount(), commentsQualifier));

        String  timeCount;
        String timeQualifier;
        try {
            Date now = new Date();
            Date topicCreation = tp.getCreatedAtAsDate();
            String[] cau = DateTimeHelper.getCountAndUnit(topicCreation, now);
            timeCount = cau[0];
            timeQualifier = cau[1];
        } catch (ParseException pe) {
            timeCount = "long";
            timeQualifier = "long";
        }
        tp.setDisplayableRelativeTime(timeCount+" "+timeQualifier);

        postedTimeView.setText(context.getResources().getString(R.string.relative_time_past,
                timeCount, timeQualifier));

        titleView.setOnClickListener(onTitleClickListener(tp));
        opView.setOnClickListener(onUsernameClickListener(tp));

        return convertView;
    }

    private View.OnClickListener onTitleClickListener(final Topic clickedTopic) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewThreadIntent = new Intent(context, TopicDiscussionActivity.class);
                viewThreadIntent.putExtra(TopicDiscussionActivity.TOPIC_TITLE_EXTRA, clickedTopic.getTitle());
                viewThreadIntent.putExtra(TopicDiscussionActivity.TOPIC_ID_EXTRA, clickedTopic.getId());
                viewThreadIntent.putExtra(TopicDiscussionActivity.TOPIC_CATEGORY_EXTRA,
                        Category.getnameFromId(clickedTopic.getCategory()));
                viewThreadIntent.putExtra(TopicDiscussionActivity.TOPIC_OP_EXTRA, clickedTopic.getPosterUsername());
                viewThreadIntent.putExtra(TopicDiscussionActivity.TOPIC_LIKE_COUNT_EXTRA,
                        clickedTopic.getLikeCount());
                viewThreadIntent.putExtra(TopicDiscussionActivity.TOPIC_COMMENT_COUNT_EXTRA,
                        clickedTopic.getPostsCount());
                viewThreadIntent.putExtra(TopicDiscussionActivity.TOPIC_RELATIVE_TIME_EXTRA,
                        clickedTopic.getDisplayableRelativeTime());

                context.startActivity(viewThreadIntent);
            }
        };
    }

    private View.OnClickListener onUsernameClickListener(final Topic clickedTopic) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String posterUsername = clickedTopic.getPosterUsername();
                int posterId = clickedTopic.getPoster().getUserId();

                Intent profileIntent = new Intent(context, UserProfileActivity.class);
                profileIntent.putExtra(UserProfileActivity.EXTRA_USERNAME, posterUsername);
                context.startActivity(profileIntent);
            }
        };
    }

    public View getLoadingView(LayoutInflater inflater, ViewGroup parent){
        return inflater.inflate(R.layout.loading_progress, null);
    }
}