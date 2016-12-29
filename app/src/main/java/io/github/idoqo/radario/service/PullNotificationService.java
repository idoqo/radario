package io.github.idoqo.radario.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import io.github.idoqo.radario.NotificationActivity;
import io.github.idoqo.radario.R;
import io.github.idoqo.radario.TopicListActivity;
import io.github.idoqo.radario.Utils;
import io.github.idoqo.radario.model.Notification;

public class PullNotificationService extends Service {
    //todo make this minutes and make it configurable from settings.
    public static final int NOTIFICATION_PULL_INTERVAL = 20000;

    private NotificationManager NM;
    private static final int NOTIFICATION_ID = 7;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    public void onCreate(){
        Log.i("PullNotificationService", "executing onCreate()");
    }

    public int onStartCommand(Intent intent, int flags, int startID){
        NM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Log.i("PullNotificationService", "executing onStartCommand()");
        while (true) {
            fetchNotifications();
            return START_STICKY;
        }
    }

    private class FetchNotificationTask extends AsyncTask<Void, Void, ArrayList<Notification>>{
        protected ArrayList<Notification> doInBackground(Void... params){
            String jsonString;
            ArrayList<Notification> unreadNotifications = new ArrayList<>();
            try {
                jsonString = Utils.loadJsonFromAsset(getApplicationContext(), "noti.json");
                ObjectMapper mapper = new ObjectMapper();
                JsonNode responseNode = mapper.readTree(jsonString);
                JsonNode notificationNode = responseNode.path("notifications");
                Iterator<JsonNode> notiIterator = notificationNode.elements();
                while (notiIterator.hasNext()) {
                    Notification notification = mapper.readValue(notiIterator.next().traverse(), Notification.class);
                    if (!notification.isRead()) {
                        unreadNotifications.add(notification);
                    }
                }
            } catch (IOException ioe) {
                Log.e("NotificationFetcherTask", "doInBackground: "+ioe.getMessage());
            }
            return unreadNotifications;
        }

        protected void onPostExecute(ArrayList<Notification> result){
            displayNotifications(result);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    fetchNotifications();
                }
            }, NOTIFICATION_PULL_INTERVAL);
        }
    }

    private void fetchNotifications(){
        new FetchNotificationTask().execute();
    }

    private void displayNotifications(ArrayList<Notification> unread){
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
                    .setSmallIcon(R.drawable.ic_status_bar)
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
            NM.notify(NOTIFICATION_ID, builder.build());
        }
    }
}
