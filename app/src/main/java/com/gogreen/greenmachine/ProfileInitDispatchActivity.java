package com.gogreen.greenmachine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Created by jonathanlui on 4/23/15.
 */
public class ProfileInitDispatchActivity extends Activity {

    public ProfileInitDispatchActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check if there is current user info
        final ParseUser currentUser = ParseUser.getCurrentUser();
        try {
            // Check if the profile was previously initiated
            if (currentUser.get("privateProfile") != null) {
                startActivity((new Intent(this, ProfileBasicInfoActivity.class)));
            } else {
                // Set up private profile
                final PrivateProfile privateProfile = new PrivateProfile();
                privateProfile.setUser(currentUser);

                privateProfile.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            // Show the error message
                            Toast.makeText(ProfileInitDispatchActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        } else {
                            // Start an intent for the dispatch activity
                            currentUser.put("privateProfile", privateProfile);
                            currentUser.saveInBackground();
                            Intent intent = new Intent(ProfileInitDispatchActivity.this, ProfileBasicInfoActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                });
            }
        } catch (Exception e) {
            Toast.makeText(ProfileInitDispatchActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(ProfileInitDispatchActivity.this, ProfileInitDispatchActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

}
