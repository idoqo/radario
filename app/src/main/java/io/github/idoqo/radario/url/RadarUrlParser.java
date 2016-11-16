package io.github.idoqo.radario.url;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class RadarUrlParser {

    public static final String RADAR_BASE_URL = "http://www.radar.techcabal.com";
    public static final String RADAR_USER_MODEL_URL = "user";
    public static final String RADAR_TOPIC_MODEL_URL = "topic";
    public static final String RADAR_CATEGORY_MODEL_URL = "category";
    public static final String RADAR_MISC_MODEL_URL = "misc";

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
            if (href.startsWith("/user")) {
                html = html.replace(href, "radario://user/dsfs");
            }
        }
        return html;
    }

    public static String getModelFromUrl(String url){
        if (isRelativeUrl(url)) {
            if (url.startsWith("/user")) {
                return RADAR_USER_MODEL_URL;
            }
        }
        return RADAR_MISC_MODEL_URL;
    }
}
