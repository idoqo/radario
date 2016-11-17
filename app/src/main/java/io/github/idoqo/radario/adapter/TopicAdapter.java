package io.github.idoqo.radario.adapter;


import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.media.Image;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.github.idoqo.radario.R;
import io.github.idoqo.radario.TopicDiscussionActivity;
import io.github.idoqo.radario.UserProfileActivity;
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
                viewThreadIntent.putExtra(TopicDiscussionActivity.TOPIC_OP_EXTRA, 1);
                viewThreadIntent.putExtra(TopicDiscussionActivity.TOPIC_LIKE_COUNT_EXTRA,
                        clickedTopic.getLikeCount());

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