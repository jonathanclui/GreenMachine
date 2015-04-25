package com.gogreen.greenmachine;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.HashMap;


public class SettingsActivity extends ActionBarActivity {

    // UI Components
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mHomeCity;
    private EditText mPhone;
    private Switch mDriving;
    private EditText mCar;
    private EditText mArriveHQBy;
    private EditText mArriveHomeBy;

    private GoogleMap mMap;

    private static PrivateProfile userProfile;
    private boolean isDriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        userProfile = (PrivateProfile) ParseUser.getCurrentUser().get("privateProfile");
        try {
            userProfile = userProfile.fetchIfNeeded();
        } catch (ParseException e) {
            return;
        }

        // Set up proper links to UI Components
        mFirstName = (EditText) findViewById(R.id.first_name_edit_text);
        mLastName = (EditText) findViewById(R.id.last_name_edit_text);
        mHomeCity = (EditText) findViewById(R.id.home_city_edit_text);
        mPhone = (EditText) findViewById(R.id.phone_edit_text);
        mDriving = (Switch) findViewById(R.id.driver_switch);
        mCar = (EditText) findViewById(R.id.driver_car_edit_text);
        mArriveHQBy = (EditText) findViewById(R.id.arrive_hq_by_edit_text);
        mArriveHomeBy = (EditText) findViewById(R.id.arrive_home_by_edit_text);

        // Set user data into fields
        mFirstName.setText(userProfile.getFirstName());
        mLastName.setText(userProfile.getLastName());
        mHomeCity.setText(userProfile.getHomeCity());
        mPhone.setText(userProfile.getPhoneNumber());
        mCar.setText(userProfile.getDriverCar());
        mArriveHQBy.setText(userProfile.getArriveHQBy());
        mArriveHomeBy.setText(userProfile.getArriveHomeBy());

        isDriver = userProfile.getDriverStatus();
        mDriving.setChecked(isDriver);

        mDriving.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    isDriver = true;
                } else {
                    // The toggle is disabled
                    isDriver = false;
                }
            }
        });

        Button submitButton = (Button) findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateProfile(userProfile);
            }
        });

        // Set up the log out button click handler
        Button logoutButton = (Button) findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Call the Parse log out method
                ParseUser.logOut();
                // Start and intent for the dispatch activity
                Intent intent = new Intent(SettingsActivity.this, DispatchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link com.google.android.gms.maps.SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            try {
                mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                        .getMap();
            } catch (NullPointerException e) {
                // Handle null pointer exception
            }
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(37.775, -122.4183333))      // Sets the center of the map
                .zoom(10)
                .build();                   // Creates a CameraPosition from the builder
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mMap.addMarker(new MarkerOptions().position(new LatLng(37.775, -122.4183333)).title("San Francisco"));
    }

    private void updateProfile(PrivateProfile p) {
        // Set up a progress dialog
        final ProgressDialog dialog = new ProgressDialog(SettingsActivity.this);
        dialog.setMessage(getString(R.string.progress_update_profile));
        dialog.show();

        // Prepare user inputs for validator
        HashMap<String, Object> fields = new HashMap<String, Object>();
        fields.put("firstName", mFirstName.getText().toString().trim());
        fields.put("lastName", mLastName.getText().toString().trim());
        fields.put("homeCity", mHomeCity.getText().toString().trim());
        fields.put("phone", mPhone.getText().toString().trim());
        fields.put("driving", isDriver);
        fields.put("car", mCar.getText().toString().trim());
        fields.put("arriveHQBy", mArriveHQBy.getText().toString().trim());
        fields.put("arriveHomeBy", mArriveHomeBy.getText().toString().trim());

        validateFields(fields);

        userProfile.setFirstName((String) fields.get("firstName"));
        userProfile.setLastName((String) fields.get("lastName"));
        userProfile.setHomeCity((String) fields.get("homeCity"));
        userProfile.setPhoneNumber((String) fields.get("phone"));
        userProfile.setDriverStatus((Boolean) fields.get("driving"));
        userProfile.setDriverCar((String) fields.get("car"));
        userProfile.setArriveHQBy((String) fields.get("arriveHQBy"));
        userProfile.setArriveHomeBy((String) fields.get("arriveHomeBy"));

        userProfile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                dialog.dismiss();
                if (e != null) {
                    // Show the error message
                    Toast.makeText(SettingsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    // show user the activity was complete
                    Toast.makeText(SettingsActivity.this, R.string.profile_saved, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Validate user inputs
    private void validateFields(HashMap<String, Object> fields) {
    }
}
