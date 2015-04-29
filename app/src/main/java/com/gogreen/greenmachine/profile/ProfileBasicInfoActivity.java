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
import android.widget.Toast;

import com.gogreen.greenmachine.R;
import com.gogreen.greenmachine.parseobjects.PrivateProfile;
import com.gogreen.greenmachine.parseobjects.PublicProfile;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class ProfileBasicInfoActivity extends ActionBarActivity {

    // UI references.
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText homeCityEditText;
    private EditText phoneEditText;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_basic_info);

        // Set up the toolbar
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Set up Edit Texts
        firstNameEditText = (EditText) findViewById(R.id.first_name_edit_text);
        lastNameEditText = (EditText) findViewById(R.id.last_name_edit_text);
        homeCityEditText = (EditText) findViewById(R.id.home_city_edit_text);
        phoneEditText = (EditText) findViewById(R.id.phone_edit_text);

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
        getMenuInflater().inflate(R.menu.menu_profile_basic_info, menu);
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
        final ParseUser currentUser = ParseUser.getCurrentUser();
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String homeCity = homeCityEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();

        savePrivateProfile(firstName, lastName, homeCity, phone);
        try {
            // Check if the profile was not previously initiated
            if (currentUser.get("publicProfile") == null) {
                // Set up private profile
                final PublicProfile publicProfile = new PublicProfile();
                publicProfile.setUser(currentUser);
                publicProfile.setFirstName(firstName);
                publicProfile.setPhoneNumber(phone);

                publicProfile.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            // Show the error message
                            Toast.makeText(ProfileBasicInfoActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        } else {
                            // Start an intent for the dispatch activity
                            currentUser.put("publicProfile", publicProfile);
                            currentUser.saveInBackground();
                            startNextActivity();
                        }
                    }
                });
            } else {
                savePublicProfile(firstName, phone);
                startNextActivity();
            }
        } catch (Exception e) {
            Toast.makeText(ProfileBasicInfoActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(ProfileBasicInfoActivity.this, ProfileBasicInfoActivity.class);
            startActivity(intent);
        }
    }

    private void savePrivateProfile(String firstName, String lastName, String homeCity, String phone) {
        PrivateProfile privProfile = (PrivateProfile) ParseUser.getCurrentUser().get("privateProfile");
        privProfile.setFirstName(firstName);
        privProfile.setLastName(lastName);
        privProfile.setHomeCity(homeCity);
        privProfile.setPhoneNumber(phone);
        privProfile.saveInBackground();
    }

    private void savePublicProfile(String firstName, String phone) {
        PublicProfile pubProfile = (PublicProfile) ParseUser.getCurrentUser().get("publicProfile");
        pubProfile.setFirstName(firstName);
        pubProfile.setPhoneNumber(phone);
        pubProfile.saveInBackground();
    }

    private void startNextActivity() {
        Intent intent = new Intent(ProfileBasicInfoActivity.this, ProfileDriverInfoActivity.class);
        startActivity(intent);
    }
}
