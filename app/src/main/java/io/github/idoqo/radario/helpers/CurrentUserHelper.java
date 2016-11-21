package io.github.idoqo.radario.helpers;


import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import io.github.idoqo.radario.model.CurrentUser;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

import static android.content.ContentValues.TAG;

public class CurrentUserHelper {
    public static final String CURRENT_USER_URL = "http://radar.techcabal.com/session/current.json";

    //http client for making requests to the server, should be provide by caller clas
    private OkHttpClient okHttpClient;

    //an object of the currently logged in user, NOT RELIABLE and should be verified before
    //being taken serious
    private CurrentUser loggedUser;

    public CurrentUserHelper(OkHttpClient client){
        okHttpClient = client;
    }

    public CurrentUser getLoggedUser(){
        return loggedUser;
    }

    public void setLoggedUser(CurrentUser user){
        this.loggedUser = user;
    }

    //interface for the CurrentUserFetcherTask to expose its methods
    public interface LoggedUserInfoInterface
    {
        public void onPreExecute();
        //public CurrentUser doInBackground();
        public void onPostExecute(CurrentUser result);
    }

    public void verifyLoggedUser(LoggedUserInfoInterface in){
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
