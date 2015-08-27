package com.gogreen.greenmachine.main;

import com.gogreen.greenmachine.R;
import com.gogreen.greenmachine.components.ApplicationComponent;
import com.gogreen.greenmachine.components.DaggerApplicationComponent;
import com.gogreen.greenmachine.modules.ApplicationModule;
import com.gogreen.greenmachine.parseobjects.Hotspot;
import com.gogreen.greenmachine.parseobjects.HotspotsData;
import com.gogreen.greenmachine.parseobjects.MatchRequest;
import com.gogreen.greenmachine.parseobjects.MatchRoute;
import com.gogreen.greenmachine.parseobjects.PrivateProfile;
import com.gogreen.greenmachine.parseobjects.PublicProfile;
import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by jonathanlui on 4/19/15.
 */
public class GreenMachineApplication extends android.app.Application {
    // Debugging switch
    public static final boolean APPDEBUG = false;

    // Debugging tag for the application
    public static final String APPTAG = "greenmachine";

    // Used to pass location from MainActivity to DrivingActivity/RidingActivity
    public static final String INTENT_EXTRA_LOCATION = "location";

    public static String APP_ID = null;
    public static String CLIENT_KEY = null;

    private ApplicationComponent mComponent;

    public GreenMachineApplication() {
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
        ParseObject.registerSubclass(HotspotsData.class);
        Parse.initialize(this, APP_ID, CLIENT_KEY);

        mComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public ApplicationComponent getComponent() {
        return mComponent;
    }
}
