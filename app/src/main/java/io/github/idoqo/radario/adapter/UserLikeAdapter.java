package io.github.idoqo.radario.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import io.github.idoqo.radario.R;
import io.github.idoqo.radario.model.Category;
import io.github.idoqo.radario.model.UserAction;
import io.github.idoqo.radario.url.RadarUrlParser;


public class UserLikeAdapter extends RecyclerView.Adapter<UserLikeAdapter.UserActionViewHolder>
{
    private Context context;
    private ArrayList<UserAction> likes;

    public UserLikeAdapter(Context context, ArrayList<UserAction> likes){
        this.context = context;
        this.likes = likes;
    }

    public int getItemCount(){
        return (likes == null) ? 0 : likes.size();
    }

    public void onBindViewHolder(UserActionViewHolder holder, int position){
        UserAction like = likes.get(position);

        holder.topicTitle.setText(like.getParentTopic());
        String parsedExcerpts = RadarUrlParser.userUrlToIntent(like.getExcerpt());
        holder.excerpt.setText(Html.fromHtml(parsedExcerpts));
        //holder.receiverUsername.setText(like.getReceiverUsername());
        holder.category.setText(Category.getnameFromId(like.getCategoryId()));
    }

    public UserActionViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View rootView = inflater.inflate(R.layout.user_likes_item, parent, false);
        return new UserActionViewHolder(rootView);
    }

    public void setData(ArrayList<UserAction> data) {
        this.likes = data;
        notifyDataSetChanged();
    }

    static class UserActionViewHolder extends RecyclerView.ViewHolder
    {
        TextView topicTitle, excerpt, numberOfLikes, receiverUsername, category, postedTime;
        ImageView likeButton;

        public UserActionViewHolder(View itemView){
            super(itemView);

            topicTitle = (TextView) itemView.findViewById(R.id.parent_topic_title);
            excerpt = (TextView) itemView.findViewById(R.id.reply_excerpt);
            numberOfLikes = (TextView) itemView.findViewById(R.id.number_of_likes);
            receiverUsername = (TextView) itemView.findViewById(R.id.comment_poster);
            category = (TextView) itemView.findViewById(R.id.parent_topic_category);
            postedTime = (TextView) itemView.findViewById(R.id.comment_posted_time);

            excerpt.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }
}
