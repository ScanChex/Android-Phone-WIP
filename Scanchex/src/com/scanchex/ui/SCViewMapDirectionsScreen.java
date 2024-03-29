package com.scanchex.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.scanchex.bo.AssetsTicketsInfo;
import com.scanchex.utils.Resources;
import com.scanchex.utils.SCPreferences;
import com.squareup.picasso.Picasso;

public class SCViewMapDirectionsScreen extends FragmentActivity implements
		OnClickListener , LocationListener {

	private GoogleMap mapView;
	Polyline line;
	private LocationManager locManager;
	private double longitude;
	private double latitude;
	private Marker userLoc;
	private String provider;
	private Location location;

	LatLng startLatLng;
	LatLng endLatLng;// = new LatLng(29.3956, 71.6836);

	AssetsTicketsInfo tInfo;
	LinearLayout layout;
	ImageView ticketStatusIcon;
	ImageView image;
	TextView clientName;
	TextView phoneNumber;
	TextView address1;
	TextView address2;

	TextView ticketId;
	TextView assetId;
	TextView assetName;
	TextView ticketStartDate;
	TextView ticketStartTime;
	ImageView mapIcon;
	ImageView detailIcon;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		setContentView(R.layout.sc_viewmapdirections_screen);
		LinearLayout layoutScreen = (LinearLayout) findViewById(R.id.mapDirectionScreen);
		layoutScreen.setBackgroundColor((SCPreferences
				.getColor(SCViewMapDirectionsScreen.this)));

		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();

		layout = (LinearLayout) findViewById(R.id.tickets_layout);
		image = (ImageView) findViewById(R.id.image_view);
		ticketStatusIcon = (ImageView) findViewById(R.id.ticket_status_icon);
		clientName = (TextView) findViewById(R.id.text1);
		phoneNumber = (TextView) findViewById(R.id.text2);
		address1 = (TextView) findViewById(R.id.text3);
		address2 = (TextView) findViewById(R.id.text4);

		ticketId = (TextView) findViewById(R.id.text5);
		assetId = (TextView) findViewById(R.id.text6);
		assetName = (TextView) findViewById(R.id.text7);
		ticketStartDate = (TextView) findViewById(R.id.text8);
		ticketStartTime = (TextView) findViewById(R.id.text9);
		mapIcon = (ImageView) findViewById(R.id.map_icon);
		detailIcon = (ImageView) findViewById(R.id.ticket_detail_icon);
		mapIcon.setOnClickListener(this);
		detailIcon.setOnClickListener(this);

		tInfo = Resources.getResources().getAssetTicketInfo();

		if (tInfo.ticketOverDue.equals("1")) {

			layout.setBackgroundColor(this.getResources().getColor(R.color.red));
			ticketStatusIcon.setImageResource(R.drawable.excalamation_icon);
		} else if (tInfo.ticketStatus.equalsIgnoreCase("Assigned")
				&& tInfo.ticketOverDue.equals("0")) {

			layout.setBackgroundColor(this.getResources().getColor(
					R.color.green));
			ticketStatusIcon.setVisibility(View.GONE);
		} else if (tInfo.ticketStatus.equalsIgnoreCase("complete")) {

			layout.setBackgroundColor(this.getResources()
					.getColor(R.color.grey));
			ticketStatusIcon.setImageResource(R.drawable.accept_ticket);
		} else if (tInfo.ticketStatus.equalsIgnoreCase("pending")) {

			layout.setBackgroundColor(this.getResources()
					.getColor(R.color.blue));
			ticketStatusIcon.setVisibility(View.VISIBLE);
			ticketStatusIcon.setBackgroundResource(R.drawable.lightning_image);
		} else {
			layout.setBackgroundColor(this.getResources().getColor(R.color.red));
			ticketStatusIcon.setImageResource(R.drawable.excalamation_icon);
		}

		clientName.setText(tInfo.assetClientName);
		phoneNumber.setText(tInfo.assetPhone);
		address1.setText(tInfo.addressStreet);
		address2.setText(tInfo.addressCity + ", " + tInfo.addressState);

		ticketId.setText(tInfo.ticketId);
		assetId.setText(tInfo.assetUNAssetId);
		assetName.setText(tInfo.assetDescription);
		ticketStartDate.setText(tInfo.ticketStartDate);
		ticketStartTime.setText(tInfo.ticketStartTime);

		try{
		Picasso.with(this) //
		.load(tInfo.thumbPhotoUrl) //
		.placeholder(R.drawable.photo_not_available) //
		.error(R.drawable.photo_not_available) //
		.into(image);
		}catch(Exception e){
			e.printStackTrace();
		}
		mapView = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		Resources.getResources().setAssetsTicketData(null);

		mapView.getUiSettings().setZoomControlsEnabled(true);

		locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		 provider = locManager.getBestProvider(criteria, false);
		 location = locManager.getLastKnownLocation(provider);
		 getAccurateLocation();
		if (location != null) {
			latitude = location.getLatitude();
			longitude = location.getLongitude();
			Log.i("LOCATION LAT>>" + latitude, "Longitute" + longitude);
			 onLocationChanged(location);
		}
		startLatLng = new LatLng(latitude, longitude);
		endLatLng = new LatLng(Double.parseDouble(tInfo.assetlatitude),
				Double.parseDouble(tInfo.assetLongitude));
		// startLatLng = new LatLng(31.4804658, 74.2808364);
		// endLatLng = new LatLng(31.4754, 74.3431);
		// GoogleMap myMap

		mapView.setMyLocationEnabled(true);
		mapView.moveCamera(CameraUpdateFactory.newLatLng(startLatLng));
		mapView.animateCamera(CameraUpdateFactory.zoomTo(10));

		String urlTopass = makeURL(startLatLng.latitude, startLatLng.longitude,
				endLatLng.latitude, endLatLng.longitude);
		new connectAsyncTask(urlTopass).execute();
	}
	
	 @Override
	    protected void onStart() {
	        super.onStart();       
	     
	        	if ( SCPreferences.getPreferences().getUserFullName(this).length()>0) {
	        		if (Resources.getResources().isLaunchloginactivity()  && Resources.getResources().isFromBackground())  {
	        	//	fireAlarm();
	        			Log.i("Base Activity", "App in foreground after 10 mins ");
	        			 Resources.getResources().setLaunchloginactivity(false);
	        			 Resources.getResources().setFromBackground(false);
	        			Intent i = new Intent(this, SCLoginScreen.class);
	        			i.addFlags((Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
	    		        
	        			startActivity(i);
	        		   
	        		}
	        	    	
	        		}
	      
	    }

	@Override
	public void onClick(View v) {

		if (v == detailIcon) {
			Intent details = new Intent(SCViewMapDirectionsScreen.this,
					SCDetailsFragmentScreen.class);
			startActivity(details);
			this.finish();
		} else if (v == mapIcon) {
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
					Uri.parse("http://maps.google.com/maps?saddr=" + latitude
							+ "," + longitude + "&daddr="
							+ Double.parseDouble(tInfo.assetlatitude) + ","
							+ Double.parseDouble(tInfo.assetLongitude)));
			startActivity(intent);
		}

	}
	
	   @Override
	    public void onLocationChanged(Location location) {
	        double lat =  location.getLatitude();
	        double lng = location.getLongitude();
	        LatLng coordinate = new LatLng(lat, lng);
	        if(userLoc!=null) userLoc.remove();
	         userLoc =  mapView.addMarker(new MarkerOptions()
	        .position(coordinate)
	        .title("You are here")
	        .snippet("")
	        .icon(BitmapDescriptorFactory.fromResource(R.drawable.fblue_flag_32)));
	    }
	
	// Pass the desired latitude and longitude to this method
	  public void showMarker(Double lat, Double lon) {
		  mapView.clear();
	      // Create a LatLng object with the given Latitude and Longitude
	      LatLng markerLoc = new LatLng(lat, lon);

	      //Add marker to map
	      mapView.addMarker(new MarkerOptions()
	              .position(markerLoc)                                                                        // at the location you needed
	              .title("User Location")                                                                     // with a title you needed
	              .snippet("")                                                           // and also give some summary of available
	              .icon(BitmapDescriptorFactory.fromResource(R.drawable.fblue_flag_32))); // and give your animation drawable as icon
	  }
	  
	  @Override
	    public void onProviderDisabled(String provider) {
	        Toast.makeText(this, "Enabled new provider " + provider,
	                Toast.LENGTH_SHORT).show();

	    }


	    @Override
	    public void onProviderEnabled(String provider) {
	        Toast.makeText(this, "Disabled provider " + provider,
	                Toast.LENGTH_SHORT).show();

	    }


	    @Override
	    public void onStatusChanged(String provider, int status, Bundle extras) {
	        // TODO Auto-generated method stub

	    }

	    
	    /* Request updates at startup */
	    @Override
	    protected void onResume() {
	        super.onResume();
	        locManager.requestLocationUpdates(provider, 10000, 1, this);
	    }

	    /* Remove the locationlistener updates when Activity is paused */
	    @Override
	    protected void onPause() {
	        super.onPause();
	        locManager.removeUpdates(this);
	    }
	    
	    
	    public void getAccurateLocation(){
			
			Log.i("GET ACCURATE LOCATION", "GET ACCURATE LOCATION");
			Location gpsLocation = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			Location networkLocation = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);		
			if(networkLocation != null && gpsLocation != null){
				location = getBetterLocation(networkLocation, gpsLocation);
			}else if(gpsLocation != null){
				location = gpsLocation;
			}else if(networkLocation != null){
			location = networkLocation;
					}else{
				location = networkLocation;
			}	
		}
		
		protected Location getBetterLocation(Location newLocation, Location currentBestLocation) {
			if (currentBestLocation == null) {
				// A new location is always better than no location
				return newLocation;
			}

			// Check whether the new location fix is newer or older
			long timeDelta = newLocation.getTime() - currentBestLocation.getTime();
			boolean isSignificantlyNewer = timeDelta > (1000 * 60 * 2);
			boolean isSignificantlyOlder = timeDelta < -(1000 * 60 * 2);
			boolean isNewer = timeDelta > 0;

			// If it's been more than two minutes since the current location, use the new location
			// because the user has likely moved.
			if (isSignificantlyNewer) {
				return newLocation;
				// If the new location is more than two minutes older, it must be worse
			} else if (isSignificantlyOlder) {
				return currentBestLocation;
			}

			// Check whether the new location fix is more or less accurate
			int accuracyDelta = (int) (newLocation.getAccuracy() - currentBestLocation.getAccuracy());
			boolean isLessAccurate = accuracyDelta > 0;
			boolean isMoreAccurate = accuracyDelta < 0;
			boolean isSignificantlyLessAccurate = accuracyDelta > 200;

			// Check if the old and new location are from the same provider
			boolean isFromSameProvider = isSameProvider(newLocation.getProvider(),currentBestLocation.getProvider());

			// Determine location quality using a combination of timeliness and accuracy
			if (isMoreAccurate) {
				return newLocation;
			} else if (isNewer && !isLessAccurate) {
				return newLocation;
			} else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
				return newLocation;
			}
			return currentBestLocation;
		}
		
		private boolean isSameProvider(String provider1, String provider2) {
			if (provider1 == null){
				return provider2 == null;
			}
			
			return provider1.equals(provider2);
		}
	    
	private class connectAsyncTask extends AsyncTask<Void, Void, String> {
		private ProgressDialog progressDialog;
		String url;
		connectAsyncTask(String urlPass) {
			url = urlPass;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(SCViewMapDirectionsScreen.this);
			progressDialog.setMessage("Fetching route, Please wait...");
			progressDialog.setIndeterminate(true);
			progressDialog.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			JSONParser jParser = new JSONParser();
			String json = jParser.getJSONFromUrl(url);
			return json;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			progressDialog.hide();
			if (result != null) {
				drawPath(result);
			}
		}
	}

	public String makeURL(double sourcelat, double sourcelog, double destlat,
			double destlog) {
		StringBuilder urlString = new StringBuilder();
		urlString.append("http://maps.googleapis.com/maps/api/directions/json");
		urlString.append("?origin=");// from
		urlString.append(Double.toString(sourcelat));
		urlString.append(",");
		urlString.append(Double.toString(sourcelog));
		urlString.append("&destination=");// to
		urlString.append(Double.toString(destlat));
		urlString.append(",");
		urlString.append(Double.toString(destlog));
		urlString.append("&sensor=false&mode=driving&alternatives=true");
		return urlString.toString();
	}

	public class JSONParser {

		InputStream is = null;
		JSONObject jObj = null;
		String json = "";

		// constructor
		public JSONParser() {
		}

		public String getJSONFromUrl(String url) {

			// Making HTTP request
			try {
				// defaultHttpClient
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(url);

				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is, "iso-8859-1"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				json = sb.toString();
				is.close();
			} catch (Exception e) {
				Log.e("Buffer Error", "Error converting result " + e.toString());
			}
			return json;

		}
	}

	public void drawPath(String result) {

		if (line != null) {
			mapView.clear();
		}
		
		
		mapView.addMarker(new MarkerOptions().position(endLatLng).title(tInfo.assetClientName+"\n"+tInfo.ticketId+"\n"+tInfo.addressCity+", "+tInfo.addressState+"\n"+tInfo.assetDescription)
				.icon(
				BitmapDescriptorFactory
						.fromResource(R.drawable.other_locations)));
		mapView.addMarker(new MarkerOptions().position(startLatLng).icon(
				BitmapDescriptorFactory.fromResource(R.drawable.my_location)));
		
		mapView.setInfoWindowAdapter(new InfoWindowAdapter() {

	        @Override
	        public View getInfoWindow(Marker arg0) {
	            return null;
	        }

	        @Override
	        public View getInfoContents(Marker marker) {

	            View v = getLayoutInflater().inflate(R.layout.marker, null);

	            TextView info= (TextView) v.findViewById(R.id.info);

	            info.setText(marker.getTitle());

	            return v;
	        }
	    });
		
		try {
			// Tranform the string into a json object
			final JSONObject json = new JSONObject(result);
			JSONArray routeArray = json.getJSONArray("routes");
			if ( routeArray.length() > 0  ) {
			JSONObject routes = routeArray.getJSONObject(0);
			JSONObject overviewPolylines = routes
					.getJSONObject("overview_polyline");
			String encodedString = overviewPolylines.getString("points");
			List<LatLng> list = decodePoly(encodedString);

			PolylineOptions options = new PolylineOptions().width(5)
					.color(Color.BLUE).geodesic(true);// gul
			for (int z = 0; z < list.size(); z++) {
				LatLng point = list.get(z);
				options.add(point);
			}
			line = mapView.addPolyline(options);
			try {
				LatLngBounds.Builder builder = new LatLngBounds.Builder();
				builder.include(startLatLng);
				builder.include(endLatLng);
//				LatLngBounds bounds = builder.build();
//				mapView.animateCamera(CameraUpdateFactory.newLatLngBounds(
//						bounds, 20));
				
				  LatLngBounds bounds = builder.build();
                  int padding = 0; // offset from edges of the map in pixels
                  CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                  mapView.moveCamera(cu);
                //  mapView.animateCamera(cu);
                  float zoom = mapView.getCameraPosition().zoom;
	               
                  mapView.animateCamera(CameraUpdateFactory.zoomTo(zoom-1.0f));

//				CameraPosition cameraPosition = new CameraPosition.Builder()
//				.target(startLatLng).zoom(13).build();

//				mapView.animateCamera(CameraUpdateFactory
//				.newCameraPosition(cameraPosition));
				
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			// for (int z = 0; z < list.size() - 1; z++) {//orig
			// LatLng src = list.get(z);
			// LatLng dest = list.get(z + 1);
			// line = mapView.addPolyline(new PolylineOptions()
			// .add(new LatLng(src.latitude, src.longitude),
			// new LatLng(dest.latitude, dest.longitude))
			// .width(5).color(Color.BLUE).geodesic(true));
			// }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private List<LatLng> decodePoly(String encoded) {

		List<LatLng> poly = new ArrayList<LatLng>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;

		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;

			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			LatLng p = new LatLng((((double) lat / 1E5)),
					(((double) lng / 1E5)));
			poly.add(p);
		}

		return poly;
	}
}
