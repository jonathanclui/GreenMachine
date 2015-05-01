package com.gogreen.greenmachine.main.match;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonFloat;
import com.gogreen.greenmachine.R;
import com.gogreen.greenmachine.parseobjects.Hotspot;
import com.gogreen.greenmachine.parseobjects.MatchRequest;
import com.gogreen.greenmachine.parseobjects.MatchRoute;
import com.gogreen.greenmachine.parseobjects.PublicProfile;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class DrivingHotspotSelectActivity extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener {

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Location mCurrentLocation;
    double mLatitude;
    double mLongitude;
    private boolean mRequestingLocationUpdates = true;
    private LocationRequest mLocationRequest;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1004;
    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";
    protected final static String LOCATION_KEY = "location-key";
    protected String mLastUpdateTime;
    protected GoogleMap mMap;

    private Toolbar toolbar;

    private MatchRoute matchRoute;
    private Set<Hotspot> serverHotspots;
    private int currentCapacity;
    private Date matchByDate;
    private Date arriveByDate;
    private MatchRoute.Destination destination;
    private Set<Hotspot> selectedHotspots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving_hotspot_select);

        // Process intent items
        this.currentCapacity = (int) getIntent().getExtras().get("capacity");
        this.matchByDate = convertToDateObject(getIntent().getExtras().get("matchDate").toString());
        this.arriveByDate = convertToDateObject(getIntent().getExtras().get("arriveDate").toString());
        this.destination = processDestination(getIntent().getExtras().get("destination").toString());

        // Grab server hotspots
        this.serverHotspots = getAllHotspots();

        // Initialize selectedHotspots
        this.selectedHotspots = new HashSet<Hotspot>();

        // Set up the toolbar
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        ButtonFloat matchButton = (ButtonFloat) findViewById(R.id.buttonFloat);
        matchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Create a route and only execute it if it is succesfully created
                new FindMatchTask().execute();
            }
        });

        // Update values using data stored in the Bundle.
        updateValuesFromBundle(savedInstanceState);
        buildGoogleApiClient();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_driving_hotspot_select, menu);

        return super.onCreateOptionsMenu(menu);
    }

    private void startNextActivity() {
        Intent intent = new Intent(DrivingHotspotSelectActivity.this, DriverMatchedActivity.class);
        startActivity(intent);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(DrivingHotspotSelectActivity.class.getSimpleName(), "Connected to GoogleApiClient");
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            mLatitude = (mLastLocation.getLatitude());
            mLongitude = (mLastLocation.getLongitude());
        }
        else {
            Toast.makeText(getApplicationContext(),"Please turn on location.", Toast.LENGTH_SHORT).show();
        }
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
        //map can be loaded after the current location is known
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onConnectionSuspended(int code) {
        Toast.makeText(getApplicationContext(), "Connection Aborted.. Retrying", Toast.LENGTH_SHORT).show();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(DrivingHotspotSelectActivity.class.getSimpleName(), "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
        Toast.makeText(getApplicationContext(), "Connection Failed", Toast.LENGTH_SHORT).show();
    }
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        //mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateLocation();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }
    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    private void updateLocation() {
        Log.i(DrivingHotspotSelectActivity.class.getSimpleName(), "Lat:"+mLatitude+" Lon:" + mLongitude);

        mLatitude = mCurrentLocation.getLatitude();
        mLongitude = mCurrentLocation.getLongitude();

        // Fetch user's public profile
        ParseUser currentUser = ParseUser.getCurrentUser();
        PublicProfile pubProfile = (PublicProfile) currentUser.get("publicProfile");
        try {
            pubProfile.fetchIfNeeded();
        } catch (ParseException e) {
            return;
        }

        // Insert coordinates into the user's public profile lastKnownLocation
        ParseGeoPoint userLoc = new ParseGeoPoint(mLatitude, mLongitude);
        pubProfile.setLastKnownLocation(userLoc);
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,
                mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Updates fields based on data stored in the bundle.
     *
     * @param savedInstanceState The activity state saved in the Bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        Log.i(DrivingHotspotSelectActivity.class.getSimpleName(), "Updating values from bundle");
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        REQUESTING_LOCATION_UPDATES_KEY);
            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                mLastUpdateTime = savedInstanceState.getString(LAST_UPDATED_TIME_STRING_KEY);
            }
            updateLocation();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }
    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(mLatitude, mLongitude))      // Sets the center of the map
                .zoom(10)
                .build();                   // Creates a CameraPosition from the builder
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        if (this.serverHotspots != null) {
            Iterator iter = this.serverHotspots.iterator();
            while (iter.hasNext()) {
                Hotspot h = (Hotspot) iter.next();
                ParseGeoPoint parsePoint = h.getParseGeoPoint();
                LatLng hotspotLoc = new LatLng(parsePoint.getLatitude(), parsePoint.getLongitude());
                mMap.addMarker(new MarkerOptions().position(hotspotLoc)
                                .icon(BitmapDescriptorFactory.defaultMarker(30))
                                .title(h.getName())
                                .alpha(0.75f)
                );
                mMap.setOnMarkerClickListener((GoogleMap.OnMarkerClickListener)this);
            }
        } else {
            // Handle the server not getting hotspots
        }
    }

    @Override
    public boolean onMarkerClick(Marker m){
        if (m.getAlpha() == 0.75f) {
            setMarker(m);
        }
        else{
            resetMarker(m);
        }

        return true;
    }

    public void setMarker(Marker m){
        m.setIcon(BitmapDescriptorFactory.defaultMarker(150));
        m.setAlpha(1.0f);
        // Grab location & add to Hotspot set
        LatLng mPoint = m.getPosition();
        ParseGeoPoint hPoint = new ParseGeoPoint(mPoint.latitude, mPoint.longitude);

        // Find the original hotspot item and add it to the set
        Iterator iter = this.serverHotspots.iterator();
        while (iter.hasNext()) {
            Hotspot hSpot = (Hotspot) iter.next();
            try {
                hSpot.fetchIfNeeded();
            } catch (ParseException e) {
                return;
            }
            if (isEqualParseGeoPoint(hPoint, hSpot.getParseGeoPoint())) {
                this.selectedHotspots.add(hSpot);
                break;
            }
        }
    }

    private boolean isEqualParseGeoPoint(ParseGeoPoint p1, ParseGeoPoint p2) {
        return (p1.getLatitude() == p2.getLatitude() && p1.getLongitude() == p2.getLongitude());
    }

    public void resetMarker(Marker m){
        m.setAlpha(0.75f);
        m.setIcon(BitmapDescriptorFactory.defaultMarker(30));

        LatLng mPoint = m.getPosition();
        ParseGeoPoint hPoint = new ParseGeoPoint(mPoint.latitude, mPoint.longitude);

        // Find the original hotspot item and add it to the set
        Iterator iter = this.serverHotspots.iterator();
        while (iter.hasNext()) {
            Hotspot hSpot = (Hotspot) iter.next();
            try {
                hSpot.fetchIfNeeded();
            } catch (ParseException e) {
                return;
            }
            if (isEqualParseGeoPoint(hPoint, hSpot.getParseGeoPoint())) {
                this.selectedHotspots.remove(hSpot);
                break;
            }
        }
    }

    private Set<Hotspot> getAllHotspots() {
        Set<Hotspot> serverHotspots = new HashSet<Hotspot>();

        // Grab the hotspot set from the server
        ParseQuery<Hotspot> hotspotQuery = ParseQuery.getQuery("Hotspot");
        hotspotQuery.orderByDescending("hotspotId");
        try {
            serverHotspots = new HashSet<Hotspot>(hotspotQuery.find());
        } catch (ParseException e) {
            // Handle a server query fail
            return null;
        }

        return serverHotspots;
    }

    private boolean findRiders() {
        // MatchRoute should be created so now we scan the server for riders
        List<MatchRequest> matchRequests;
        boolean matched = false;

        ParseQuery<MatchRequest> matchQuery = ParseQuery.getQuery("MatchRequest");
        matchQuery = matchQuery.whereEqualTo("status", MatchRequest.MatchStatus.ACTIVE.toString());
        try {
            matchRequests = new ArrayList<MatchRequest>(matchQuery.find());
        } catch (ParseException e) {
            // Handle server retrieval failure
            return false;
        }

        Iterator iter = matchRequests.iterator();
        while (iter.hasNext() && !matched) {
            MatchRequest request = (MatchRequest) iter.next();
            Set<Hotspot> other = request.getHotspots();
            Set<Hotspot> intersection = new HashSet<Hotspot>(selectedHotspots);

            // Find the intersection
            intersection.retainAll(other);

            // If there are hot spots in common then match the two
            if (!intersection.isEmpty()) {
                ParseUser rider = request.getRequester();
                try {
                    rider.fetchIfNeeded();
                } catch (ParseException e) {
                    return false;
                }

                // The rider cannot also be the driver!
                if (!rider.equals(ParseUser.getCurrentUser())) {
                    // Update parameters for the route
                    PublicProfile riderProfile = (PublicProfile) rider.get("publicProfile");
                    try {
                        riderProfile = riderProfile.fetchIfNeeded();
                    } catch (ParseException e) {
                        return false;
                    }

                    Hotspot chosenHotspot = (Hotspot) intersection.iterator().next();

                    // Check if there is capacity in the car
                    int newCapacity = this.matchRoute.getCapacity() - 1;
                    if (newCapacity < 0) {
                        continue;
                    } else {
                        this.matchRoute.updateMatchRoute(chosenHotspot, riderProfile,
                                MatchRoute.TripStatus.EN_ROUTE_HOTSPOT, newCapacity);
                    }

                    // Save the match route with changes
                    try {
                        this.matchRoute.save();
                        matched = true;
                        return true;
                    } catch (ParseException e) {
                        matched = false;
                        return false;
                    }
                }
            }
        }
        return false;
    }

    private boolean createMatchRoute() {
        boolean createdRoute = false;
        List<MatchRequest> matchRequests;

        ParseQuery<MatchRequest> matchQuery = ParseQuery.getQuery("MatchRequest");
        matchQuery.whereEqualTo("status", MatchRequest.MatchStatus.ACTIVE.toString());
        try {
            matchRequests = new ArrayList<MatchRequest>(matchQuery.find());
        } catch (ParseException e) {
            // Handle server retrieval failure
            return false;
        }

        // Create a match route
        this.matchRoute = new MatchRoute();
        ArrayList<Hotspot> selectedHotspotsList = new ArrayList<Hotspot>(selectedHotspots);
        matchRoute.initializeMatchRoute(ParseUser.getCurrentUser(), selectedHotspotsList, destination,
                MatchRoute.TripStatus.NOT_STARTED, currentCapacity, matchByDate, arriveByDate);
        matchRoute.saveInBackground();

        return true;
    }

    private void processResult() {
        ArrayList<PublicProfile> riders = this.matchRoute.getRiders();
        if (riders == null) {
            Toast.makeText(DrivingHotspotSelectActivity.this, getString(R.string.progress_no_rider_found), Toast.LENGTH_SHORT).show();
        } else {
            startNextActivity();
        }
    }

    private Date convertToDateObject(String s) {
        SimpleDateFormat ft = new SimpleDateFormat ("h:m a");

        String input = s;

        Date t = new Date();

        try {
            t = ft.parse(input);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        return t;
    }

    private MatchRoute.Destination processDestination(String s) {
        if (s.equals("HQ")) {
            return MatchRoute.Destination.HQ;
        } else {
            return MatchRoute.Destination.HOME;
        }
    }

    private class FindMatchTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog pdLoading = new ProgressDialog(DrivingHotspotSelectActivity.this);
        Boolean routeCreated = false;
        Boolean riderFound = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setMessage(getString(R.string.progress_matching_driver));
            pdLoading.show();
        }
        @Override
        protected Void doInBackground(Void... params) {
            // Loop through every 30 seconds and try to find a rider
            for (int i = 0; i < 5; i++) {
                if (!routeCreated) {
                    routeCreated = createMatchRoute();
                } else if (!riderFound) {
                    riderFound = findRiders();
                } else if (riderFound) {
                    break;
                }
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {

                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pdLoading.dismiss();
            processResult();
        }
    }
}
