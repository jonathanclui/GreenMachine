package com.gogreen.greenmachine.parseobjects;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by jonathanlui on 4/26/15.
 */
@ParseClassName("Hotspot")
public class Hotspot extends ParseObject {

    public String getDescription() {
        return getString("description");
    }

    public String getObject() {
        return getString("objectId");
    }

    public void setDescription(String value) {
        put("description", value);
    }

    public String getName() {
        return getString("name");
    }

    public void setName(String value) {
        put("name", value);
    }

    public int getHotspotId() {
        return getInt("hotspotId");
    }

    public void setHotspotId(int value) {
        put("hotspotId", value);
    }

    public ParseGeoPoint getParseGeoPoint() {
        return getParseGeoPoint("parseGeoPoint");
    }

    public void setParseGeoPoint(ParseGeoPoint value) {
        put("parseGeoPoint", value);
    }

    public static ParseQuery<Hotspot> getQuery() {
        return ParseQuery.getQuery(Hotspot.class);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Hotspot other = (Hotspot) obj;
        return other.getHotspotId() == this.getHotspotId();
    }

}
