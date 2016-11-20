package io.github.idoqo.radario;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    public static final String RADAR_LOGIN_URL = "http://radar.techcabal.com/login";
    public static final String COOKIE_ID_NAME = "_t";
    public static final String COOKIE_SESSION_NAME = "_forum_session";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
            RelativeLayout root = (RelativeLayout) findViewById(R.id.activity_login);
            TextView cookieView = new TextView(LoginActivity.this);
            cookieView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            String id = getCookie(url, COOKIE_ID_NAME);
            cookieView.setText(id);
            root.addView(cookieView);
            super.onPageFinished(view, url);
        }
    }

    public static String getCookie(String url, String cookieName){
        String cookieValue = null;
        String cookies = android.webkit.CookieManager.getInstance().getCookie(url);
        String[] tmp = cookies.split(";");
        for (String cookie : tmp) {
            String[] lol = cookie.split("=");
            //the value should be the second index after the split above
            cookieValue = lol[1];
        }
        return cookieValue;
    }
}
