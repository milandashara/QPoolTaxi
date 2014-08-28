package com.example.qpooltaxi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.common.Constants;
import com.example.common.Gender;
import com.example.model.User;

public class MainActivity extends Activity {

	Logger logger = Logger.getLogger("MainActivity");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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

	public void register(View button) {

		final EditText nameField = (EditText) findViewById(R.id.EditTextName);
		String name = nameField.getText().toString();

		final EditText phoneField = (EditText) findViewById(R.id.EditTextPhone);
		String phone = phoneField.getText().toString();

		final Spinner genderField = (Spinner) findViewById(R.id.SpinnerGender);
		String gender = genderField.getSelectedItem().toString();
		
		
		final EditText passwordField = (EditText) findViewById(R.id.EditTextPassword);
		String password = phoneField.getText().toString();

		final User user = new User();
		if(gender.equals("male"))
		{
			user.setGender(Gender.M.toString());
		}
		else
		{
			user.setGender(Gender.F.toString());
		}
		
		user.setName(name);
		user.setPhone(phone);
		user.setPassword(password);
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

		user.setDeviceId(telephonyManager.getDeviceId());

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					registerDevice(user);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		thread.start();
	}

	private void registerDevice(User user) {
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(Constants.URL_REGISTER);

		try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
			nameValuePairs
					.add(new BasicNameValuePair("mobile", user.getPhone()));
			nameValuePairs
					.add(new BasicNameValuePair("dev", user.getDeviceId()));
			nameValuePairs.add(new BasicNameValuePair("name", user.getName()));
			nameValuePairs.add(new BasicNameValuePair("gender", user
					.getGender()));
			nameValuePairs.add(new BasicNameValuePair("password", user
					.getPassword()));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);
			int status=new Integer(response.toString());
			if(status == Constants.USER_SUCCESS)
			{
				//registration success full
				
			}
			else if(status == Constants.USER_ALREADY_EXIST)
			{
				String text = getResources().getString(R.string.error_msg_alreadyRegistered);
				showToastMessage(text);
				
			}
			else 
			{
				String text = getResources().getString(R.string.error_msg_register);
				showToastMessage(text);
				
			}
			logger.info(response.toString());

		} catch (ClientProtocolException e) {

			String text = getResources().getString(R.string.error_msg_register);
			showToastMessage(text);
			e.printStackTrace();

		} catch (IOException e) {
			String text = getResources().getString(R.string.error_msg_register);
			showToastMessage(text);
			e.printStackTrace();
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
