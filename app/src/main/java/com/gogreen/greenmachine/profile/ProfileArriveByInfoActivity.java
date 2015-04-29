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


public class ProfileArriveByInfoActivity extends ActionBarActivity {

    // UI references.
    private EditText arriveHQByEditText;
    private EditText arriveHomeByEditText;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_arrive_by_info);

        // Set up the toolbar
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        arriveHQByEditText = (EditText) findViewById(R.id.arrive_hq_by_edit_text);
        arriveHomeByEditText = (EditText) findViewById(R.id.arrive_home_by_edit_text);

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
        getMenuInflater().inflate(R.menu.menu_profile_arrive_by_info, menu);
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
        String arriveHQBy = arriveHQByEditText.getText().toString().trim();
        String arriveHomeBy = arriveHomeByEditText.getText().toString().trim();

        savePrivateProfile(arriveHQBy, arriveHomeBy);

        Intent intent = new Intent(ProfileArriveByInfoActivity.this, ProfileHotspotInfoActivity.class);
        startActivity(intent);
    }

    private void savePrivateProfile(String arriveHQBy, String arriveHomeBy) {
        PrivateProfile privProfile = (PrivateProfile) ParseUser.getCurrentUser().get("privateProfile");
        privProfile.setArriveHQBy(arriveHQBy);
        privProfile.setArriveHomeBy(arriveHomeBy);
        privProfile.saveInBackground();
    }
}
