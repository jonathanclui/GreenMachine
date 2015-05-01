package com.gogreen.greenmachine.main.match;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.gogreen.greenmachine.R;
import com.gogreen.greenmachine.parseobjects.Hotspot;
import com.gogreen.greenmachine.parseobjects.MatchRequest;
import com.gogreen.greenmachine.parseobjects.MatchRoute;
import com.gogreen.greenmachine.parseobjects.PublicProfile;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class DrivingActivity extends ActionBarActivity {
    private Spinner mCarSpinner;
    private Spinner mStartSpinner;
    private Spinner mDestSpinner;

    private Set<Hotspot> hotspots;
    private MatchRequest driverRequest;
    private MatchRoute matchRoute;

    private Toolbar toolbar;

    // UI references.
    private EditText matchByEditText;
    private EditText leaveByEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving);

        // Set up the toolbar
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mCarSpinner = (Spinner) findViewById(R.id.car_spinner);
        ArrayAdapter<CharSequence> carAdapter = ArrayAdapter.createFromResource(this,
                R.array.car_seats_array, android.R.layout.simple_spinner_item);
        carAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCarSpinner.setAdapter(carAdapter);

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

        matchByEditText = (EditText) findViewById(R.id.match_by_edit_text);
        leaveByEditText = (EditText) findViewById(R.id.leave_by_edit_text);

        matchByEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(DrivingActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String suffix = " AM";
                        if (selectedHour == 00){
                            selectedHour = 12;
                        }
                        else if (selectedHour > 12){
                            selectedHour = selectedHour - 12;
                            suffix = " PM";
                        }
                        String time = selectedHour + ":";
                        if (selectedMinute < 10){
                            time = time + "0" + selectedMinute;
                        }
                        else {
                            time = time + selectedMinute;
                        }
                        time = time + suffix;
                        matchByEditText.setText(time);
                    }
                }, hour, minute, false);//use AM/PM time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }

        });

        leaveByEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(DrivingActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String suffix = " AM";
                        if (selectedHour == 00){
                            selectedHour = 12;
                        }
                        else if (selectedHour > 12){
                            selectedHour = selectedHour - 12;
                            suffix = " PM";
                        }
                        String time = selectedHour + ":";
                        if (selectedMinute < 10){
                            time = time + "0" + selectedMinute;
                        }
                        else {
                            time = time + selectedMinute;
                        }
                        time = time + suffix;
                        leaveByEditText.setText(time);
                    }
                }, hour, minute, false);//use AM/PM time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }

        });

        // Set up the handler for the match button click
        Button matchButton = (Button) findViewById(R.id.driver_match_button);
        matchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new FindMatchTask().execute();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_driving, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    private void findMatch() {
        // 1) Create a match request
        createMatchRequest();

        // 2) Find riders and create route
        createRoute();
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
        driverRequest = new MatchRequest();
        driverRequest.populateMatchRequest(MatchRequest.RequestType.DRIVER, ParseUser.getCurrentUser(), hotspots,
                 matchBy, arriveBy, MatchRequest.MatchStatus.ACTIVE, 3);
        try {
            driverRequest.save();
        } catch (Exception e) {
            // Handle a save failure
            return;
        }
    }

    private void createRoute() {
        List<MatchRequest> matchRequests;
        boolean matched = false;

        ParseQuery<MatchRequest> matchQuery = ParseQuery.getQuery("MatchRequest");
        matchQuery.whereEqualTo("status", MatchRequest.MatchStatus.ACTIVE.toString());
        try {
            matchRequests = new ArrayList<MatchRequest>(matchQuery.find());
        } catch (ParseException e) {
            // Handle server retrieval failure
            return;
        }

        Iterator iter = matchRequests.iterator();
        while (iter.hasNext() && !matched) {
            MatchRequest request = (MatchRequest) iter.next();
            Set<Hotspot> other = request.getHotspots();
            Set<Hotspot> intersection = new HashSet<Hotspot>(this.hotspots);

            // Find the intersection
            intersection.retainAll(other);

            // If there are hot spots in common then match the two
            ParseGeoPoint hq = new ParseGeoPoint(37.5311942, -122.2646403);
            if (!intersection.isEmpty()) {
                ParseUser rider = request.getRequester();
                try {
                    rider.fetchIfNeeded();
                } catch (ParseException e) {
                    return;
                }
                // The rider can't also be the driver!
                if (!rider.equals(ParseUser.getCurrentUser())) {
                    // Create a new match route
                    this.matchRoute = new MatchRoute();
                    PublicProfile riderProfile = (PublicProfile) rider.get("publicProfile");
                    try {
                        riderProfile = riderProfile.fetchIfNeeded();
                    } catch (ParseException e) {
                        return;
                    }
                    matchRoute.populateMatchRoute(ParseUser.getCurrentUser(), riderProfile,
                            (Hotspot) intersection.iterator().next(),
                            hq, MatchRoute.TripStatus.NOT_STARTED, 3);
                    try {
                        matchRoute.save();
                        matched = true;
                    } catch (ParseException e) {
                        return;
                    }
                }
            }
        }
        // If not matched then we want to erase the match status
        if (!matched) {
            this.driverRequest.setStatus(MatchRequest.MatchStatus.INACTIVE);
            try {
                this.driverRequest.save();
            } catch (ParseException e) {
                return;
            }
        }
    }

    private void startNextActivity() {
        Intent intent = new Intent(DrivingActivity.this, DriverMatchedActivity.class);
        startActivity(intent);
    }

    private void processResult() {
        // 3) Show page with information
        if (this.matchRoute == null) {
            Toast.makeText(DrivingActivity.this, getString(R.string.progress_no_rider_found), Toast.LENGTH_SHORT).show();
        } else {
            startNextActivity();
        }
    }

    private class FindMatchTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog pdLoading = new ProgressDialog(DrivingActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setMessage(getString(R.string.progress_matching_driver));
            pdLoading.show();
        }
        @Override
        protected Void doInBackground(Void... params) {
            findMatch();
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
