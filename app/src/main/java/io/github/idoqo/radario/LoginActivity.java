package io.github.idoqo.radario;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class LoginActivity extends AppCompatActivity {

    public static final String RADAR_LOGIN_URL = "http://radar.techcabal.com/login";
    public static final String COOKIE_FULL_STRING = "radar_cookie";
    public static final String PREFERENCE_LOGIN_DATA = "login";
    public static final String TAG = "LoginActivity";
    private SharedPreferences loginData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginData = getApplicationContext().getSharedPreferences(PREFERENCE_LOGIN_DATA, MODE_PRIVATE);

        final WebView loginView  = (WebView) findViewById(R.id.login_web_view);
        WebSettings loginViewSettings = loginView.getSettings();
        loginViewSettings.setJavaScriptEnabled(true);
        //override the client so we can access the cookies
        loginView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //set a new web view client that will extract cookies from the
                //new url
                loginView.setWebViewClient(new CookeHandlerWebClient());
                loginView.loadUrl(url);
                loginView.setVisibility(View.INVISIBLE);
                return super.shouldOverrideUrlLoading(view, url);
            }

        });
        loginView.loadUrl(RADAR_LOGIN_URL);
    }

    private class CookeHandlerWebClient extends WebViewClient{
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            String cookies = android.webkit.CookieManager.getInstance().getCookie(url);
            //todo remove google analytics cookies from the above cookie string.
            saveLoginCookies(cookies);
        }
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
