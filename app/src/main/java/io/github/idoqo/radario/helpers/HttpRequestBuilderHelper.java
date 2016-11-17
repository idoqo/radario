package io.github.idoqo.radario.helpers;


import io.github.idoqo.radario.url.RadarUrlParser;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.RequestBody;

public class HttpRequestBuilderHelper {
    public static HttpUrl buildHomePageUrl(){
        return new HttpUrl.Builder()
                .scheme("http")
                .host("radar.techcabal.com")
                .addPathSegment("latest.json")
                .build();
        //returns http://www.radar.techcabal.com/latest.json
    }

    //Login request body
    public static RequestBody LoginBody(String username, String password, String token) {
        return new FormBody.Builder()
                .add("action", "login")
                .add("format", "json")
                .add("username", username)
                .add("password", password)
                .add("logintoken", token)
                .build();
    }

    public static HttpUrl buildURL() {
        return new HttpUrl.Builder()
                .scheme("https") //http
                .host("www.somehostname.com")
                .addPathSegment("pathSegment")//adds "/pathSegment" at the end of hostname
                .addQueryParameter("param1", "value1") //add query parameters to the URL
                .addEncodedQueryParameter("encodedName", "encodedValue")//add encoded query parameters to the URL
                .build();
        /*
         * The return URL:
         *  https://www.somehostname.com/pathSegment?param1=value1&encodedName=encodedValue
         */
    }
}
