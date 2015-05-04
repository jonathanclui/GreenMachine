package com.gogreen.greenmachine.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonRectangle;
import com.gogreen.greenmachine.R;
import com.gogreen.greenmachine.main.badges.BadgeActivity;
import com.gogreen.greenmachine.main.login.DispatchActivity;
import com.gogreen.greenmachine.main.match.DrivingActivity;
import com.gogreen.greenmachine.main.match.RidingActivity;
import com.gogreen.greenmachine.navigation.AboutUsActivity;
import com.gogreen.greenmachine.navigation.NavDrawerAdapter;
import com.gogreen.greenmachine.navigation.distmatrix.RetrieveDistanceMatrix;
import com.gogreen.greenmachine.navigation.SettingsActivity;
import com.gogreen.greenmachine.navigation.distmatrix.Element;
import com.gogreen.greenmachine.navigation.distmatrix.Result;
import com.gogreen.greenmachine.navigation.distmatrix.Row;
import com.gogreen.greenmachine.parseobjects.Hotspot;
import com.gogreen.greenmachine.parseobjects.MatchRoute;
import com.gogreen.greenmachine.parseobjects.PrivateProfile;
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
import com.google.api.client.http.GenericUrl;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class MainActivity extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener,OnMapReadyCallback,GoogleMap.OnMarkerClickListener {

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Location mCurrentLocation;
    private double mLatitude;
    private double mLongitude;
    private boolean mRequestingLocationUpdates = true;
    private LocationRequest mLocationRequest;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1004;
    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";
    protected final static String LOCATION_KEY = "location-key";
    protected String mLastUpdateTime;
    protected GoogleMap mMap;

    // Menu positions
    private final int HOME = 1;
    private final int BADGES = 2;
    private final int HOTSPOTS = 3;
    private final int ABOUT_US = 4;
    private final int LOGOUT = 5;

    // Nav drawer resources
    private String navDrawerName;
    private String navDrawerEmail;
    private int navDrawerProfileImage = R.drawable.jonathan_lui;
    private String[] navRowTitles;
    private TypedArray navRowIcons;

    private Toolbar toolbar;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DrawerLayout mDrawer;

    private ActionBarDrawerToggle mDrawerToggle;

    private Set<Hotspot> serverHotspots;

    public List<LatLng> simulatePoints=Arrays.asList(
        makeLatLng(37.6476749,-122.4066639),
        makeLatLng(37.641694,-122.405891),
        makeLatLng(37.6347100,-122.4067497),
        makeLatLng(37.634042,-122.4136162),
        makeLatLng(37.6338779,-122.4170494),
        makeLatLng(37.6330622,-122.4191952),
        makeLatLng(37.6307511,-122.4178219),
        makeLatLng(37.6292556,-122.4167061),
        makeLatLng(37.6270124,-122.415247),
        makeLatLng(37.6246331,-122.4138737),
        makeLatLng(37.6226617,-122.4121571),
        makeLatLng(37.6202823,-122.4104404),
        makeLatLng(37.6155912,-122.4064922),
        makeLatLng(37.615035, -122.405966),
        makeLatLng(37.614185, -122.405140),
        makeLatLng(37.614686, -122.405623),
        makeLatLng(37.613819, -122.404786),
        makeLatLng(37.609642, -122.400763)
    );

    public List<LatLng> simulatePoints_2=Arrays.asList(
        makeLatLng(37.475430,-122.221943),
        makeLatLng(37.477158,-122.221364),
        makeLatLng(37.476213,-122.220817),
        makeLatLng(37.476221,-122.221471),
        makeLatLng(37.477073,-122.222684),
        makeLatLng(37.478614,-122.224722),
        makeLatLng(37.480768,-122.227876),
        makeLatLng(37.482258,-122.229786),
        makeLatLng(37.483999,-122.231900),
        makeLatLng(37.485689, -122.234341),
        makeLatLng(37.484701, -122.233096),
        makeLatLng(37.485374, -122.234019),
        makeLatLng(37.486787, -122.235553),
        makeLatLng(37.487179, -122.236015),
        makeLatLng(37.487179, -122.236015),
        makeLatLng(37.487179, -122.236015),
        makeLatLng(37.487179, -122.236015)
    );

    public int simulateStep=0;
    Marker simulatedDriver;
    public int simulateStep_2=0;
    Marker simulatedDriver_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Grab server hotspots
        this.serverHotspots = getAllHotspots();

        // Grab appropriate data for adapter
        navRowTitles = getResources().getStringArray(R.array.navigation_drawer_titles);
        navRowIcons = getResources().obtainTypedArray(R.array.navigation_drawer_icons);

        // Set up the toolbar
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Set up recycler and provide it with the proper adapter
        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView);
        mRecyclerView.setHasFixedSize(true);

        ParseUser currUser = ParseUser.getCurrentUser();

        navDrawerEmail = currUser.getEmail();
        PrivateProfile privProfile = (PrivateProfile) currUser.get("privateProfile");
        try {
            privProfile.fetchIfNeeded();
        } catch (ParseException e) {
            navDrawerName = "Connor Horton";
        }

        navRowIcons = getResources().obtainTypedArray(R.array.navigation_drawer_icons);
        navDrawerName = privProfile.getFirstName() + " " + privProfile.getLastName();

        mAdapter = new NavDrawerAdapter(navRowTitles, navRowIcons, navDrawerName, navDrawerEmail, navDrawerProfileImage, this);
        mRecyclerView.setAdapter(mAdapter);

        final GestureDetector mGestureDetector = new GestureDetector(MainActivity.this,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override public boolean onSingleTapUp(MotionEvent e) {
                        return true;
                }
        });

        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if(child != null && mGestureDetector.onTouchEvent(motionEvent)){
                    int childPosition = recyclerView.getChildPosition(child);
                    mDrawer.closeDrawers();
                    switch(childPosition) {
                        case HOME:
                            return true;
                        case BADGES:
                            startActivity(new Intent(MainActivity.this, BadgeActivity.class));
                            return true;
                        case HOTSPOTS:
                            return true;
                        case ABOUT_US:
                            startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                            return true;
                        case LOGOUT:
                            logout();
                            return true;
                        default:
                            return false;
                    }
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
            }
        });

        // Set the proper layout
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Set the drawer
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawer,
                toolbar,
                R.string.open_drawer,
                R.string.close_drawer) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
                syncState();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
                syncState();
            }
        };
        mDrawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        // Set up the handler for the driving button click
        ButtonRectangle drivingButton = (ButtonRectangle) findViewById(R.id.driving_button);
        drivingButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DrivingActivity.class);
                startActivity(intent);
            }
        });

        // Set up the handler for the riding button click
        ButtonRectangle ridingButton = (ButtonRectangle) findViewById(R.id.riding_button);
        ridingButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RidingActivity.class);
                startActivity(intent);
            }
        });

        // Update values using data stored in the Bundle.
        updateValuesFromBundle(savedInstanceState);
        buildGoogleApiClient();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_user:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage(getString(R.string.progress_logout));
        dialog.show();

        ParseUser.logOutInBackground(new LogOutCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    dialog.dismiss();
                    startNextActivity();
                }
            }
        });
    }

    private void startNextActivity() {
        // Start and intent for the dispatch activity
        Intent intent = new Intent(MainActivity.this, DispatchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(MainActivity.class.getSimpleName(), "Connected to GoogleApiClient");
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
        Log.i(MainActivity.class.getSimpleName(), "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
        Toast.makeText(getApplicationContext(), "Connection Failed", Toast.LENGTH_SHORT).show();
    }
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        //mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateLocation();
        simulateDriverStep();
    }

    public void simulateDriverStep(){
        if (simulateStep==0){
            simulatedDriver = mMap.addMarker(new MarkerOptions().position(simulatePoints.get(0))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car_black))
                            .title("Jaden Smith")
                            .alpha(0.75f));

            simulatedDriver_2 = mMap.addMarker(new MarkerOptions().position(simulatePoints_2.get(0))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car_black))
                            .title("Will Smith")
                            .alpha(0.75f));

            simulateStep+=1;

        }
        else{
           if (simulateStep<simulatePoints.size()){
               simulatedDriver.setPosition(simulatePoints.get(simulateStep));

           }
           if (simulateStep<simulatePoints_2.size()){
                simulatedDriver_2.setPosition(simulatePoints_2.get(simulateStep));
            }

            simulateStep+=1;
            if (simulateStep>Math.min(simulatePoints.size()-1,simulatePoints_2.size()-1))
                simulateStep=Math.min(simulatePoints.size()-1,simulatePoints_2.size()-1);
        }
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
        Log.i(MainActivity.class.getSimpleName(), "Lat:" + mLatitude + " Lon:" + mLongitude);

        if (mCurrentLocation!=null){
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
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(500);
        mLocationRequest.setFastestInterval(100);
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
        //savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Updates fields based on data stored in the bundle.
     *
     * @param savedInstanceState The activity state saved in the Bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        Log.i(MainActivity.class.getSimpleName(), "Updating values from bundle");
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
                try{
                    h.fetchIfNeeded();
                }
                catch (ParseException e){
                    e.printStackTrace();
                };
                ParseGeoPoint parsePoint = h.getParseGeoPoint();
                LatLng hotspotLoc = new LatLng(parsePoint.getLatitude(), parsePoint.getLongitude());
                Marker m=mMap.addMarker(new MarkerOptions().position(hotspotLoc)
                                .icon(BitmapDescriptorFactory.defaultMarker(30))
                                .title("Next Pickup: N/A")
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
     /*if (m.getAlpha()==0.75f) {
        setMarker(m);
     }
     else{
        resetMarker(m);
     }
    */
     updateMarkerTags(m);
     return false;
    }


    private class updateMarkers extends AsyncTask<Hotspot, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setMessage("");
            pdLoading.show();
        }
        @Override
        protected String doInBackground(Hotspot... params) {
            // Loop through every 30 seconds and try to find a rider
            Hotspot h=params[0];
            String origins="";
            ArrayList<PublicProfile> drivers_pub_prof = getActiveDrivers(h);
            int size_i=drivers_pub_prof.size()-1;

            //get locations of all the drivers headed towards this hotspot
            for(PublicProfile p:drivers_pub_prof){
                ParseGeoPoint lkl=p.getLastKnownLocation();
                origins+=(Double.toString(lkl.getLatitude())+","+Double.toString(lkl.getLongitude()));
                if (size_i!=0) origins+="|";
            }
            return origins;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pdLoading.dismiss();
        }
    }

    public int getHotspotId(Marker m){
        Iterator iter = this.serverHotspots.iterator();
        int i=1;
        while (iter.hasNext()) {
            Hotspot h = (Hotspot) iter.next();
            try {
                h.fetchIfNeeded();
            } catch (ParseException e) {

            }
            ParseGeoPoint parsePoint = h.getParseGeoPoint();
            LatLng hotspotLoc = new LatLng(parsePoint.getLatitude(), parsePoint.getLongitude());
            if (m.getPosition().longitude==parsePoint.getLongitude() && m.getPosition().latitude==parsePoint.getLatitude()){
                return i;
            }
            i+=1;
        }
        return -1;
    }

    public Hotspot getMyHotspot(Marker m){
        Iterator iter = this.serverHotspots.iterator();
        int i=1;
        while (iter.hasNext()) {
            Hotspot h = (Hotspot) iter.next();
            ParseGeoPoint parsePoint = h.getParseGeoPoint();
            LatLng hotspotLoc = new LatLng(parsePoint.getLatitude(), parsePoint.getLongitude());
            if (m.getPosition().longitude==parsePoint.getLongitude() && m.getPosition().latitude==parsePoint.getLatitude()){
                return h;
            }
            i+=1;
        }
        return (Hotspot)null;
    }

    public void updateMarkerTags(Marker m){
        //Log.i(MainActivity.class.getSimpleName(),Integer.toString(getHotspotId(m)));
        //TODO:oncoming driver's hotspot should be at this marker
        Hotspot h = getMyHotspot(m);
        //Log.i(MainActivity.class.getSimpleName(),"myHotspot-"+h.getName());
        if (h==null){
            m.setTitle("Next Pickup: N/A");
            return;
        }
        else {
            //Log.i(MainActivity.class.getSimpleName(),"myHotspot-"+h.getName());
            String origins="";
            try {
                origins = new updateMarkers().execute(h).get();
            }
            catch (Exception e){
                Log.i(MainActivity.class.getSimpleName(),"updateMarks:"+e.getMessage());
                e.printStackTrace();
            }
            if (origins=="") {
                m.setTitle("Next Pickup: N/A");
                m.setSnippet("no drivers yet");
            }
            Log.i(MainActivity.class.getSimpleName(),"drivers@-"+origins);
            int minTime=24*60*60;  //pickupTime in seconds

            //pack the query for distance matrix
            String destinations=Double.toString(m.getPosition().latitude)+","+Double.toString(m.getPosition().longitude);
            String mode="driving";
            String language="US-EN";
            String key="AIzaSyCzhOo4mqXFIMa73xk5N-2A5mifzcpINfo";
            String urlString="https://maps.googleapis.com/maps/api/distancematrix/json";

            GenericUrl url = new GenericUrl(urlString);
            url.put("origins", origins);
            url.put("destinations", destinations);
            url.put("mode", mode);
            url.put("language", language);
            url.put("key", key);

            Log.i(MainActivity.class.getSimpleName(),url+" "+url);
            try {
                Result r = new RetrieveDistanceMatrix().execute(url).get();
                List<Row> rows = r.rows;
                for (Row row : rows) {
                    List<Element> elements = row.elements;
                    for (Element e : elements) {
                        Log.i(RetrieveDistanceMatrix.class.getSimpleName(), e.distance.text + " " + e.distance.value);
                        minTime=Math.min(minTime,e.duration.value);
                    }
                }

            }
            catch (Exception e){
                Log.i(MainActivity.class.getSimpleName(),e.getMessage());
                e.printStackTrace();
            }

            //if minTime has not been reduced, possibly no drivers on the hotspot
            if (minTime<1440){
                m.setTitle("Next Pickup: "+(minTime/60)+" min");
                m.setSnippet("Text @ 650-290-2120");
            }
            else {
                m.setTitle("Next Pickup: N/A");
                m.setSnippet("no drivers yet");
            }
        }
        boolean condition =(getHotspotId(m)==6);
        Log.i(MainActivity.class.getSimpleName(),"myHotspot"+getMyHotspot(m).getName());
        condition=getMyHotspot(m).getName().startsWith("Hills");
        boolean myHotspot=condition;
        if (myHotspot && simulateStep<simulatePoints.size()) {
            String origins=Double.toString(simulatePoints.get(simulateStep).latitude)+","+Double.toString(simulatePoints.get(simulateStep).longitude);
            String destinations=Double.toString(m.getPosition().latitude)+","+Double.toString(m.getPosition().longitude);
            String mode="driving";
            String language="US-EN";
            String key="AIzaSyCzhOo4mqXFIMa73xk5N-2A5mifzcpINfo";
            String urlString="https://maps.googleapis.com/maps/api/distancematrix/json";

            GenericUrl url = new GenericUrl(urlString);
            url.put("origins", origins);
            url.put("destinations", destinations);
            url.put("mode", mode);
            url.put("language", language);
            url.put("key", key);
            int timeValue=-1;
            Log.i(MainActivity.class.getSimpleName(),url+" "+url);
            try {
                Result r = new RetrieveDistanceMatrix().execute(url).get();
                List<Row> rows = r.rows;
                for (Row row : rows) {
                    List<Element> elements = row.elements;
                    for (Element e : elements) {
                        Log.i(RetrieveDistanceMatrix.class.getSimpleName(), e.distance.text + " " + e.distance.value);
                        timeValue=e.duration.value;
                    }
                }

            }
            catch (Exception e){
                Log.i(MainActivity.class.getSimpleName(),e.getMessage());
                e.printStackTrace();
            }

            if (timeValue!=-1) {
                m.setTitle("Next Pickup: "+(timeValue/60)+" min");
                m.setSnippet("Text @ 650-290-2120");
            }
            else {
                m.setTitle("Next Pickup: N/A");
                m.setSnippet("no drivers yet");
            }
        }
    }

    //get drivers headed towards this headspot
    public ArrayList<PublicProfile> getActiveDrivers(Hotspot h){
        ArrayList<MatchRoute> matchRoute;
        ArrayList<PublicProfile> drivers_pub_prof = new ArrayList<PublicProfile>();
        ParseQuery<MatchRoute> query = ParseQuery.getQuery("MatchRoute");
        //query = query.whereEqualTo("tripStatus", MatchRoute.TripStatus.NOT_STARTED.toString());
        HashMap<Hotspot, PublicProfile> hash = new HashMap<>();

        try {
            matchRoute = new ArrayList<MatchRoute>(query.find());
            for(MatchRoute r:matchRoute){
                ParseUser driver=r.getDriver();
                try {
                    driver.fetchIfNeeded();
                } catch (ParseException e) {

                }
                PublicProfile pub_prof= (PublicProfile) driver.get("publicProfile");
                try {
                    pub_prof.fetchIfNeeded();
                } catch (ParseException e) {
                    continue;
                }
                ParseGeoPoint lkl=pub_prof.getLastKnownLocation();
                Log.i(MainActivity.class.getSimpleName(),"getActiveDrivers:"+lkl);
                Hotspot g=r.getHotspot();
                if (g!=null) {
                    if (g.getObjectId() == h.getObjectId()) {
                        drivers_pub_prof.add(pub_prof);
                    }
                }
            }
            return drivers_pub_prof;

        } catch (ParseException e) {
            // Handle server retrieval failure
            Log.i(MainActivity.class.getSimpleName(),"activeDrivers:"+e.getMessage());
            e.printStackTrace();
            return (null);
        }
    }
    public void setMarker(Marker m){
        m.setIcon(BitmapDescriptorFactory.defaultMarker(150));
        m.setAlpha(1.0f);

    }

    public void resetMarker(Marker m){
        m.setAlpha(0.75f);
        m.setIcon(BitmapDescriptorFactory.defaultMarker(30));
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
    public LatLng makeLatLng(double a, double b){
        return new LatLng(a,b);
    }

    public static int computePoints(Marker m){
        String id=m.getTitle();

        LatLng l=m.getPosition();

        //grab the driver(s) headed to hotspot with above id in this time window
        //need a table of hotspotId|timeWindow|driverObj|numriders --> To fill this table, find time from driver's location to hotspotID(s)
        //---------------------------------------------------------> when a rider is matched, add numriders to the hotspot against the matched time window
        //(DateObj.getHours()*60 + minutes)/15

        // if no drivers in this window, print no drivers around

        //pickup time =  (driver's leave by time + time from driver's location to this hotspot) - (current_time)
        //double dist=find distance to destination from hotspot

        //return dist/number_riders

        return 1;
    }
}
