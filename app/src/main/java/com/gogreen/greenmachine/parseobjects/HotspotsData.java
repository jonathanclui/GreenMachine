package com.gogreen.greenmachine.parseobjects;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by arbkhan on 4/30/2015.
 */
@ParseClassName("HotspotsData")
public class HotspotsData extends ParseObject {
    public int getHotspotId() {
        return getInt("HotspotId");
    }

    public void setHotspotId(int value) {
        put("HotspotId", value);
    }

    public int getnumRiders() {
        return getInt("numRiders");
    }

    public void setnumRiders(int value) {
        put("numRiders", value);
    }

    public int gettimeWindow() {
        return getInt("timeWindow");
    }

    public void settimeWindow(int value) {
        put("timeWindow", value);
    }

    public String getDriverObj() {
        return getString("driverObj");
    }

    public void setDriverObj(String value) {
        put("driverObj", value);
    }

    public static ParseQuery<HotspotsData> getQuery() {
        return ParseQuery.getQuery(HotspotsData.class);
    }

}
