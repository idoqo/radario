package io.github.idoqo.radario.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.method.LinkMovementMethod;
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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import io.github.idoqo.radario.R;
import io.github.idoqo.radario.UserProfileActivity;
import io.github.idoqo.radario.helpers.DateTimeHelper;
import io.github.idoqo.radario.model.Comment;
import io.github.idoqo.radario.url.RadarUrlParser;

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
        TextView postedTimeView = (TextView) convertView.findViewById(R.id.comment_posted_time);
        ImageView collapsedIndicator = (ImageView) convertView.findViewById(R.id.show_comment_indicator);

        Comment comment = comments.get(position);
        LinearLayout indentView = (LinearLayout)convertView.findViewById(R.id.indent);

        setupIndent(indentView, comment.getCommentDepth());

        String parsedComments = RadarUrlParser.userUrlToIntent(comment.getCooked());
        commentOP.setText(comment.getUsername());
        commentTextView.setText(Html.fromHtml(Jsoup.clean(parsedComments, Whitelist.basic())));
        commentTextView.setMovementMethod(LinkMovementMethod.getInstance());
        String  timeCount;
        String timeQualifier;
        try {
            Date now = new Date();
            Date topicCreation = comment.getCreatedAtAsDate();
            String[] cau = DateTimeHelper.getCountAndUnit(topicCreation, now);
            timeCount = cau[0];
            timeQualifier = cau[1];
        } catch (ParseException pe) {
            timeCount = "long";
            timeQualifier = "long";
        }
        postedTimeView.setText(context.getResources().getString(R.string.relative_time_past,
                timeCount, timeQualifier));
        collapsedIndicator.setOnClickListener(commentCollapser());
        commentOP.setOnClickListener(onUsernameClickedListener(comment));

        return convertView;
    }

    private View.OnClickListener onUsernameClickedListener(final Comment comment){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = comment.getUsername();
                String fullName = comment.getPosterFullName();
                String avatarUrl = comment.getAvatarUrl();

                Intent profileIntent = new Intent(context, UserProfileActivity.class);
                profileIntent.putExtra(UserProfileActivity.EXTRA_USERNAME, username);
                profileIntent.putExtra(UserProfileActivity.EXTRA_FULLNAME, fullName);
                profileIntent.putExtra(UserProfileActivity.EXTRA_AVATAR_URL, avatarUrl);
                context.startActivity(profileIntent);
            }
        };
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
        //drop a space between the bar and the text
        indentView.setPadding(0,0,10,0);

        indentView.setVisibility(View.VISIBLE);
        indentBarView.setVisibility(View.VISIBLE);
    }
}
