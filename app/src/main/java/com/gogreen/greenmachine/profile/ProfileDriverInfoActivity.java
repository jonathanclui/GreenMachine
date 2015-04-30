package com.gogreen.greenmachine.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.CompoundButton;

import com.gogreen.greenmachine.R;
import com.gogreen.greenmachine.parseobjects.PrivateProfile;
import com.parse.ParseUser;


public class ProfileDriverInfoActivity extends ActionBarActivity {

    // UI references.
    private  Switch drivingSwitch;
    private EditText carEditText;
    private LinearLayout carReveal;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_driver_info);

        // Set up the toolbar
        /*toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);*/

        // Set up Edit Texts and Switch
        carReveal = (LinearLayout) findViewById(R.id.isdriving);
        drivingSwitch = (Switch) findViewById(R.id.driving_switch);
        drivingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (drivingSwitch.isChecked()){
                    carReveal.setVisibility(View.VISIBLE);}
                else{
                    carReveal.setVisibility(View.INVISIBLE);
                }
            }


        });
        carEditText = (EditText) findViewById(R.id.car_edit_text);


        // Set up the handler for the next button click
        ImageButton nextButton = (ImageButton) findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                submit();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile_driver_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    private void submit() {
        // Set up public profile with data
        ParseUser currentUser = ParseUser.getCurrentUser();
        String driving = drivingSwitch.getText().toString().trim();
        String car = carEditText.getText().toString().trim();

        savePrivateProfile(driving, car);

        Intent intent = new Intent(ProfileDriverInfoActivity.this, ProfileArriveByInfoActivity.class);
        startActivity(intent);
    }

    private void savePrivateProfile(String driving, String car) {
        PrivateProfile privProfile = (PrivateProfile) ParseUser.getCurrentUser().get("privateProfile");
        boolean driverStatus = false;
        if (driving.toLowerCase().equals("yes")) {
            driverStatus = true;
        }
        privProfile.setDriverStatus(driverStatus);
        privProfile.setDriverCar(car);
        privProfile.saveInBackground();
    }
}
