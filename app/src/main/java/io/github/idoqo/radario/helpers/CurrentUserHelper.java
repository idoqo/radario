package io.github.idoqo.radario.helpers;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import io.github.idoqo.radario.LoginActivity;
import io.github.idoqo.radario.model.CurrentUser;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

public class CurrentUserHelper {

    public static final String PREF_USERNAME_FIELD = "username";
    public static final String PREF_FULL_NAME_FIELD = "full_name";
    public static final String PREF_USER_ID_FIELD = "user_id";
    public static final String PREF_AVATAR_URL_FIELD = "avatar_url";

    private SharedPreferences userData;


    //http client for making requests to the server, should be provide by caller clas
    private OkHttpClient okHttpClient;

    //an object of the currently logged in user, NOT RELIABLE and should be verified before
    //being taken serious
    private CurrentUser loggedUser;

    //used to miscellaneous operations that may require a context
    private Context context;

    public CurrentUserHelper(OkHttpClient client, Context c){
        okHttpClient = client;
        context = c;

        if (context != null) {
            Context ctx = context.getApplicationContext();
            userData = ctx.getSharedPreferences(LoginActivity.COOKIE_FULL_STRING,
                    MODE_PRIVATE);
        }
    }

    public CurrentUser getLoggedUser(){
        return loggedUser;
    }

    public void setLoggedUser(CurrentUser user){
        this.loggedUser = user;
    }

    private void registerUserToCache(CurrentUser user){
        SharedPreferences.Editor editor = userData.edit();
        editor.putString(PREF_USERNAME_FIELD, user.getUsername());
        editor.putInt(PREF_USER_ID_FIELD, user.getId());
        editor.putString(PREF_FULL_NAME_FIELD, user.getFullName());
        editor.putString(PREF_AVATAR_URL_FIELD, user.getAvatarUrlTemplate());
        editor.apply();
    }

    //the user returned by this method is gotten from a shared preference, which means
    //the server might reject it if for instance, the cookies has expired or something...
    public CurrentUser lazyLoadUser(){
        CurrentUser user = new CurrentUser();
        String username = userData.getString(PREF_USERNAME_FIELD, null);
        //i hope to God they didn't use an id of 0 for any snowflake account
        Integer userID = userData.getInt(PREF_USER_ID_FIELD, 0);
        String fullName = userData.getString(PREF_FULL_NAME_FIELD, null);
        String avatarUrl = userData.getString(PREF_AVATAR_URL_FIELD, null);
        user.setUsername(username);
        user.setFullName(fullName);
        user.setId(userID);
        user.setAvatarUrlTemplate(avatarUrl);

        return user;
    }

    //interface for the CurrentUserFetcherTask to expose its methods
    public interface LoggedUserInfoInterface
    {
        public void onPreExecute();
        //public CurrentUser doInBackground();
        public void onPostExecute(CurrentUser result);
    }

    public void requestLoggedUser(LoggedUserInfoInterface in){
        CurrentUserFetcherTask fetcherTask = new CurrentUserFetcherTask(in);
        fetcherTask.execute();
    }

    public class CurrentUserFetcherTask extends AsyncTask<Void, Void, CurrentUser>
    {
        LoggedUserInfoInterface infoInterface;

        public CurrentUserFetcherTask(LoggedUserInfoInterface in){
            infoInterface = in;
        }

        @Override
        protected CurrentUser doInBackground(Void... voids) {
            String userJsonString = null;
            CurrentUser currentUser = null;
            try {
                HttpUrl url = HttpRequestBuilderHelper.buildCurrentUserUrl();
                userJsonString = ApiHelper.GET(okHttpClient, url);
            } catch (IOException ioe) {
                userJsonString = null;
                Log.i(TAG, "doInBackground: "+ioe.getLocalizedMessage());
            }
            if (userJsonString != null) {
                if (userJsonString.isEmpty()) {
                    //an empty response from the server means no authenticated user is present.
                    currentUser = null;
                } else {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        JsonNode retval = mapper.readTree(userJsonString);
                        JsonNode currentUserPath = retval.path("current_user");
                        currentUser = mapper.readValue(currentUserPath.traverse(), CurrentUser.class);
                        registerUserToCache(currentUser);
                    } catch (IOException ioe) {
                        //something bad happened
                        currentUser = null;
                    }
                }
            }
            return currentUser;
        }

        @Override
        protected void onPostExecute(CurrentUser data) {
            super.onPostExecute(data);
            loggedUser = data;
            //expose the result data to be acted upon by an interface specified by the caller
            infoInterface.onPostExecute(data);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            infoInterface.onPreExecute();
        }
    }
}
