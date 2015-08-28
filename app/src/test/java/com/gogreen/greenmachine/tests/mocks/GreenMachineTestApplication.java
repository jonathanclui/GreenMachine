package com.gogreen.greenmachine.tests.mocks;

import android.app.Application;

import com.gogreen.greenmachine.components.ApplicationComponent;
import com.gogreen.greenmachine.components.DaggerApplicationComponent;
import com.gogreen.greenmachine.modules.ApplicationModule;

/**
 * Created by jonathanlui on 8/26/15.
 */
public class GreenMachineTestApplication extends Application {

    private ApplicationComponent mComponent;

    public GreenMachineTestApplication() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public ApplicationComponent getComponent() {
        return mComponent;
    }
}
