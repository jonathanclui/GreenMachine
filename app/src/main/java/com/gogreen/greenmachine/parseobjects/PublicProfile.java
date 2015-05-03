package com.gogreen.greenmachine.parseobjects;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by jonathanlui on 4/21/15.
 */
@ParseClassName("PublicProfile")
public class PublicProfile extends ParseObject {

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

    public String getPhoneNumber() {
        return getString("phoneNumber");
    }

    public void setPhoneNumber(String value) {
        put("phoneNumber", value);
    }

    public ParseGeoPoint getLastKnownLocation() {
        return getParseGeoPoint("lastKnownLocation");
    }

    public void  setLastKnownLocation(ParseGeoPoint value) {
        put("lastKnownLocation", value);
    }

    public static ParseQuery<PublicProfile> getQuery() {
        return ParseQuery.getQuery(PublicProfile.class);
    }

    public void initializePublicProfile(ParseUser user, String firstName, String lastName, String phone) {
        setUser(user);
        setFirstName(firstName);
        setLastName(lastName);
        setPhoneNumber(phone);
    }

}
