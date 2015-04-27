package com.gogreen.greenmachine.main.match;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.gogreen.greenmachine.R;
import com.gogreen.greenmachine.parseobjects.Hotspot;
import com.gogreen.greenmachine.parseobjects.MatchRequest;
import com.gogreen.greenmachine.parseobjects.MatchRoute;
import com.gogreen.greenmachine.parseobjects.PublicProfile;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class RidingActivity extends ActionBarActivity {
    private Spinner mStartSpinner;
    private Spinner mDestSpinner;

    private Set<Hotspot> hotspots;
    private MatchRequest riderRequest;
    private MatchRoute matchRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riding);

        mStartSpinner = (Spinner) findViewById(R.id.start_spinner);
        ArrayAdapter<CharSequence> startAdapter = ArrayAdapter.createFromResource(this,
                R.array.destinations_array, android.R.layout.simple_spinner_item);
        startAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mStartSpinner.setAdapter(startAdapter);

        mDestSpinner = (Spinner) findViewById(R.id.destination_spinner);
        ArrayAdapter<CharSequence> destAdapter = ArrayAdapter.createFromResource(this,
                R.array.destinations_array, android.R.layout.simple_spinner_item);
        destAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDestSpinner.setAdapter(destAdapter);

        // Set up the handler for the match button click
        Button matchButton = (Button) findViewById(R.id.rider_match_button);
        matchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                findMatch();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_riding, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void findMatch() {
        // 1) Create a match request
        createMatchRequest();

        // 2) Find riders and create route
        findRoute();

        // 3) Show page with information
        if (this.matchRoute == null) {
            Toast.makeText(RidingActivity.this, getString(R.string.progress_no_driver_found), Toast.LENGTH_SHORT).show();
        } else {
            startNextActivity();
        }
    }

    private void createMatchRequest() {
        // Create a query to grab proper hotspots
        ParseQuery<Hotspot> hotspotQuery = ParseQuery.getQuery("Hotspot");
        hotspotQuery.orderByDescending("hotspotId");
        // Try to retrieve hotspot objects
        try {
            this.hotspots = new HashSet<Hotspot>(hotspotQuery.find());
        } catch (ParseException e) {
            // Handle a server query fail
            return;
        }

        // Fake Date data
        Date matchBy = new Date();
        Date arriveBy = new Date();

        // Create a match request
        riderRequest = new MatchRequest();
        riderRequest.populateMatchRequest(MatchRequest.RequestType.RIDER, ParseUser.getCurrentUser(), hotspots,
                matchBy, arriveBy, MatchRequest.MatchStatus.ACTIVE, 0);
        try {
            riderRequest.save();
        } catch (Exception e) {
            // Handle a save failure
            return;
        }
    }

    private void findRoute() {
        List<MatchRoute> matchRoutes;
        boolean matched = false;

        ParseQuery<MatchRoute> matchQuery = ParseQuery.getQuery("MatchRoute");
        matchQuery.whereEqualTo("tripStatus", MatchRoute.TripStatus.NOT_STARTED.toString());

        try {
            matchRoutes = new ArrayList<MatchRoute>(matchQuery.find());
        } catch (ParseException e) {
            // Handle server retrieval failure
            return;
        }

        Iterator iter = matchRoutes.iterator();
        while (iter.hasNext() && !matched) {
            MatchRoute request = (MatchRoute) iter.next();
            Set<Hotspot> other = new HashSet<Hotspot>();
            other.add(request.getHotspot());
            Set<Hotspot> intersection = new HashSet<Hotspot>(this.hotspots);

            // Find the intersection
            intersection.retainAll(other);

            // Match the rider to a driver going to the same hotspot and destination
            if (!intersection.isEmpty()) {
                ParseUser driver = request.getDriver();
                try {
                    driver.fetchIfNeeded();
                } catch (ParseException e) {
                    return;
                }
                ParseUser currentUser = ParseUser.getCurrentUser();
                // The driver can't also be the rider!
                if (!driver.equals(currentUser)) {
                    // Add the rider to the match route
                    PublicProfile pubProfile = (PublicProfile) currentUser.get("publicProfile");
                    try {
                        pubProfile.fetchIfNeeded();
                    } catch (ParseException e) {
                        return;
                    }
                    this.matchRoute = new MatchRoute();
                    this.matchRoute.addRider(pubProfile);
                    try {
                        this.matchRoute.save();
                        matched = true;
                    } catch (ParseException e) {
                        return;
                    }
                }
            }
        }
        // If not matched then we want to erase the match status
        if (!matched) {
            this.riderRequest.setStatus(MatchRequest.MatchStatus.INACTIVE);
            try {
                this.riderRequest.save();
            } catch (ParseException e) {
                return;
            }
        }
    }

    private void startNextActivity() {
        Intent intent = new Intent(RidingActivity.this, MatchingActivity.class);
        startActivity(intent);
    }
}
