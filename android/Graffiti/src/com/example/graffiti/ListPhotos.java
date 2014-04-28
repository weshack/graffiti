package com.example.graffiti;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import android.app.ListActivity;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

public class ListPhotos extends ListActivity implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {
    private static double longitude, latitude;
    LocationClient mLocationClient;
    ListArrayAdapter ls;
    @Override
    protected void onStart(){super.onStart();mLocationClient.connect();}
    @Override
    protected void onStop(){mLocationClient.disconnect();super.onStop();}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mLocationClient = new LocationClient(this,this,this); 
        ls = new ListArrayAdapter(this);
        setListAdapter(ls); 
        new LoadClosest(ls).execute(latitude,longitude);
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
    	Intent intent = new Intent(this, PhotoView.class);
    	intent.putExtra("url", ls.urls.get(position));
    	startActivity(intent);
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
	
	private static class LoadClosest extends AsyncTask<Double,Void,Graffiti[]> {
		ListArrayAdapter ls;
		Double latitude;
		Double longitude;
		public LoadClosest(ListArrayAdapter ls) {
			super();
			this.ls = ls;
		}
		@Override
		protected Graffiti[] doInBackground(Double... params) {
			latitude = params[0];
			longitude = params[1];
			DefaultHttpClient   httpclient = new DefaultHttpClient(new BasicHttpParams());
			HttpGet httpget = new HttpGet("http://stumobile0.wesleyan.edu:3000/images/" + latitude.toString() + "/" + longitude.toString());
			// Depends on your web service
			httpget.setHeader("Content-type", "application/json");

			InputStream inputStream = null;
			String result = null;
			try {
			    HttpResponse response = httpclient.execute(httpget);           
			    HttpEntity entity = response.getEntity();

			    inputStream = entity.getContent();
			    // json is UTF-8 by default
			    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
			    StringBuilder sb = new StringBuilder();

			    String line = null;
			    while ((line = reader.readLine()) != null)
			    {
			        sb.append(line + "\n");
			    }
			    result = sb.toString();
			} catch (Exception e) { 
			    Log.e("fck", e.toString());
			}
			finally {
			    try{if(inputStream != null)inputStream.close();}catch(Exception squish){}
			}
			JSONArray jArray;
			try {
				jArray = new JSONArray(result);
				Graffiti[] res = new Graffiti[jArray.length()];
				for (int i=0; i < jArray.length(); i++)
				{
					String id =  jArray.getJSONObject(i).getString("id");
					double lat = jArray.getJSONObject(i).getDouble("latitude");
					double lon = jArray.getJSONObject(i).getDouble("longitude");
					res[i] = new Graffiti(id,lat,lon);
				}
				return res;
			} catch (JSONException e) { Log.e("fck", e.toString());}
			
			return null;
		}
		
		static double dist(double lat1, double lon1, double lat2, double lon2) {
			double R = 6371; // km
			double dLat = (lat2-lat1) * Math.PI / 180;
			double dLon = (lon2-lon1) * Math.PI / 180;
			lat1 = lat1 * Math.PI / 180;
			lat2 = lat2 * Math.PI / 180;
			double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
					        Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat1) * Math.cos(lat2); 
			double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
			double d = R * c;
			return d / 1.609344;
		}
		
		@Override
		protected void onPostExecute(Graffiti[] result) {
			for (int i = 0; i < result.length; i++){
				ls.add(dist(latitude,longitude,result[i].latitude,result[i].longitude) + " miles away");
				ls.urls.add("http://stumobile0.wesleyan.edu:3000/static/graffiti/" + result[i].id + ".jpg");
			}
		}
	}
	
	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
}
