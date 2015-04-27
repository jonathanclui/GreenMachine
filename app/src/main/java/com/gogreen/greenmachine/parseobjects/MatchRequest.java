package com.gogreen.greenmachine.parseobjects;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by jonathanlui on 4/26/15.
 */
@ParseClassName("MatchRequest")
public class MatchRequest extends ParseObject {

    public RequestType getRequestType() {
        return RequestType.parse(getString("type"));
    }

    public void setRequestType(RequestType value) {
        put("type", value.toString());
    }

    public ParseUser getRequester() {
        return getParseUser("user");
    }

    public void setRequester(ParseUser value) {
        put("user", value);
    }

    public Set<Hotspot> getHotspots() {
        return new HashSet<Hotspot>((ArrayList<Hotspot>) get("hotspots"));
    }

    public void setHotspots(Set<Hotspot> value) {
        put("hotspots", new ArrayList<Hotspot>(value));
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
        return MatchStatus.parse(getString("status"));
    }

    public void setStatus(MatchStatus value) {
        put("status", value.toString());
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

    public void populateMatchRequest(RequestType type, ParseUser user, Set<Hotspot> hotspots,
                                      Date matchBy, Date arriveBy, MatchStatus status, int seats) {
        setRequestType(type);
        setRequester(user);
        setHotspots(hotspots);
        setMatchByTime(matchBy);
        setArriveByTime(arriveBy);
        setStatus(status);
        setSeats(seats);
    }

    public enum MatchStatus {
        ACTIVE,
        INACTIVE;

        private static MatchStatus parse(String s) {
            if (s.equals("ACTIVE")) {
                return ACTIVE;
            } else {
                return INACTIVE;
            }
        }
    }

    public enum RequestType {
        DRIVER,
        RIDER;

        private static RequestType parse(String s) {
            if (s.equals("DRIVER")) {
                return DRIVER;
            } else {
                return RIDER;
            }
        }
    }
}
