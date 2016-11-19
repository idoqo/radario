package io.github.idoqo.radario;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.net.CookieManager;
import java.net.CookiePolicy;

public class LoginActivity extends AppCompatActivity {

    public static final String RADAR_LOGIN_URL = "http://radar.techcabal.com/login";
    public static final String RADAR_HOME_URL = "http://radar.techcabal.com";

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
                loginView.setWebViewClient(new CookeHandlerWebClient());
                loginView.loadUrl(url);

                return super.shouldOverrideUrlLoading(view, url);
            }

        });
        loginView.loadUrl(RADAR_LOGIN_URL);
    }

    private class CookeHandlerWebClient extends WebViewClient{
        @Override
        public void onPageFinished(WebView view, String url) {
            TextView cookieView = new TextView(LoginActivity.this);
            RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.activity_login);
            cookieView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT));

            String cookies = android.webkit.CookieManager.getInstance().getCookie(url);
            cookieView.setText(cookies);
            Toast.makeText(LoginActivity.this, cookies, Toast.LENGTH_LONG).show();
            mainLayout.addView(cookieView);
            super.onPageFinished(view, url);
        }
    }
}
