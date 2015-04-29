package com.gogreen.greenmachine.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.gogreen.greenmachine.R;
import com.gogreen.greenmachine.main.badges.BadgeActivity;
import com.gogreen.greenmachine.main.login.DispatchActivity;
import com.gogreen.greenmachine.main.match.DrivingActivity;
import com.gogreen.greenmachine.main.match.RidingActivity;
import com.gogreen.greenmachine.navigation.NavDrawerAdapter;
import com.gogreen.greenmachine.navigation.SettingsActivity;
import com.parse.LogOutCallback;
import com.parse.ParseUser;
import com.parse.ParseException;

public class MainActivity extends ActionBarActivity {

    // Menu positions
    private final int HOME = 1;
    private final int BADGES = 2;
    private final int HOTSPOTS = 3;
    private final int ABOUT_US = 4;
    private final int LOGOUT = 5;

    private int ICONS[] = {R.drawable.ic_home,
            R.drawable.ic_home,
            R.drawable.ic_home,
            R.drawable.ic_home,
            R.drawable.ic_home};

    String NAME = "Connor Horton";
    String EMAIL = "connor.horton@oracle.com";
    int PROFILE = R.drawable.jonathan_lui;

    private String[] navRowTitles;
    private TypedArray navRowIcons;

    private Toolbar toolbar;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DrawerLayout mDrawer;

    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Grab appropriate data for adapter
        navRowTitles = getResources().getStringArray(R.array.navigation_drawer_titles);
        navRowIcons = getResources().obtainTypedArray(R.array.navigation_drawer_icons);

        // Set up the toolbar
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Set up recycler and provide it with the proper adapter
        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new NavDrawerAdapter(navRowTitles, ICONS, NAME, EMAIL, PROFILE, this);
        mRecyclerView.setAdapter(mAdapter);

        final GestureDetector mGestureDetector = new GestureDetector(MainActivity.this,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override public boolean onSingleTapUp(MotionEvent e) {
                        return true;
                }
        });

        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if(child != null && mGestureDetector.onTouchEvent(motionEvent)){
                    int childPosition = recyclerView.getChildPosition(child);
                    mDrawer.closeDrawers();
                    switch(childPosition) {
                        case HOME:
                            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                            return true;
                        case BADGES:
                            startActivity(new Intent(MainActivity.this, BadgeActivity.class));
                            return true;
                        case HOTSPOTS:
                            return true;
                        case ABOUT_US:
                            return true;
                        case LOGOUT:
                            logout();
                            return true;
                        default:
                            return true;
                    }
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

            }
        });

        // Set the proper layout
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Set the drawer
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawer,
                toolbar,
                R.string.open_drawer,
                R.string.close_drawer) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
                syncState();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
                syncState();
            }
        };
        mDrawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        // Set up the handler for the driving button click
        Button drivingButton = (Button) findViewById(R.id.driving_button);
        drivingButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DrivingActivity.class);
                startActivity(intent);
            }
        });

        // Set up the handler for the riding button click
        Button ridingButton = (Button) findViewById(R.id.riding_button);
        ridingButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RidingActivity.class);
                startActivity(intent);
            }
        });

        // Set up the handler for the riding button click
        Button badgesButton = (Button) findViewById(R.id.badges_button);
        badgesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BadgeActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage(getString(R.string.progress_logout));
        dialog.show();

        ParseUser.logOutInBackground(new LogOutCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    dialog.dismiss();
                    startNextActivity();
                }
            }
        });
    }

    private void startNextActivity() {
        // Start and intent for the dispatch activity
        Intent intent = new Intent(MainActivity.this, DispatchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
