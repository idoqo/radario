package io.github.idoqo.radario.url;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RadarUrlParser {

    public static final String RADAR_BASE_URL = "http://www.radar.techcabal.com";
    public static final String RADAR_USER_MODEL_URL = "user";
    public static final String RADAR_TOPIC_MODEL_URL = "topic";
    public static final String RADAR_CATEGORY_MODEL_URL = "category";
    public static final String RADAR_MISC_MODEL_URL = "misc";

    public static final String KEY_USERNAME_QUERY = "username";

    /*assumes that if the href starts with a slash instead of "http" or "www", it is relative
    to RADAR_BASE_URL*/
    public static boolean isRelativeUrl(String url){
        return url.startsWith("/");
    }

    //prepends a base to the url by simple concatenation
    public static String prependBaseToRelativeUrl(String base, String url){
        return base+url;
    }

    public static String prependRadarBaseToUrl(String url){
        return prependBaseToRelativeUrl(RADAR_BASE_URL, url);
    }

    public static String userUrlToIntent(String html){
        Document document = Jsoup.parse(html);
        Elements links = document.select("a[href]");
        for (Element link : links) {
            String href = link.attr("href");
            if (href.startsWith("/users")) {
                String username = getUsernameFromUrl(href);
                html = html.replace(href, "radario://user/?"+KEY_USERNAME_QUERY+"="+username);
            }
        }
        return html;
    }

    public static String getModelFromUrl(String url){
        if (isRelativeUrl(url)) {
            if (url.startsWith("/users")) {
                return RADAR_USER_MODEL_URL;
            }
        }
        return RADAR_MISC_MODEL_URL;
    }

    public static String getUsernameFromUrl(String url){
        //the username comes after the second slash i.e "/users/{username}/"
        String username = "";
        String[] tokens = url.split("/");
        if (tokens.length >= 2) {
            //0 is "users", 1 should be the username
            username = tokens[2];
        }
        return username;
    }
}
