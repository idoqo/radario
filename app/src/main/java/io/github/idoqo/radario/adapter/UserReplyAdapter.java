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
import io.github.idoqo.radario.model.UserReply;

public class UserReplyAdapter extends RecyclerView.Adapter<UserReplyAdapter.UserReplyViewHolder> {

    private Context context;
    private ArrayList<UserReply> replies;

    public UserReplyAdapter(Context context, ArrayList<UserReply> replies){
        this.context = context;
        this.replies = replies;
    }

    public void setData(ArrayList<UserReply> data){
        replies = data;
        notifyDataSetChanged();
    }

    public void onBindViewHolder(UserReplyViewHolder holder, int position){
        UserReply reply = replies.get(position);
        holder.parentTitle.setText(reply.getParentTopic());
        holder.replyExcerpt.setText(reply.getExcerpt());
    }

    public int getItemCount(){
        return (replies == null) ? 0 : replies.size();
    }

    public UserReplyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View rootView = inflater.inflate(R.layout.user_reply_item, parent, false);
        return new UserReplyViewHolder(rootView);
    }

    static class UserReplyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView parentTitle, replyExcerpt, numberOfLikes;
        public ImageView likeButton;

        public UserReplyViewHolder(View itemView){
            super(itemView);
            parentTitle = (TextView) itemView.findViewById(R.id.parent_topic_title);
            replyExcerpt = (TextView) itemView.findViewById(R.id.reply_excerpt);
            numberOfLikes = (TextView) itemView.findViewById(R.id.number_of_likes);
        }
    }
}
