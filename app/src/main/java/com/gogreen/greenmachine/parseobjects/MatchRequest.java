package com.gogreen.greenmachine.parseobjects;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by jonathanlui on 4/26/15.
 */
@ParseClassName("MatchRequest")
public class MatchRequest extends ParseObject {

    public RequestType getRequestType() {
        return (RequestType) get("type");
    }

    public void setRequestType(RequestType value) {
        put("type", value);
    }

    public ParseUser getRequester() {
        return getParseUser("user");
    }

    public void setRequester(ParseUser value) {
        put("user", value);
    }

    public ArrayList<Hotspot> getHotspots() {
        return (ArrayList<Hotspot>) get("hotspots");
    }

    public void setHotspots(ArrayList<Hotspot> value) {
        put("hotspots", value);
    }

    public Date getMatchByTime() {
        return getDate("matchByTime");
    }

    public void setMatchByTime(Date value) {
        put("matchByTime", value);
    }

    public Date getArriveByTime() {
        return getDate("arriveByTime");
    }

    public void setArriveByTime(Date value) {
        put("arriveByTime", value);
    }

    public MatchStatus getStatus() {
        return (MatchStatus) get("status");
    }

    public void setStatus(MatchStatus value) {
        put("status", value);
    }

    public int getSeats() {
        return getInt("seats");
    }

    public void setSeats(int value) {
        put("seats", value);
    }

    public static ParseQuery<MatchRequest> getQuery() {
        return ParseQuery.getQuery(MatchRequest.class);
    }

    private enum MatchStatus {
        ACTIVE,
        INACTIVE
    }

    private enum RequestType {
        DRIVER,
        RIDER
    }
}
