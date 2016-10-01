package io.github.idoqo.radario.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import io.github.idoqo.radario.R;
import io.github.idoqo.radario.model.Category;
import io.github.idoqo.radario.model.Topic;

public class TopicAdapter extends BaseAdapter {
    private Context context;
    private List<Topic> topics;
    private LayoutInflater inflater;

    public TopicAdapter(Context c, List<Topic> items){
        context = c;
        topics = items;
        inflater = LayoutInflater.from(c);
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null){
            view = inflater.inflate(R.layout.topic_item, viewGroup, false);
        }
        TextView titleTV = (TextView)view.findViewById(R.id.topic_title);
        TextView categoryTV = (TextView)view.findViewById(R.id.topic_category);
        Topic topic = topics.get(i);
        titleTV.setText(topic.getTitle());
        categoryTV.setText(Category.getnameFromId(topic.getCategory()));
        return view;
    }

    public long getItemId(int i) {
        return i;
    }

    public Object getItem(int i) {
        return topics.get(i);
    }

    public int getCount() {
        return topics.size();
    }
}
