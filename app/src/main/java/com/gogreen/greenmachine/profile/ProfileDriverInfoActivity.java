package com.gogreen.greenmachine.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.gogreen.greenmachine.R;
import com.gogreen.greenmachine.parseobjects.PrivateProfile;
import com.parse.ParseUser;


public class ProfileDriverInfoActivity extends ActionBarActivity {

    // UI references.
    private EditText drivingEditText;
    private EditText carEditText;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_driver_info);

        // Set up the toolbar
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Set up Edit Texts
        drivingEditText = (EditText) findViewById(R.id.driving_edit_text);
        carEditText = (EditText) findViewById(R.id.car_edit_text);

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
        getMenuInflater().inflate(R.menu.menu_profile_driver_info, menu);
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
        String driving = drivingEditText.getText().toString().trim();
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
