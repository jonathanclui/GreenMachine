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
import android.widget.AdapterView;
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
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.gc.materialdesign.views.ButtonRectangle;


public class RidingActivity extends ActionBarActivity {
    private Spinner mStartSpinner;
    private Spinner mDestSpinner;

    private Set<Hotspot> hotspots;
    private MatchRequest riderRequest;
    private MatchRoute matchRoute;

    private Toolbar toolbar;

    // UI references.
    private EditText matchByEditText;
    private EditText leaveByEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riding);

        // Set up the toolbar
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mStartSpinner = (Spinner) findViewById(R.id.start_spinner);
        ArrayAdapter<CharSequence> startAdapter = ArrayAdapter.createFromResource(this,
                R.array.destinations_array, R.layout.spinner);
        startAdapter.setDropDownViewResource(R.layout.spinner);
        mStartSpinner.setAdapter(startAdapter);


        mDestSpinner = (Spinner) findViewById(R.id.destination_spinner);
        ArrayAdapter<CharSequence> destAdapter = ArrayAdapter.createFromResource(this,
                R.array.destinations_array, R.layout.spinner);
        destAdapter.setDropDownViewResource(R.layout.spinner);
        mDestSpinner.setAdapter(destAdapter);

        mStartSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(mStartSpinner.getSelectedItemPosition()==0){
                    mDestSpinner.setSelection(1);
                }else{
                    mDestSpinner.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        mDestSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(mDestSpinner.getSelectedItemPosition()==0){
                    mStartSpinner.setSelection(1);
                }else{
                    mStartSpinner.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        matchByEditText = (EditText) findViewById(R.id.match_by_edit_text);
        leaveByEditText = (EditText) findViewById(R.id.leave_by_edit_text);

        matchByEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(RidingActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String suffix = "AM";
                        if (selectedHour == 00){
                            selectedHour = 12;
                        }
                        else if (selectedHour > 12){
                            selectedHour = selectedHour - 12;
                            suffix = "PM";
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
                mTimePicker = new TimePickerDialog(RidingActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String suffix = "AM";
                        if (selectedHour == 00){
                            selectedHour = 12;
                        }
                        else if (selectedHour > 12){
                            selectedHour = selectedHour - 12;
                            suffix = "PM";
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
        ButtonRectangle matchButton = (ButtonRectangle) findViewById(R.id.rider_match_button);
        matchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new FindMatchTask().execute();
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
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    private void findMatch() {
        // 1) Create a match request
        createMatchRequest();

        // 2) Find riders and create route
        findRoute();
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

    private void processResult() {
        // 3) Show page with information
        if (this.matchRoute == null) {
            Toast.makeText(RidingActivity.this, getString(R.string.progress_no_driver_found), Toast.LENGTH_SHORT).show();
        } else {
            startNextActivity();
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
                    this.matchRoute = request;
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
        Intent intent = new Intent(RidingActivity.this, RiderMatchedActivity.class);
        startActivity(intent);
    }

    private class FindMatchTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog pdLoading = new ProgressDialog(RidingActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setMessage(getString(R.string.progress_matching_rider));
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
