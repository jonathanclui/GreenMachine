package com.gogreen.greenmachine;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;

/**
 * Created by jonathanlui on 4/23/15.
 */
@ParseClassName("PrivateProfile")
public class PrivateProfile extends ParseObject {
    public ParseUser getUser() {
        return getParseUser("user");
    }

    public void setUser(ParseUser value) {
        put("user", value);
    }

    public String getFirstName() {
        return getString("firstName");
    }

    public void setFirstName(String value) {
        put("firstName", value);
    }

    public String getLastName() {
        return getString("lastName");
    }

    public void setLastName(String value) {
        put("lastName", value);
    }

    public String getHomeCity() {
        return getString("homeCity");
    }

    public void setHomeCity(String value) {
        put("homeCity", value);
    }

    public String getPhoneNumber() {
        return getString("phoneNumber");
    }

    public void setPhoneNumber(String value) {
        put("phoneNumber", value);
    }

    public Boolean getDriverStatus() {
        return getBoolean("isDriver");
    }

    public void setDriverStatus(Boolean value) {
        put("isDriver", value);
    }

    public String getDriverCar() {
        return getString("driverCar");
    }

    public void setDriverCar(String value) {
        put("driverCar", value);
    }

    public String getArriveBy() {
        return getString("arriveBy");
    }

    public void setArriveBy(String value) {
        put("arriveBy", value);
    }

    public ArrayList<ParseGeoPoint> getPreferredHotspots() {
        return (ArrayList<ParseGeoPoint>) get("preferredHotspots");
    }

    public void setPreferredHotspots(ArrayList<ParseGeoPoint> value) {
        put("preferredHotspots", value);
    }

    public static ParseQuery<PrivateProfile> getQuery() {
        return ParseQuery.getQuery(PrivateProfile.class);
    }
}
