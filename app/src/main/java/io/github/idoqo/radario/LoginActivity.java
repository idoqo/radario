package io.github.idoqo.radario;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;

import io.github.idoqo.radario.helpers.CurrentUserHelper;
import io.github.idoqo.radario.model.CurrentUser;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    public static final String RADAR_LOGIN_URL = "http://radar.techcabal.com/login";
    public static final String COOKIE_FULL_STRING = "radar_cookie";
    public static final String PREFERENCE_LOGIN_DATA = "login";
    //the launching activity should pass a http client if it intends to dictate the client
    //this activity should use
    public static final String TAG = "LoginActivity";
    private SharedPreferences loginData;
    private OkHttpClient httpClient;

    private AVLoadingIndicatorView loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginData = getApplicationContext().getSharedPreferences(PREFERENCE_LOGIN_DATA,
                MODE_PRIVATE);
        loadingIndicator = (AVLoadingIndicatorView) findViewById(R.id.page_loading_indicator);

        Toast.makeText(this, "Please wait while the login screen loads", Toast.LENGTH_LONG).show();

        final WebView loginView  = (WebView) findViewById(R.id.login_web_view);
        WebSettings loginViewSettings = loginView.getSettings();
        loginViewSettings.setJavaScriptEnabled(true);
        //override web chrome client so we can show some progress
        /*loginView.setWebChromeClient(new WebChromeClient(){
            public void onProgressChanged(WebView webView, int progress){
                updateProgress(progress);
            }
        });*/
        //override the client so we can access the cookies
        loginView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //set a new web view client that will extract cookies from the
                //new url
                loginView.setWebViewClient(new CookeHandlerWebClient());
                loginView.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }

        });
        //loadingIndicator.setVisibility(View.VISIBLE);
        loginView.loadUrl(RADAR_LOGIN_URL);
    }

    private void updateProgress(int progress){
        if (progress >= 100){
            loadingIndicator.setVisibility(View.GONE);
        }
    }

    private class CookeHandlerWebClient extends WebViewClient{
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            final String cookies = android.webkit.CookieManager.getInstance().getCookie(url);
            //todo remove google analytics cookies from the above cookie string.
            saveLoginCookies(cookies);

            httpClient = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            final Request original = chain.request();
                            String cookie = (cookies != null) ? cookies : "";
                            final Request authorized = original.newBuilder()
                                    .addHeader("Cookie", cookie)
                                    .build();
                            return chain.proceed(authorized);
                        }
                    }).build();
            broadcastLoggedUser(url);
        }
    }

    private void broadcastLoggedUser(final String url){
        CurrentUserHelper userHelper = new CurrentUserHelper(httpClient, this);
        userHelper.requestLoggedUser(new CurrentUserHelper.LoggedUserInfoInterface() {
            @Override
            public void onPreExecute() {

            }

            @Override
            public void onPostExecute(CurrentUser result) {
                if (url.equals("http://radar.techcabal.com/") ||
                        url.equals("https://radar.techcabal.com/")) {
                    //do stuffs with the user you got
                    //finish the activity and go back to the launcher activity
                    Intent returnIntent = new Intent();
                    setResult(RESULT_OK, returnIntent);
                    //fini sh();
                }
            }
        });
    }

    private void saveLoginCookies(String cookieString){
        SharedPreferences.Editor editor = loginData.edit();
        editor.putString(COOKIE_FULL_STRING, cookieString);
        editor.apply();
    }

    //returns the value of the cookie with the specified name, null if no match was found
    public static String getCookie(String url, String cookieName){
        String cookieValue = null;
        String cookies = android.webkit.CookieManager.getInstance().getCookie(url);
        Log.i("LoginActivity", cookies);
        String[] tmp = cookies.split(";");
        for (String cookie : tmp) {
            String[] lol = cookie.split("=");
            //the value should be the second index after the split above
            if (lol[0].equals(cookieName)) {
                cookieValue = lol[1];
            }
        }
        return cookieValue;
    }
}
