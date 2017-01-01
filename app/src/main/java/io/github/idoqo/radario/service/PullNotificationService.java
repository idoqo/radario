package io.github.idoqo.radario.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import io.github.idoqo.radario.LoginActivity;
import io.github.idoqo.radario.NotificationActivity;
import io.github.idoqo.radario.R;
import io.github.idoqo.radario.TopicListActivity;
import io.github.idoqo.radario.Utils;
import io.github.idoqo.radario.helpers.ApiHelper;
import io.github.idoqo.radario.helpers.HttpRequestBuilderHelper;
import io.github.idoqo.radario.helpers.NotificationHelper;
import io.github.idoqo.radario.model.Notification;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PullNotificationService extends Service {

    //todo make this minutes and make it configurable from settings.
    public static int NOTIFICATION_PULL_INTERVAL;

    private NotificationManager NM;
    private SharedPreferences sharedPreferences;
    private OkHttpClient httpClient;
    private String cookies;

    private static final int NOTIFICATION_ID = 7;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    public void onCreate(){
        Log.i("PullNotificationService", "executing onCreate()");
    }

    private void initHttpClient(){
        httpClient = new OkHttpClient().newBuilder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        final Request original = chain.request();
                        String cookie = (cookies != null) ? cookies : "ddd";
                        final Request authorized = original.newBuilder()
                                .addHeader("Cookie", cookie)
                                .build();
                        return chain.proceed(authorized);
                    }
                }).build();
    }

    public int onStartCommand(Intent intent, int flags, int startID){
        NM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        SharedPreferences loginData = getSharedPreferences(LoginActivity.PREFERENCE_LOGIN_DATA, MODE_PRIVATE);
        cookies = loginData.getString(LoginActivity.COOKIE_FULL_STRING, null);
        int interval = sharedPreferences.getInt(getResources()
                .getString(R.string.key_notification_interval),  2);
        //let the default interval be 20 seconds (2 * 10000 milli seconds)
        NOTIFICATION_PULL_INTERVAL = interval * 10000;

        //in case of a new login, refresh the notification feed
        SharedPreferences.OnSharedPreferenceChangeListener listener = new
                SharedPreferences.OnSharedPreferenceChangeListener() {
                    @Override
                    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                        if (key.equals(LoginActivity.COOKIE_FULL_STRING)) {
                            startService(new Intent(PullNotificationService.this, PullNotificationService.class));
                        }
                    }
                };
        loginData.registerOnSharedPreferenceChangeListener(listener);
        initHttpClient();
        Log.i("PullNotificationService", "executing onStartCommand()");
        fetchNotifications();
        return START_STICKY;
    }

    private class FetchNotificationTask extends AsyncTask<Void, Void, ArrayList<Notification>>{
        protected ArrayList<Notification> doInBackground(Void... params){
            String jsonString;
            ArrayList<Notification> unreadNotifications = new ArrayList<>();
            HttpUrl url = HttpRequestBuilderHelper.buildNotificationsUrl();
            try {
                //jsonString = Utils.loadJsonFromAsset(getApplicationContext(), "noti.json");
                jsonString = ApiHelper.GET(httpClient, url);
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
        boolean showNoti = sharedPreferences.getBoolean(getResources().getString(R.string.key_enable_notification), false);
        if (showNoti) {
            new FetchNotificationTask().execute();
        } else {
            stopSelf();
        }
    }

    private void displayNotifications(ArrayList<Notification> unread){
        String title;
        String content;
        if (!unread.isEmpty()) {
            //get the first item in the list...
            Notification first = unread.get(0);
            int count = unread.size();
            title = (count < 2) ? count + " new notification" : count + " new notifications";
            if (count < 2) {
                NotificationHelper helper = new NotificationHelper(first);
                String subject = helper.getSubject();
                String link = helper.getLink();
                String object = helper.getObject();
                content = subject+" "+link+" "+object;
            } else {
                content = first.getData().getUsername()+" and "+(count-1)+" others";
            }
            Log.i("NotificationActivity", title);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_status_bar)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setAutoCancel(true);
            Intent topicListIntent = new Intent(this, NotificationActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(TopicListActivity.class);
            stackBuilder.addNextIntent(topicListIntent);
            PendingIntent listPendingIntent = stackBuilder.getPendingIntent(0,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(listPendingIntent);
            NM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            android.app.Notification noti = builder.build();
            noti.flags = android.app.Notification.DEFAULT_LIGHTS;
            NM.notify(NOTIFICATION_ID, noti);
        }
    }
}
