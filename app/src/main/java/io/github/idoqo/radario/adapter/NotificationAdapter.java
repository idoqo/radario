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
import io.github.idoqo.radario.helpers.NotificationHelper;
import io.github.idoqo.radario.model.Notification;


public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private Context context;
    private ArrayList<Notification> notifications;

    public NotificationAdapter(Context c, ArrayList<Notification> items){
        context = c;
        notifications = items;
    }

    public int getItemCount(){
        return (notifications == null) ? 0 : notifications.size();
    }

    public void onBindViewHolder(NotificationViewHolder viewHolder, int position){
        Notification notification = notifications.get(position);
        NotificationHelper helper = new NotificationHelper(notification);
        String subject = helper.getSubject();
        String link = helper.getLink();
        String object = helper.getObject();
        int icon = helper.getTypeIcon();

        String text = context.getResources().getString(R.string.notification_details,
                subject, link, object);
        viewHolder.detailsView.setText(Html.fromHtml(text));
        viewHolder.typeIconView.setImageResource(icon);
    }

    public void reloadData(ArrayList<Notification> data) {
        this.notifications = data;
        notifyDataSetChanged();
    }

    public NotificationViewHolder onCreateViewHolder(ViewGroup container, int type){
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        View rootView = inflater.inflate(R.layout.notification_item, container, false);
        return  new NotificationViewHolder(rootView);
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder{
        public ImageView typeIconView;
        public TextView detailsView;
        public NotificationViewHolder(View itemView){
            super(itemView);
            typeIconView = (ImageView) itemView.findViewById(R.id.notification_type_icon);
            detailsView = (TextView) itemView.findViewById(R.id.notification_details);
        }
    }
}
