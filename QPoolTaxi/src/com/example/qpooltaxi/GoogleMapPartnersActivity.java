package com.example.qpooltaxi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.common.Constants;
import com.example.common.ServiceHandler;
import com.example.model.FindPartner;
import com.example.model.Partner;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class GoogleMapPartnersActivity extends Activity {
	private FindPartner findPartner;
	private GoogleMap map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_google_map_users);
		Intent intent = getIntent();
		findPartner = (FindPartner) intent.getExtras().get("findPartner");

		// Get a handle to the Map Fragment
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		
		LatLng latLng = new LatLng(findPartner.getTaxiStand().getLat(),findPartner.getTaxiStand().getLng());
		
		map.addMarker(new MarkerOptions().title(findPartner.getTaxiStand().getName())
				.snippet("I want to share Taxi").position(latLng));

		new FindPartnerAsyncTask().execute(findPartner);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.google_map_users, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public class FindPartnerAsyncTask extends
			AsyncTask<FindPartner, FindPartner, FindPartner> {

		@Override
		protected FindPartner doInBackground(FindPartner... params) {
			// TODO Auto-generated method stub
			try {
				return postData(params[0]);
			} catch (ClientProtocolException e) {
				String text = getResources()
						.getString(R.string.error_msg_sigIn);
				showToastMessage(text);
			} catch (IOException e) {
				String text = getResources()
						.getString(R.string.error_msg_sigIn);
				showToastMessage(text);
			}
			return null;
		}

		protected void onPostExecute(FindPartner findPartner) {

			if (findPartner.getStatus() == Constants.USER_SUCCESS) {

				if (findPartner.getPartners().size() > 0) {
					// display on map
					for (Partner p : findPartner.getPartners()) {
						LatLng latLng = new LatLng(Double.parseDouble(p
								.getDlat()), Double.parseDouble(p.getDlon()));

						map.setMyLocationEnabled(true);
						// map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,
						// 13));
						String title = p.getDname() + "\n"
								+ "Number of person : " + p.getCurrmem()
								+ " \n " + " Name :" + p.getName();
						map.addMarker(new MarkerOptions().title(title)
								.snippet("I want to share Taxi")
								.position(latLng));

						map.moveCamera(CameraUpdateFactory.newLatLngZoom(
								latLng, 15));
						// Zoom in, animating the camera.
						map.animateCamera(CameraUpdateFactory.zoomIn());
						// Zoom out to zoom level 10, animating with a duration
						// of 2 seconds.
						map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000,
								null);
					}

				}

			} else if (findPartner.getStatus() == Constants.USER_ALREADY_EXIST) {
				// user name / password is wrong
				findPartner = null;
				String text = getResources().getString(
						R.string.error_msg_taxiStandNotFound);
				showToastMessage(text);

			} else {
				String text = getResources().getString(
						R.string.error_msg_taxiStandNotFound);
				showToastMessage(text);

			}

		}

		protected void onProgressUpdate(Integer... progress) {
			// pb.setProgress(progress[0]);
		}

		public FindPartner postData(FindPartner findPartner)
				throws ClientProtocolException, IOException {

			// Creating service handler class instance
			ServiceHandler sh = new ServiceHandler();

			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
			nameValuePairs.add(new BasicNameValuePair("standid", findPartner
					.getTaxiStand().getTaxiStandId()));

			nameValuePairs.add(new BasicNameValuePair("mobile", findPartner
					.getUser().getPhone()));
			nameValuePairs.add(new BasicNameValuePair("dlat", findPartner
					.getLat().toString()));
			nameValuePairs.add(new BasicNameValuePair("dlon", findPartner
					.getLng().toString()));
			nameValuePairs.add(new BasicNameValuePair("dlat", findPartner
					.getLat().toString()));
			nameValuePairs.add(new BasicNameValuePair("dname", findPartner
					.getDestination()));
			nameValuePairs.add(new BasicNameValuePair("memreq", findPartner
					.getMemreq()));
			nameValuePairs.add(new BasicNameValuePair("numofmem", findPartner
					.getNumOfmem()));

			String jsonStr = sh.makeServiceCall(Constants.URL_FINDMATCH, 2,
					nameValuePairs);

			jsonStr = jsonStr.replaceAll("\\\\\"", "\"");
			jsonStr = jsonStr.replace("\"[", "[");
			jsonStr = jsonStr.replace("]\"", "]");

			List<Partner> partners = new ArrayList<Partner>();
			if (jsonStr != null) {
				try {
					JSONObject jsonObj = new JSONObject(jsonStr);
					JSONArray jsonArray = jsonObj.getJSONArray("response");

					for (int i = 0; i < jsonArray.length(); i++) {
						Partner p = new Partner();
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						p.setDlat(jsonObject.get("dlat").toString());
						p.setMobile(jsonObject.get("mobile").toString());
						p.setName(jsonObject.get("name").toString());
						p.setDev(jsonObject.get("dev").toString());
						p.setDname(jsonObject.get("dname").toString());
						p.setSlat(jsonObject.get("slat").toString());
						p.setDlat(jsonObject.get("dlat").toString());
						p.setDlon(jsonObject.get("dlon").toString());
						p.setSlon(jsonObject.get("slon").toString());
						p.setCurrmem(jsonObject.get("currmem").toString());
						p.setMemreq(jsonObject.get("memreq").toString());
						p.setSname(jsonObject.get("sname").toString());
						partners.add(p);

					}

				} catch (JSONException e) {
					e.printStackTrace();
				}

				findPartner.setPartners(partners);

			} else {
				Log.e("ServiceHandler", "Couldn't get any data from the url");
			}

			return findPartner;
		}
	}

	private void showToastMessage(String msg) {
		Context context = getApplicationContext();
		String text = getResources().getString(R.string.error_msg_register);
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}
}
