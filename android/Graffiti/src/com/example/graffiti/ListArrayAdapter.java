package com.example.graffiti;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListArrayAdapter extends ArrayAdapter<String> {
	private final Context context;
	List<String> urls;
		
	public ListArrayAdapter(Context context) {
		super(context, R.layout.list_mobile);
		this.context = context;
	}
 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
 
		View rowView = inflater.inflate(R.layout.list_mobile, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.label);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.logo);
		textView.setText(getItem(position));

		imageView.setTag(urls.get(position));
		new GetBitmap().execute(imageView);
		

		return rowView;
	}
	

	 public static class GetBitmap extends AsyncTask<ImageView, Void, Bitmap> {
		 ImageView iv;
	     protected Bitmap doInBackground(ImageView... ivs) {
	 	    try {
	 	    	iv = ivs[0];
		        URL url = new URL((String)iv.getTag());
		        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
 		        connection.setDoInput(true);
		        connection.connect();
		        InputStream input = connection.getInputStream();
		        Bitmap myBitmap = BitmapFactory.decodeStream(input);
		        return myBitmap;
		        
		    } catch (IOException e) {
		        
		        return null;
		    }
	     }

	     protected void onPostExecute(Bitmap result) {
	    	 Log.e("Got here:", result.toString());
	    	 iv.setImageBitmap(result);
	     }
	 }
	 
}