package com.gogreen.greenmachine.parseobjects;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by jonathanlui on 4/26/15.
 */
@ParseClassName("MatchRoute")
public class MatchRoute extends ParseObject {

    public static ParseQuery<MatchRoute> getQuery() {
        return ParseQuery.getQuery(MatchRoute.class);
    }
}
