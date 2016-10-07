package io.github.idoqo.radario.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;

import io.github.idoqo.radario.R;
import io.github.idoqo.radario.lib.EndlessScrollAdapter;
import io.github.idoqo.radario.lib.EndlessScrollListener;
import io.github.idoqo.radario.model.Comment;


public class CommentsAdapter extends BaseAdapter {
    private ArrayList<Comment> comments;
    private LayoutInflater inflater;
    private Context context;

    public CommentsAdapter(Context context, ArrayList<Comment> comments){
        super();
        context = context;
        inflater = LayoutInflater.from(context);
        this.comments = comments;
    }

    public long getItemId(int position) {
        return position;
    }

    public Object getItem(int position) {
        return comments.get(position);
    }

    public int getCount() {
        return comments.size();
    }

    public View getView(int position, View convertView, ViewGroup parent){
        if (convertView == null){
            convertView = inflater.inflate(R.layout.comment_item, null);
        }
        TextView commentOP = (TextView) convertView.findViewById(R.id.comment_poster);
        TextView commentTextView = (TextView)convertView.findViewById(R.id.comment_text);

        Comment comment = comments.get(position);
        if (comment.getParentCommentId() != null){
            LinearLayout commentItemRoot = (LinearLayout) convertView.findViewById(R.id.comment_item_root);
            int indent = comment.getCommentDepth()*24;
            commentItemRoot.setPadding(indent, 0,0,0);
        }

        commentOP.setText(comment.getUsername()+"------"+comment.getCommentDepth());
        commentTextView.setText(comment.getCooked());

        return convertView;
    }
}
