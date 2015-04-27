package com.gogreen.greenmachine.parseobjects;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;

/**
 * Created by jonathanlui on 4/26/15.
 */
@ParseClassName("MatchRoute")
public class MatchRoute extends ParseObject {

    public ParseUser getDriver() {
        return getParseUser("driver");
    }

    public void setDriver(ParseUser value) {
        put("driver", value);
    }

    public ArrayList<PublicProfile> getRiders() {
        return (ArrayList<PublicProfile>) get("riders");
    }

    public void setRiders(ArrayList<PublicProfile> value) {
        put("riders", value);
    }

    public void addRider(PublicProfile rider) {
        ArrayList<PublicProfile> riders;
        try {
            riders = getRiders();
            if (riders == null) {
                riders = new ArrayList<PublicProfile>();
            }
        } catch (Exception e) {
            riders = new ArrayList<PublicProfile>();
        }
        riders.add(rider);
        put("riders", riders);
    }

    public Hotspot getHotspot() {
        return (Hotspot) get("hotspot");
    }

    public void setHotspot(Hotspot value) {
        put("hotspot", value);
    }

    public ParseGeoPoint getDestination() {
        return getParseGeoPoint("destination");
    }

    public void setDestination(ParseGeoPoint value) {
        put("destination", value);
    }

    public TripStatus getStatus() {
        return TripStatus.parse(getString("tripStatus"));
    }

    public void setStatus(TripStatus value) {
        put("tripStatus", value.toString());
    }

    public int getCapacity() {
        return getInt("capacity");
    }

    public void setCapacity(int value) {
        put("capacity", value);
    }

    public static ParseQuery<MatchRoute> getQuery() {
        return ParseQuery.getQuery(MatchRoute.class);
    }

    public void populateMatchRoute(ParseUser driver, PublicProfile rider, Hotspot hotspot,
                                     ParseGeoPoint destination, TripStatus status, int capacity) {
        setDriver(driver);
        addRider(rider);
        setHotspot(hotspot);
        setDestination(destination);
        setStatus(status);
        setCapacity(capacity);
    }

    public enum TripStatus {
        NOT_STARTED,
        EN_ROUTE_HOTSPOT,
        EN_ROUTE_DESTINATION,
        COMPLETED;

        private static TripStatus parse(String s) {
            if (s.equals("NOT_STARTED")) {
                return NOT_STARTED;
            } else if (s.equals("EN_ROUTE_HOTSPOT")) {
                return EN_ROUTE_HOTSPOT;
            } else if (s.equals("EN_ROUTE_DESTINATION")) {
                return EN_ROUTE_DESTINATION;
            } else {
                return COMPLETED;
            }
        }
    }
}
