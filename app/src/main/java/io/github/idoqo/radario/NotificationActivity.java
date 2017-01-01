package io.github.idoqo.radario;

import android.app.DownloadManager;
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
import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import io.github.idoqo.radario.adapter.NotificationAdapter;
import io.github.idoqo.radario.helpers.ApiHelper;
import io.github.idoqo.radario.helpers.CurrentUserHelper;
import io.github.idoqo.radario.helpers.HttpRequestBuilderHelper;
import io.github.idoqo.radario.model.CurrentUser;
import io.github.idoqo.radario.model.Notification;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView notificationListView;
    private NotificationAdapter adapter;
    private OkHttpClient httpClient;
    private AVLoadingIndicatorView indicatorView;

    private SharedPreferences cachedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        notificationListView = (RecyclerView) findViewById(R.id.notification_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        notificationListView.setLayoutManager(layoutManager);
        indicatorView = (AVLoadingIndicatorView) findViewById(R.id.item_loading_indicator);

        adapter = new NotificationAdapter(this, new ArrayList<Notification>());
        notificationListView.setAdapter(adapter);

        cachedPref = getSharedPreferences(LoginActivity.COOKIE_FULL_STRING,
                MODE_PRIVATE);
        //reset the unread notifications count once the activity has been opened, if it isn't really read,
        //the server will resend the next time
        SharedPreferences.Editor editor = cachedPref.edit();
        editor.putInt(CurrentUserHelper.PREF_UNREAD_NOTIFICATION_COUNT, 0);
        editor.apply();

        initHttpClient();

        indicatorView.setVisibility(View.VISIBLE);
        NotificationFetcherTask fetcherTask = new NotificationFetcherTask();
        fetcherTask.execute();
    }

    private void initHttpClient() {
        SharedPreferences loginData = getSharedPreferences(LoginActivity.PREFERENCE_LOGIN_DATA, MODE_PRIVATE);
        final String savedCookies = loginData.getString(LoginActivity.COOKIE_FULL_STRING, null);
        httpClient = new OkHttpClient().newBuilder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        final Request original = chain.request();
                        String cookie = (savedCookies != null) ? savedCookies : "ddd";
                        final Request authorized = original.newBuilder()
                                .addHeader("Cookie", cookie)
                                .build();
                        return chain.proceed(authorized);
                    }
                }).build();
    }

    private class NotificationFetcherTask extends AsyncTask<Void, Void, ArrayList<Notification>>
    {
        private ArrayList<Notification> unreadNotifications = new ArrayList<>();

        public ArrayList<Notification> doInBackground(Void... params){
            String jsonString;
            ArrayList<Notification> notifications = new ArrayList<>();
            HttpUrl url = HttpRequestBuilderHelper.buildNotificationsUrl();
            try {
                jsonString = ApiHelper.GET(httpClient, url);
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
            indicatorView.setVisibility(View.GONE);
        }
    }
}
