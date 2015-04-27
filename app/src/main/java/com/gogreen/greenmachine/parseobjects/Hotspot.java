package com.gogreen.greenmachine.parseobjects;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;

/**
 * Created by jonathanlui on 4/26/15.
 */
@ParseClassName("Hotspot")
public class Hotspot extends ParseGeoPoint {

    public static ParseQuery<MatchRequest> getQuery() {
        return ParseQuery.getQuery(MatchRequest.class);
    }
}
