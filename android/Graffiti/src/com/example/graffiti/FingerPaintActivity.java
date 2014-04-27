package com.example.graffiti;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.util.EntityUtils;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class FingerPaintActivity extends Activity implements
		ColorPickerDialog.OnColorChangedListener,
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {
	private static Double longitude, latitude;
	LocationClient mLocationClient;

	MyView mv;
	LinearLayout l;
	AlertDialog dialog;
	Button b1;

	@Override
	public void onDisconnected() {
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		Location location = mLocationClient.getLastLocation();
		longitude = location.getLongitude();
		latitude = location.getLatitude();

	}

	@Override
	protected void onStart() {
		super.onStart();
		mLocationClient.connect();
	}

	@Override
	protected void onStop() {
		mLocationClient.disconnect();
		super.onStop();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mLocationClient = new LocationClient(this, this, this);
		mv = new MyView(this);

		mv.setDrawingCacheEnabled(true);

		// mv.setBackgroundResource(R.drawable.afor);//set the back ground if
		// you wish to
		setContentView(mv);

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(0xFFFF0000);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(20);
		mEmboss = new EmbossMaskFilter(new float[] { 1, 1, 1 }, 0.4f, 6, 3.5f);
		mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);
		startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), 100);
	}

	private Paint mPaint;
	private MaskFilter mEmboss;
	private MaskFilter mBlur;

	public void colorChanged(int color) {
		mPaint.setColor(color);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			mv.mImage = (Bitmap) data.getExtras().get("data");
		}
	}

	public class MyView extends View {

		private static final float MINP = 0.25f;
		private static final float MAXP = 0.75f;
		private Bitmap mImage;
		private Bitmap mBitmap;
		private Canvas mCanvas;
		private Path mPath;
		private Paint mBitmapPaint;
		Context context;

		public MyView(Context c) {
			super(c);
			context = c;
			mPath = new Path();
			mBitmapPaint = new Paint(Paint.DITHER_FLAG);

			mPaint = new Paint();
			mPaint.setAntiAlias(true);
			mPaint.setDither(true);
			mPaint.setColor(0xFFFF0000);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeJoin(Paint.Join.ROUND);
			mPaint.setStrokeCap(Paint.Cap.ROUND);
			mPaint.setStrokeWidth(20);
			mEmboss = new EmbossMaskFilter(new float[] { 1, 1, 1 }, 0.4f, 6,
					3.5f);
			mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);

		}

		public void openColorPicker() {
			new ColorPickerDialog(getContext(), null, mPaint.getColor()).show();

		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			super.onSizeChanged(w, h, oldw, oldh);
			mBitmap = Bitmap.createScaledBitmap(mImage, w, h, false);
			mCanvas = new Canvas(mBitmap);

		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);

			canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);

			canvas.drawPath(mPath, mPaint);
		}

		private float mX, mY;
		private static final float TOUCH_TOLERANCE = 4;

		private void touch_start(float x, float y) {
			// showDialog();
			mPath.reset();
			mPath.moveTo(x, y);
			mX = x;
			mY = y;

		}

		private void touch_move(float x, float y) {
			float dx = Math.abs(x - mX);
			float dy = Math.abs(y - mY);
			if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
				mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
				mX = x;
				mY = y;
			}
		}

		private void touch_up() {
			mPath.lineTo(mX, mY);
			// commit the path to our offscreen
			mCanvas.drawPath(mPath, mPaint);
			// kill this so we don't double draw
			mPath.reset();
			mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
			// mPaint.setMaskFilter(null);
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			float x = event.getX();
			float y = event.getY();

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				touch_start(x, y);
				invalidate();
				break;
			case MotionEvent.ACTION_MOVE:

				touch_move(x, y);
				invalidate();
				break;
			case MotionEvent.ACTION_UP:
				touch_up();
				invalidate();
				break;
			}
			return true;
		}
	}

	private static final int COLOR_MENU_ID = Menu.FIRST;
	private static final int EMBOSS_MENU_ID = Menu.FIRST + 1;
	private static final int BLUR_MENU_ID = Menu.FIRST + 2;
	private static final int ERASE_MENU_ID = Menu.FIRST + 3;
	private static final int SRCATOP_MENU_ID = Menu.FIRST + 4;
	private static final int Save = Menu.FIRST + 5;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, COLOR_MENU_ID, 0, "Color").setShortcut('3', 'c');
		menu.add(0, EMBOSS_MENU_ID, 0, "Emboss").setShortcut('4', 's');
		menu.add(0, BLUR_MENU_ID, 0, "Blur").setShortcut('5', 'z');
		menu.add(0, ERASE_MENU_ID, 0, "Erase").setShortcut('5', 'z');
		menu.add(0, SRCATOP_MENU_ID, 0, "SrcATop").setShortcut('5', 'z');
		menu.add(0, Save, 0, "Save").setShortcut('5', 'z');

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		return true;
	}

	private class SendPic extends AsyncTask<Void, Void, Void> {
		Double latitude;
		Double longitude;
		Bitmap bitmap;

		public SendPic(Double latitude, Double longitude, Bitmap bitmap) {
			super();
			this.latitude = latitude;
			this.longitude = longitude;
			this.bitmap = bitmap;
		}

		@Override
		protected Void doInBackground(Void... params) {
			HttpURLConnection conn = null;
	        DataOutputStream dos = null;
	        DataInputStream inStream = null;
	        String lineEnd = "\r\n";
	        String twoHyphens = "--";
	        String boundary =  "*****";
	        int bytesRead, bytesAvailable, bufferSize;
	        byte[] buffer;
	        int maxBufferSize = 1*1024*1024;
	        String urlString = "http://stumobile0.wesleyan.edu:3000/upload/" + latitude.toString()
					+ "/" + longitude.toString();
	        try{
	            //------------------ CLIENT REQUEST
	        	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        	bitmap.compress(CompressFormat.JPEG, 0, bos);
	            ByteArrayInputStream fileInputStream = new ByteArrayInputStream(bos.toByteArray());
	            // open a URL connection to the Servlet
	            URL url = new URL(urlString);
	            // Open a HTTP connection to the URL
	            conn = (HttpURLConnection) url.openConnection();
	            // Allow Inputs
	            conn.setDoInput(true);
	            // Allow Outputs
	            conn.setDoOutput(true);
	            // Don't use a cached copy.
	            conn.setUseCaches(false);
	            // Use a post method.
	            conn.setRequestMethod("POST");
	            conn.setRequestProperty("Connection", "Keep-Alive");
	            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
	            dos = new DataOutputStream( conn.getOutputStream() );
	            dos.writeBytes(twoHyphens + boundary + lineEnd);
	            dos.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\"" + "img.jpg" + "\"" + lineEnd); // uploaded_file_name is the Name of the File to be uploaded
	            dos.writeBytes(lineEnd);
	            bytesAvailable = fileInputStream.available();
	            bufferSize = Math.min(bytesAvailable, maxBufferSize);
	            buffer = new byte[bufferSize];
	            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
	            while (bytesRead > 0){
	                dos.write(buffer, 0, bufferSize);
	                bytesAvailable = fileInputStream.available();
	                bufferSize = Math.min(bytesAvailable, maxBufferSize);
	                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
	            }
	            dos.writeBytes(lineEnd);
	            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
	            fileInputStream.close();
	            dos.flush();
	            dos.close();
	        }
	        catch (MalformedURLException ex){
	            Log.e("Debug", "error: " + ex.getMessage(), ex);
	        }
	        catch (IOException ioe){
	            Log.e("Debug", "error: " + ioe.getMessage(), ioe);
	        }
	        //------------------ read the SERVER RESPONSE
	        try {
	            inStream = new DataInputStream ( conn.getInputStream() );
	            String str;
	            while (( str = inStream.readLine()) != null){
	                Log.e("Debug","Server Response "+str);
	            }
	            inStream.close();
	        }
	        catch (IOException ioex){
	            Log.e("Debug", "error: " + ioex.getMessage(), ioex);
	        }
	       return null;
			
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		mPaint.setXfermode(null);
		mPaint.setAlpha(0xFF);

		switch (item.getItemId()) {
		case COLOR_MENU_ID:
			new ColorPickerDialog(this, this, mPaint.getColor()).show();
			return true;
		case EMBOSS_MENU_ID:
			if (mPaint.getMaskFilter() != mEmboss) {
				mPaint.setMaskFilter(mEmboss);
			} else {
				mPaint.setMaskFilter(null);
			}
			return true;
		case BLUR_MENU_ID:
			if (mPaint.getMaskFilter() != mBlur) {
				mPaint.setMaskFilter(mBlur);
			} else {
				mPaint.setMaskFilter(null);
			}
			return true;
		case ERASE_MENU_ID:
			mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
			mPaint.setAlpha(0x80);
			return true;
		case SRCATOP_MENU_ID:

			mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
			mPaint.setAlpha(0x80);
			return true;
		case Save:
			new SendPic(latitude, longitude, mv.getDrawingCache()).execute();

			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
