package com.example.graffiti;

import org.json.JSONArray;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

public class ListPhotos extends Activity implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {
    ListView listView ;
    private static double longitude, latitude;
    LocationClient mLocationClient;
    private static String url;
    @Override
    protected void onStart(){super.onStart();mLocationClient.connect();}
    @Override
    protected void onStop(){mLocationClient.disconnect();super.onStop();}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        
        mLocationClient = new LocationClient(this,this,this);
        
//        
//        url = "/images/"+longitude + "/" +latitude;
        //JSONArray json = JSONfunctions.getJSONfromURL(url);

        
        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.list);
        
        // Defined Array values to show in ListView
        String[] values = new String[] { "Android List View", 
                                         "Adapter implementation",
                                         "Simple List View In Android",
                                         "Create List View Android", 
                                         "Android Example", 
                                         "List View Source Code", 
                                         "List View Array Adapter", 
                                         "Android Example List View" 
                                        };

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
          android.R.layout.simple_list_item_1, android.R.id.text1, values);


        // Assign adapter to ListView
        listView.setAdapter(adapter); 
        
        // ListView Item Click Listener
        listView.setOnItemClickListener(new OnItemClickListener() {

              @Override
              public void onItemClick(AdapterView<?> parent, View view,
                 int position, long id) {
                
               // ListView Clicked item index
               int itemPosition     = position;
               
               // ListView Clicked item value
               String  itemValue    = (String) listView.getItemAtPosition(position);
                  
                // Show Alert 
                Toast.makeText(getApplicationContext(),
                  "Position :"+itemPosition+"  ListItem : " +itemValue , Toast.LENGTH_LONG)
                  .show();
             
              }

         }); 
        
        
    }

    private static final int TOMENU_MENU_ID = Menu.FIRST;
    private static final int TOCAMERA_MENU_ID = Menu.FIRST + 1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);

    menu.add(0, TOMENU_MENU_ID, 0, "To Menu").setShortcut('3', 'c');
    menu.add(0, TOCAMERA_MENU_ID, 0, "To Camera").setShortcut('3', 'c');

    return true;
    }

    @Override
     public boolean onPrepareOptionsMenu(Menu menu) {
     super.onPrepareOptionsMenu(menu);
     return true;
     }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case TOMENU_MENU_ID:
    	Intent nextScreen = new Intent(getApplicationContext(),
				MainActivity.class);
		startActivity(nextScreen);
        return true;
    case TOCAMERA_MENU_ID:
        return true;
    }
    return super.onOptionsItemSelected(item);
    }
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onConnected(Bundle connectionHint) {
		Location location = mLocationClient.getLastLocation();
        longitude = location.getLongitude();
        latitude = location.getLatitude();
		
	}
	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
    
}