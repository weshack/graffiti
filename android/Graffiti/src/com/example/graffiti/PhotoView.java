package com.example.graffiti;

import com.example.graffiti.ListArrayAdapter.GetBitmap;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class PhotoView extends Activity{
	
	private String url; 
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Intent i = getIntent();
		url = i.getExtras().getString("url");

		LinearLayout l = new LinearLayout(this);
		ImageView iv = new ImageView(this);
		
		iv.setTag(url);
		new GetBitmap().execute(iv);
		l.addView(iv);
		setContentView(l);
	}

}
