package io.github.idoqo.radario.fragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.github.idoqo.radario.R;
import io.github.idoqo.radario.service.PullNotificationService;

public class SettingsFragment extends PreferenceFragment {


    public SettingsFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        handlePreferenceChange();
    }

    private void handlePreferenceChange(){
        if (getActivity() != null) {
            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.OnSharedPreferenceChangeListener listener =
                    new SharedPreferences.OnSharedPreferenceChangeListener() {
                        @Override
                        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                            String enableKey = getActivity().getResources().getString(R.string.key_enable_notification);
                            if (key.equals(enableKey)) {
                                boolean show = prefs.getBoolean(enableKey, false);
                                if (show) {
                                    Intent notificationService = new Intent(getActivity(), PullNotificationService.class);
                                    getActivity().startService(notificationService);
                                }
                            }
                        }
                    };


            prefs.registerOnSharedPreferenceChangeListener(listener);
        }
    }
}
