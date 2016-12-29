package io.github.idoqo.radario;


import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.github.idoqo.radario.model.Comment;

public class Utils {

    public static String loadJsonFromAsset(Context context, String fname) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(fname);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            String tag = context.getClass().getName();
            Log.e(tag, ex.getMessage());
        }
        return json;
    }

    public static ArrayList<Comment> getCommentsAsThread(List<Comment> comments){
        //the resulting array of threaded comments
        ArrayList<Comment> threaded = new ArrayList<>();
        //array to hold processed comments which are removed at the end of the cycle
        List<Comment> removedComments = new ArrayList<>();

        //get top level comments first i.e comments without parents
        for (int i = 0;  i < comments.size(); i++) {
            Comment c = comments.get(i);
            if (c.getParentCommentId() == null) {
                c.setCommentDepth(0);
                c.setChildCount(0);
                threaded.add(c);
                removedComments.add(c);
            }
        }
        if (removedComments.size() > 0) {
            //clear processed comments which at this stage should solely be top-level comments
            comments.removeAll(removedComments);
            removedComments.clear();
        }

        int depth = 0;
        //get the children comments up to a depth of 10
        while (comments.size() > 0 && depth <= 10) {
            depth++;
            for (int j = 0; j < comments.size(); j++) {
                Comment child = comments.get(j);
                //check root comments for match
                for (int i = 0; i < threaded.size(); i++) {
                    Comment parent = threaded.get(i);
                    if (parent.getPostNumber() == child.getParentCommentId()) {
                        parent.setChildCount(parent.getChildCount() + 1);
                        child.setCommentDepth(depth+parent.getCommentDepth());
                        //add the child so it sits directly under its parent
                        threaded.add(i+parent.getChildCount(), child);
                        removedComments.add(child);
                        continue;
                    }
                }
            }
            if (removedComments.size() > 0) {
                comments.removeAll(removedComments);
                removedComments.clear();
            }
        }
        return threaded;
    }
}
