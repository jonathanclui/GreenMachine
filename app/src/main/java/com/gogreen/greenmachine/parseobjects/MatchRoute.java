package com.gogreen.greenmachine.parseobjects;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;

/**
 * Created by jonathanlui on 4/26/15.
 */
@ParseClassName("MatchRoute")
public class MatchRoute extends ParseObject {

    public String getDriverName() {
        return getString("driverName");
    }

    public void setDriverName(String value) {
        put("driverName", value);
    }

    public String getDriverCar() {
        return getString("driverCar");
    }

    public void setDriverCar(String value) {
        put("driverCar", value);
    }

    public ArrayList<PrivateProfile> getRiders() {
        return (ArrayList<PrivateProfile>) get("riders");
    }

    public void setRiders(ArrayList<PrivateProfile> value) {
        put("riders", value);
    }

    public Hotspot getHotspot() {
        return (Hotspot) get("hotspot");
    }

    public void setHotspot(Hotspot value) {
        put("hotspot", value);
    }

    public ParseGeoPoint getDestination() {
        return (ParseGeoPoint) get("destination");
    }

    public void setDestination(ParseGeoPoint value) {
        put("destination", value);
    }

    public TripStatus getStatus() {
        return (TripStatus) get("tripStatus");
    }

    public void setStatus(TripStatus value) {
        put("tripStatus", value);
    }

    public static ParseQuery<MatchRoute> getQuery() {
        return ParseQuery.getQuery(MatchRoute.class);
    }

    private enum TripStatus {
        NOT_STARTED,
        EN_ROUTE_HOTSPOT,
        EN_ROUTE_DESTINATION,
        COMPLETED
    }
}
