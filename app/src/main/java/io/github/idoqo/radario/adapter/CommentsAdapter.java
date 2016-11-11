package io.github.idoqo.radario.adapter;

import android.content.Context;
import android.media.Image;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.util.ArrayList;
import java.util.Collection;

import io.github.idoqo.radario.R;
import io.github.idoqo.radario.helpers.RenderableCommentHelper;
import io.github.idoqo.radario.lib.EndlessScrollAdapter;
import io.github.idoqo.radario.lib.EndlessScrollListener;
import io.github.idoqo.radario.model.Comment;


public class CommentsAdapter extends BaseAdapter {
    private ArrayList<Comment> comments;
    private LayoutInflater inflater;
    private Context context;

    public CommentsAdapter(Context context, ArrayList<Comment> comments){
        super();
        this.context = context;
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
        LinearLayout indentView = (LinearLayout)convertView.findViewById(R.id.indent);

        setupIndent(indentView, comment.getCommentDepth());

        String commentContent = comment.getCooked();

        commentOP.setText(comment.getUsername());
        commentTextView.setText(Html.fromHtml(Jsoup.clean(commentContent, Whitelist.basic())));

        convertView.setOnClickListener(commentCollapser());

        return convertView;
    }

    private View.OnClickListener commentCollapser(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View commentText = view.findViewById(R.id.comment_text);
                if (commentText != null) {
                    toggleCollapse(view, commentText);
                }
            }
        };
    }

    private void toggleCollapse(View parent, View viewToHide) {
        ImageView indicator = (ImageView) parent.findViewById(R.id.show_comment_indicator);

        if (viewToHide.getVisibility() == View.GONE) {
            viewToHide.setVisibility(View.VISIBLE);
            indicator.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_down));
        } else {
            viewToHide.setVisibility(View.GONE);
            indicator.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_right));
        }
    }

    private void setupIndent(View indentView, int depth) {
        LinearLayout indentBarView = (LinearLayout) indentView.findViewById(R.id.indent_bar);
        if (depth <= 0) {
            hideIndent(indentBarView, indentView);
        } else {
            positionIndentForDepth(indentBarView, indentView, depth);
        }
    }

    private void hideIndent(View indentBarView, View indentView) {
        indentView.getLayoutParams().width = 0;
        indentView.setVisibility(View.INVISIBLE);
        indentBarView.setVisibility(View.INVISIBLE);
    }

    private void positionIndentForDepth(View indentBarView, View indentView, int depth) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();

        //int indentSize = depth * INDENT_SIZE;
        int indentSize = depth * 5;
        int indentSizeScaledForDisplay = Math.round(
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, indentSize, metrics));
        indentView.getLayoutParams().width = indentSizeScaledForDisplay;

        int[] indentColors = context.getResources().getIntArray(R.array.commentColors);
        int indentColor = indentColors[depth % indentColors.length];
        indentBarView.setBackgroundColor(indentColor);

        indentView.setVisibility(View.VISIBLE);
        indentBarView.setVisibility(View.VISIBLE);
    }
}
