package io.github.idoqo.radario.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import io.github.idoqo.radario.R;
import io.github.idoqo.radario.model.UserAction;
import io.github.idoqo.radario.url.RadarUrlParser;

public class UserReplyAdapter extends RecyclerView.Adapter<UserReplyAdapter.UserActionViewHolder> {

    private Context context;
    private ArrayList<UserAction> replies;

    public UserReplyAdapter(Context context, ArrayList<UserAction> replies){
        this.context = context;
        this.replies = replies;
    }

    public void setData(ArrayList<UserAction> data){
        replies = data;
        notifyDataSetChanged();
    }

    public void onBindViewHolder(UserActionViewHolder holder, int position){
        UserAction reply = replies.get(position);
        holder.parentTitle.setText(reply.getParentTopic());
        String excerpt = RadarUrlParser.userUrlToIntent(reply.getExcerpt());
        holder.replyExcerpt.setText(Html.fromHtml(excerpt));
    }

    public int getItemCount(){
        return (replies == null) ? 0 : replies.size();
    }

    public UserActionViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View rootView = inflater.inflate(R.layout.user_reply_item, parent, false);
        return new UserActionViewHolder(rootView);
    }

    static class UserActionViewHolder extends RecyclerView.ViewHolder
    {
        public TextView parentTitle, replyExcerpt, numberOfLikes;
        public ImageView likeButton;

        public UserActionViewHolder(View itemView){
            super(itemView);
            parentTitle = (TextView) itemView.findViewById(R.id.parent_topic_title);
            replyExcerpt = (TextView) itemView.findViewById(R.id.reply_excerpt);
            numberOfLikes = (TextView) itemView.findViewById(R.id.number_of_likes);
        }
    }
}
