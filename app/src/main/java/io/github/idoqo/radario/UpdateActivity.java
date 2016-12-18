package io.github.idoqo.radario;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONObject;

import java.io.IOException;

import io.github.idoqo.radario.helpers.ApiHelper;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UpdateActivity extends AppCompatActivity {
    private AVLoadingIndicatorView loadingIndicatorView;
    public final static String REMOTE_APK_URL = "https://raw.githubusercontent.com/idoqo/radario-nightly/master/radario.apk";
    private View snackbarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadingIndicatorView = (AVLoadingIndicatorView) findViewById(R.id.page_loading_indicator);
        snackbarView = findViewById(R.id.snackbar_view);
        checkForUpdate();
    }

    private void checkForUpdate() {
        loadingIndicatorView.setVisibility(View.VISIBLE);
        FileDownloaderTask downloaderTask = new FileDownloaderTask();
        downloaderTask.execute();
    }

    private class FileDownloaderTask extends AsyncTask<Void, Void, Integer> {
        public Integer doInBackground(Void... params){
            String versionText = null;
            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            final Request original = chain.request();
                            final Request authorized = original.newBuilder()
                                    .build();
                            return chain.proceed(authorized);
                        }
                    }).build();
            HttpUrl remoteVersionUrl = new HttpUrl.Builder()
                    .scheme("https")
                    .host("raw.githubusercontent.com")
                    .addPathSegment("idoqo")
                    .addPathSegment("radario-nightly")
                    .addPathSegment("master")
                    .addPathSegment("version.txt")
                    .build();
            try {
                versionText = ApiHelper.GET(okHttpClient, remoteVersionUrl);
                JSONObject object = new JSONObject(versionText);
                int versionCode = object.getInt("versionCode");
                return versionCode;
            } catch (Exception ioe) {
                return 0;
            }
        }

        protected void onPostExecute(Integer result) {
            loadingIndicatorView.setVisibility(View.GONE);
                if (appIsOutdated(result)) {
                    Snackbar.make(snackbarView, "A new version is available", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Download", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent updater = new Intent(Intent.ACTION_VIEW, Uri.parse(REMOTE_APK_URL));
                                    startActivity(updater);
                                }
                            }).show();
                } else {
                    Snackbar.make(snackbarView, "App is up to date.", Snackbar.LENGTH_INDEFINITE).show();
                }
        }
    }

    private int getVersionCode(){
        PackageManager pm = getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException ex) {}
        return 0;
    }

    private boolean appIsOutdated(int remoteVersionCode) {
        int installedVersion = getVersionCode();
        return (remoteVersionCode > installedVersion);
    }
}
