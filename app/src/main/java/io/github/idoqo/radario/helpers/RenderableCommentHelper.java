package io.github.idoqo.radario.helpers;


import android.net.Uri;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import io.github.idoqo.radario.model.Comment;

public class RenderableCommentHelper{
    private Comment comment;
    public RenderableCommentHelper(Comment comment) {
        this.comment = comment;
    }

    public String buildCommentText() {
        String originalText = comment.getCooked();
        Document doc = Jsoup.parse(originalText);
        Elements links = doc.select("a[href]");

        return originalText;
    }

    public static ArrayList<String> getBlockQuotes(String fullText) {
        Document doc = Jsoup.parse(fullText);
        ArrayList<String> quotes = new ArrayList<>();
        Elements blockquotes = doc.select("blockquote");
        if (blockquotes.isEmpty()) {
            return null;
        }
        for (Element quote : blockquotes) {
            quotes.add(quote.text());
        }
        return quotes;
    }
}
