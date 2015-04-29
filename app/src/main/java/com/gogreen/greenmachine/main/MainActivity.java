package com.gogreen.greenmachine.main;

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
import android.widget.Toast;

import com.gogreen.greenmachine.R;
import com.gogreen.greenmachine.helpers.DividerItemDecoration;
import com.gogreen.greenmachine.main.badges.BadgeActivity;
import com.gogreen.greenmachine.main.match.DrivingActivity;
import com.gogreen.greenmachine.main.match.RidingActivity;
import com.gogreen.greenmachine.navigation.NavDrawerAdapter;
import com.gogreen.greenmachine.navigation.SettingsActivity;


public class MainActivity extends ActionBarActivity {
    int ICONS[] = {R.drawable.ic_home,
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
        mRecyclerView.addItemDecoration(
                new DividerItemDecoration(this, null));
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
                    mDrawer.closeDrawers();
                    Toast.makeText(MainActivity.this, "The Item Clicked is: " + recyclerView.getChildPosition(child), Toast.LENGTH_SHORT).show();

                    return true;
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
        mDrawer = (DrawerLayout) findViewById(R.id.DrawerLayout);
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

        menu.findItem(R.id.action_settings).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            }
        });
        return true;
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
}
