package com.gogreen.greenmachine.main;

import android.content.Context;
import android.content.SharedPreferences;

import com.gogreen.greenmachine.R;
import com.gogreen.greenmachine.helpers.ConfigHelper;
import com.gogreen.greenmachine.parseobjects.Hotspot;
import com.gogreen.greenmachine.parseobjects.MatchRequest;
import com.gogreen.greenmachine.parseobjects.MatchRoute;
import com.gogreen.greenmachine.parseobjects.PrivateProfile;
import com.gogreen.greenmachine.parseobjects.PublicProfile;
import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by jonathanlui on 4/19/15.
 */
public class Application extends android.app.Application {
    // Debugging switch
    public static final boolean APPDEBUG = false;

    // Debugging tag for the application
    public static final String APPTAG = "greenmachine";

    // Used to pass location from MainActivity to DrivingActivity/RidingActivity
    public static final String INTENT_EXTRA_LOCATION = "location";

    public static String APP_ID = null;
    public static String CLIENT_KEY = null;

    private static SharedPreferences preferences;

    private static ConfigHelper configHelper;

    public Application() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore
        APP_ID = getString(R.string.parse_app_id);
        CLIENT_KEY = getString(R.string.parse_client_key);

        Parse.enableLocalDatastore(this);
        ParseObject.registerSubclass(PublicProfile.class);
        ParseObject.registerSubclass(PrivateProfile.class);
        ParseObject.registerSubclass(MatchRequest.class);
        ParseObject.registerSubclass(MatchRoute.class);
        ParseObject.registerSubclass(Hotspot.class);
        Parse.initialize(this, APP_ID, CLIENT_KEY);

        preferences = getSharedPreferences("com.gogreen.greenmachine", Context.MODE_PRIVATE);

        configHelper = new ConfigHelper();
        configHelper.fetchConfigIfNeeded();
    }

    public static ConfigHelper getConfigHelper() {
        return configHelper;
    }
}
