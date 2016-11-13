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
import io.github.idoqo.radario.model.UserLike;


public class UserLikeAdapter extends RecyclerView.Adapter<UserLikeAdapter.UserLikeViewHolder>
{
    private Context context;
    private ArrayList<UserLike> likes;

    public UserLikeAdapter(Context context, ArrayList<UserLike> likes){
        this.context = context;
        this.likes = likes;
    }

    public int getItemCount(){
        return (likes == null) ? 0 : likes.size();
    }

    public void onBindViewHolder(UserLikeViewHolder holder, int position){
        UserLike like = likes.get(position);

        holder.topicTitle.setText(like.getParentTopic());
        holder.excerpt.setText(like.getExcerpt());
        holder.receiverUsername.setText(like.getReceiverUsername());
        holder.category.setText(Category.getnameFromId(like.getCategoryId()));
    }

    public UserLikeViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View rootView = inflater.inflate(R.layout.user_likes_item, parent, false);
        return new UserLikeViewHolder(rootView);
    }

    public void setData(ArrayList<UserLike> data) {
        this.likes = data;
        notifyDataSetChanged();
    }

    static class UserLikeViewHolder extends RecyclerView.ViewHolder
    {
        TextView topicTitle, excerpt, numberOfLikes, receiverUsername, category, postedTime;
        ImageView likeButton;

        public UserLikeViewHolder(View itemView){
            super(itemView);

            topicTitle = (TextView) itemView.findViewById(R.id.parent_topic_title);
            excerpt = (TextView) itemView.findViewById(R.id.reply_excerpt);
            numberOfLikes = (TextView) itemView.findViewById(R.id.number_of_likes);
            receiverUsername = (TextView) itemView.findViewById(R.id.comment_poster);
            category = (TextView) itemView.findViewById(R.id.parent_topic_category);
            postedTime = (TextView) itemView.findViewById(R.id.comment_posted_time);
        }
    }
}