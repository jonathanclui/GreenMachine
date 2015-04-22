package com.gogreen.greenmachine;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by jonathanlui on 4/21/15.
 */
@ParseClassName("Profile")
public class Profile extends ParseObject {

    public ParseUser getUser() {
        return getParseUser("user");
    }

    public void setUser(ParseUser value) {
        put("user", value);
    }

}
