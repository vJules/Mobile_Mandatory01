package org.projects.shoppinglist.settings;

/**
 * Created by Julian on 04-04-2016.
 */
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import org.projects.shoppinglist.R;

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager manager = getPreferenceManager();
        //The name chosen below is important - it should match
        //the name in the MainActivity
        manager.setSharedPreferencesName("my_prefs");
        //Adding the layout from the xml file
        addPreferencesFromResource(R.xml.prefs);
    }
}
