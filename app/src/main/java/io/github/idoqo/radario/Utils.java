package io.github.idoqo.radario;


import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

public class Utils {

    public static String loadJsonFromAsset(Activity activity, String fname) {
        String json = null;
        try {
            InputStream is = activity.getAssets().open(fname);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            Log.e("MainActivity", ex.getMessage());
        }
        return json;
    }
}
