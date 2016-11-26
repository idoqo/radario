package io.github.idoqo.radario.helpers;


import org.apache.http.params.HttpParams;

import io.github.idoqo.radario.url.RadarUrlParser;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.RequestBody;

public class HttpRequestBuilderHelper {
    public static final String RADAR_URL_SCHEME = "http";
    //somehow, prepending "www" to the host gives a 404
    public static final String RADAR_URL_HOST = "radar.techcabal.com";
    //page GET parameter
    public static final String RADAR_URL_PAGE_QUERY = "page";

    //the filter values are based on how discourse filters them.
    public static final int USER_FILTER_LIKES = 1;
    public static final int USER_FILTER_REPLIES = 5;
    public static final int USER_FILTER_TOPICS = 4;

    public static HttpUrl buildHomePageUrl(){
        //returns http://radar.techcabal.com/latest.json?page=0;
        int initialPage = 0;
        return buildTopicUrlWithPage(initialPage);
    }

    public static HttpUrl buildUserUrl(String username, int filter){
        return new HttpUrl.Builder()
                .scheme(RADAR_URL_SCHEME)
                .host(RADAR_URL_HOST)
                .addPathSegment("user_actions.json")
                .addQueryParameter("username", username)
                .addQueryParameter("filter", String.valueOf(filter))
                .build();
    }

    public static HttpUrl buildTopicUrlWithPage(int page){
        return new HttpUrl.Builder()
                .scheme(RADAR_URL_SCHEME)
                .host(RADAR_URL_HOST)
                .addPathSegment("latest.json")
                .addQueryParameter(RADAR_URL_PAGE_QUERY, String.valueOf(page))
                .build();
    }

    public static HttpUrl buildCurrentUserUrl() {
        return new HttpUrl.Builder()
                .scheme(RADAR_URL_SCHEME)
                .host(RADAR_URL_HOST)
                .addPathSegment("session")
                .addPathSegment("current.json")
                .build();
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

    public static HttpUrl buildTopicCommentsUrl(int topicID){
        String jsonPath = topicID+".json";
        return new HttpUrl.Builder()
                .scheme(RADAR_URL_SCHEME)
                .host(RADAR_URL_HOST)
                .addPathSegment("t")
                .addPathSegment(jsonPath)
                .build();
    }
}
