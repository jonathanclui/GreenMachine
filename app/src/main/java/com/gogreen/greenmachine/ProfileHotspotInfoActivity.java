package com.gogreen.greenmachine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import java.util.ArrayList;


public class ProfileHotspotInfoActivity extends ActionBarActivity {

    // UI references.
    private EditText hotspotLatEditText;
    private EditText hotspotLongEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_hotspot_info);

        hotspotLatEditText = (EditText) findViewById(R.id.hotspot_lat_edit_text);
        hotspotLongEditText = (EditText) findViewById(R.id.hotspot_long_edit_text);

        // Set up the handler for the next button click
        Button nextButton = (Button) findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                submit();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile_hotspot_info, menu);
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

    private void submit() {
        // Set up public profile with data
        ParseUser currentUser = ParseUser.getCurrentUser();
        Double hotspotLat = Double.parseDouble(hotspotLatEditText.getText().toString().trim());
        Double hotspotLong = Double.parseDouble(hotspotLongEditText.getText().toString().trim());

        savePrivateProfile(hotspotLat, hotspotLong);
        setUserProfileComplete(currentUser);

        Intent intent = new Intent(ProfileHotspotInfoActivity.this, DispatchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void savePrivateProfile(Double hotspotLat, Double hotspotLong) {
        PrivateProfile privProfile = (PrivateProfile) ParseUser.getCurrentUser().get("privateProfile");

        ArrayList<ParseGeoPoint> hotspots = new ArrayList<ParseGeoPoint>();
        hotspots.add(new ParseGeoPoint(hotspotLat, hotspotLong));

        privProfile.setPreferredHotspots(hotspots);
        privProfile.saveInBackground();
    }

    private void setUserProfileComplete(ParseUser user) {
        user.put("profileComplete", true);
        user.saveInBackground();
    }
}
