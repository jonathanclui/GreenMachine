package com.gogreen.greenmachine.navigation.distmatrix;

import com.google.api.client.util.Key;

/**
 * Created by arbkhan on 4/30/2015.
 */
public class Element {
    @Key("distance")
    public Distance distance;

    @Key("duration")
    public Duration duration;
}