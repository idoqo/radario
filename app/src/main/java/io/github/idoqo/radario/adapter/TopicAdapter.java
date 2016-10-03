package io.github.idoqo.radario.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.github.idoqo.radario.R;
import io.github.idoqo.radario.lib.EndlessScrollAdapter;
import io.github.idoqo.radario.model.Category;
import io.github.idoqo.radario.model.Topic;

public class TopicAdapter extends EndlessScrollAdapter
{
    private ArrayList<Topic> topics;

    public TopicAdapter(Context context){
        super(context);
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
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.topic_item, null);
        }
        TextView titleView = (TextView) convertView.findViewById(R.id.topic_title);
        TextView catView = (TextView)convertView.findViewById(R.id.topic_category);

        Topic tp = topics.get(position);
        titleView.setText(tp.getTitle());
        catView.setText(Category.getnameFromId(tp.getCategory()));

        return convertView;
    }

    public View getLoadingView(LayoutInflater inflater, ViewGroup parent){
        return inflater.inflate(R.layout.loading_progress, null);
    }
}