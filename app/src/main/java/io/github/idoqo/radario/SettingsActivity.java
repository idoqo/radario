package io.github.idoqo.radario;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import io.github.idoqo.radario.fragment.SettingsFragment;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
