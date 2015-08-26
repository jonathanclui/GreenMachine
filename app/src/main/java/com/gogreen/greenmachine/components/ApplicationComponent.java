package com.gogreen.greenmachine.components;

import com.gogreen.greenmachine.main.GreenMachineApplication;
import com.gogreen.greenmachine.main.MainActivity;
import com.gogreen.greenmachine.modules.ApplicationModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by jonathanlui on 8/26/15.
 */
@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {
    void inject(GreenMachineApplication app);
    void inject(MainActivity activity);
}
