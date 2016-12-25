package io.github.idoqo.radario;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import io.github.idoqo.radario.adapter.NotificationAdapter;
import io.github.idoqo.radario.helpers.CurrentUserHelper;
import io.github.idoqo.radario.model.CurrentUser;
import io.github.idoqo.radario.model.Notification;

public class NotificationActivity extends AppCompatActivity {

    private static final int NOTIFICATON_ID = 5;
    private NotificationManager NM;
    private RecyclerView notificationListView;
    private NotificationAdapter adapter;

    private SharedPreferences cachedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        notificationListView = (RecyclerView) findViewById(R.id.notification_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        notificationListView.setLayoutManager(layoutManager);

        adapter = new NotificationAdapter(this, new ArrayList<Notification>());
        notificationListView.setAdapter(adapter);

        cachedPref = getSharedPreferences(LoginActivity.COOKIE_FULL_STRING,
                MODE_PRIVATE);
        //reset the unread notifications count once the activity has been opened, if it isn't really read,
        //the server will resend the next time
        SharedPreferences.Editor editor = cachedPref.edit();
        editor.putInt(CurrentUserHelper.PREF_UNREAD_NOTIFICATION_COUNT, 0);
        editor.apply();

        NotificationFetcherTask fetcherTask = new NotificationFetcherTask();
        fetcherTask.execute();
    }

    private class NotificationFetcherTask extends AsyncTask<Void, Void, ArrayList<Notification>>
    {
        private ArrayList<Notification> unreadNotifications = new ArrayList<>();

        public ArrayList<Notification> doInBackground(Void... params){
            String jsonString;
            ArrayList<Notification> notifications = new ArrayList<>();
            try {
                jsonString = Utils.loadJsonFromAsset(NotificationActivity.this, "noti.json");
                ObjectMapper mapper = new ObjectMapper();
                JsonNode responseNode = mapper.readTree(jsonString);
                JsonNode notificationNode = responseNode.path("notifications");
                Iterator<JsonNode> notiIterator = notificationNode.elements();
                while (notiIterator.hasNext()) {
                    Notification notification = mapper.readValue(notiIterator.next().traverse(), Notification.class);
                    notifications.add(notification);
                    if (!notification.isRead()) {
                        unreadNotifications.add(notification);
                    }
                }
            } catch (IOException ioe) {
                Log.e("NotificationFetcherTask", "doInBackground: "+ioe.getMessage());
            }
            return notifications;
        }

        protected void onPostExecute(ArrayList<Notification> result){
            //update notification list and show notifications
            adapter.reloadData(result);
            if (!unreadNotifications.isEmpty()) {
                showNotification(unreadNotifications);
            }
        }
    }

    private void showNotification(ArrayList<Notification> unread) {
        String title;
        String content;
        if (!unread.isEmpty()) {
            //get the first item in the list...
            Notification first = unread.get(0);
            int count = unread.size();
            title = (count < 2) ? "New notification from "+first.getData().getUsername()
                    : "Notifications from "+first.getData().getUsername()+" and "+(count-1)+" others";
            Log.i("NotificationActivity", title);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_sort_white)
                    .setContentTitle(title)
                    .setContentText("hello jupiter");
            Intent topicListIntent = new Intent(this, TopicListActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(TopicListActivity.class);
            stackBuilder.addNextIntent(topicListIntent);
            PendingIntent listPendingIntent = stackBuilder.getPendingIntent(0,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(listPendingIntent);
            NM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NM.notify(NOTIFICATON_ID, builder.build());
        }
    }
}
