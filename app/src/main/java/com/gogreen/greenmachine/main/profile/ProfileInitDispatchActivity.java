package com.gogreen.greenmachine.main.profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.parse.ParseUser;

/**
 * Created by jonathanlui on 4/23/15.
 * Used to keep track of last step user was on
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
                finish();
            }
        } catch (Exception e) {
            Toast.makeText(ProfileInitDispatchActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(ProfileInitDispatchActivity.this, ProfileInitDispatchActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

}
