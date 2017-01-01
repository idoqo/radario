package io.github.idoqo.radario.helpers;


import org.apache.http.params.HttpParams;

import java.sql.Struct;
import java.util.ArrayList;

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

    public static HttpUrl buildNotificationsUrl(){
        //returns http://radar.techcabal.com/notifications.json
        return new HttpUrl.Builder()
                .scheme(RADAR_URL_SCHEME)
                .host(RADAR_URL_HOST)
                .addPathSegment("notifications.json")
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

    public static HttpUrl buildSearchUrl(String term){
        return new HttpUrl.Builder()
                .scheme(RADAR_URL_SCHEME)
                .host(RADAR_URL_HOST)
                .addPathSegment("search")
                .addPathSegment("query.json")
                //improve search term matching
                .addQueryParameter("include_blurbs", String.valueOf(true))
                .addQueryParameter("term", term)
                .build();
    }

    public static HttpUrl buildUserInfoUrl(String username){
        //returns "https://radar.techcabal.com/users/{username}.json"
        String filename = username+".json";
        return new HttpUrl.Builder()
                .scheme(RADAR_URL_SCHEME)
                .host(RADAR_URL_HOST)
                .addPathSegment("users")
                .addPathSegment(filename)
                .build();
    }

    public static HttpUrl buildUserAvatarUrl(String username, int size){
        //returns "https://radar.techcabal.com/user_avatar/radar.techcabal.com/{username}/{size}/201_1.png"
        return new HttpUrl.Builder()
                .scheme(RADAR_URL_SCHEME)
                .host(RADAR_URL_HOST)
                .addPathSegment("user_avatar")
                .addPathSegment("radar.techcabal.com")
                .addPathSegment(username)
                .addPathSegment(String.valueOf(size))
                //the filename is auto-selected by discourse as long as it ends in ".png"
                .addPathSegment("201_1.png")
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
        return new HttpUrl.Builder()
                .scheme(RADAR_URL_SCHEME)
                .host(RADAR_URL_HOST)
                .addPathSegment("t")
                .addPathSegment(String.valueOf(topicID))
                .addPathSegment("posts.json")
                .build();
    }

    /**
     * Builds a url in the form of
     "http://radar.techcabal.com/t/{topicId}/{post.json}?post_ids[]=stream[0]&post_ids[]=stream[1]
     */
    public static HttpUrl buildPostsUrl(int topicId, ArrayList<Integer> stream) {
        HttpUrl.Builder builder = new HttpUrl.Builder();
        builder.scheme(RADAR_URL_SCHEME)
                .host(RADAR_URL_HOST)
                .addPathSegment("t")
                .addPathSegment(String.valueOf(topicId))
                .addPathSegment("posts.json");
        String indieParam = "post_ids[]";
        for (int postId : stream) {
            builder.addQueryParameter(indieParam, String.valueOf(postId));
        }
        return builder.build();
    }
}
