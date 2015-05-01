package com.gogreen.greenmachine.main.match;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.gogreen.greenmachine.R;
import com.gogreen.greenmachine.navigation.distmatrix.Element;
import com.gogreen.greenmachine.navigation.distmatrix.Result;
import com.gogreen.greenmachine.navigation.distmatrix.Row;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson.JacksonFactory;
import com.gogreen.greenmachine.navigation.RetrieveDistanceMatrix;
import java.io.IOException;
import java.util.List;


public class DriverMatchedActivity extends ActionBarActivity {

    private Toolbar toolbar;

    static final HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();
    static final JsonFactory JSON_FACTORY = new JacksonFactory();
    HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
                                                                                @Override
                                                                                public void initialize(HttpRequest request) {
                                                                                    request.setParser(new JsonObjectParser(JSON_FACTORY));
                                                                                }
                                                                            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_matched);
        String origins="37.5505658,-122.3094177";
        String destinations="37.5505658,-122.3094177";
        String mode="driving";
        String language="US-EN";
        String key="AIzaSyCzhOo4mqXFIMa73xk5N-2A5mifzcpINfo";
        String urlString="https://maps.googleapis.com/maps/api/distancematrix/json";
        // Set up the toolbar
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        GenericUrl url = new GenericUrl(urlString);
        url.put("origins", origins);
        url.put("destinations", destinations);
        url.put("mode", mode);
        url.put("language", language);
        url.put("key", key);
        Log.i(DriverMatchedActivity.class.getSimpleName(),url+" "+url);
        new RetrieveDistanceMatrix().execute(url);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_driver_matched, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
}
