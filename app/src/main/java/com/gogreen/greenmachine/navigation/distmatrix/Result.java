package com.gogreen.greenmachine.navigation.distmatrix;

import com.google.api.client.util.Key;

import java.util.List;

/**
 * Created by arbkhan on 4/30/2015.
 */
public class Result {
    @Key("destination_addresses")
    public List<String> destination_addresses;

    @Key("origin_addresses")
    public List<String> origin_addresses;

    @Key("rows")
    public List<Row> rows;
}
