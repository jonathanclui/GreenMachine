package com.gogreen.greenmachine.parseobjects;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by jonathanlui on 4/26/15.
 */
@ParseClassName("MatchRequest")
public class MatchRequest extends ParseObject {

    public static ParseQuery<MatchRequest> getQuery() {
        return ParseQuery.getQuery(MatchRequest.class);
    }
}
