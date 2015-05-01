package com.gogreen.greenmachine.main.match;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.gogreen.greenmachine.R;
import com.gogreen.greenmachine.parseobjects.Hotspot;
import com.gogreen.greenmachine.parseobjects.MatchRoute;
import com.gogreen.greenmachine.parseobjects.PublicProfile;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class RiderMatchedActivity extends ActionBarActivity implements OnMapReadyCallback {

    private Toolbar toolbar;
    private GoogleMap mMap;
    private ParseGeoPoint driverLocation;
    private ParseGeoPoint hotspotLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_matched);

        // Set up the toolbar
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        getInfo();
        mapFragment.getMapAsync(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rider_matched, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    private void getInfo() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        List<MatchRoute> matchRoutes = new ArrayList<MatchRoute>();
        boolean foundRoute = false;

        // Query for all MatchRoutes
        ParseQuery<MatchRoute> matchRoutesQuery = ParseQuery.getQuery("MatchRoute");
        try {
            matchRoutes = new ArrayList<MatchRoute>(matchRoutesQuery.find());
        } catch (ParseException e) {
            // handle later since low on time
        }

        Iterator routeIterator = matchRoutes.iterator();
        while (routeIterator.hasNext() && !foundRoute) {
            MatchRoute route = (MatchRoute) routeIterator.next();
            try {
                route.fetchIfNeeded();
            } catch (ParseException e) {
                // handle later since low on time
            }

            ParseUser driver = route.getDriver();
            try {
                driver.fetchIfNeeded();
            } catch (ParseException e) {
                // handle later since low on time
            }

            PublicProfile driverProfile = (PublicProfile) driver.get("publicProfile");
            try {
                driverProfile.fetchIfNeeded();
            } catch (ParseException e) {
                // handle later if time
            }

            Hotspot hotspot = route.getHotspot();
            try {
                hotspot.fetchIfNeeded();
            } catch (ParseException e) {
                // handle later since low on time
            }

            this.hotspotLocation = hotspot.getParseGeoPoint();
            this.driverLocation = driverProfile.getLastKnownLocation();
            foundRoute = true;
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        double driverLat = this.driverLocation.getLatitude();
        double driverLong = this.driverLocation.getLongitude();

        double hotspotLat = this.hotspotLocation.getLatitude();
        double hotspotLong = this.hotspotLocation.getLongitude();

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(driverLat, driverLong))      // Sets the center of the map
                .zoom(10)
                .build();                   // Creates a CameraPosition from the builder
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        LatLng driverLoc = new LatLng(driverLat, driverLong);
        LatLng hotspotLoc = new LatLng(hotspotLat, hotspotLong);
        mMap.addMarker(new MarkerOptions().position(driverLoc)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car_black))
                .alpha(0.75f));
        mMap.addMarker(new MarkerOptions().position(hotspotLoc)
                .icon(BitmapDescriptorFactory.defaultMarker(30))
                .alpha(0.75f));
    }
}
