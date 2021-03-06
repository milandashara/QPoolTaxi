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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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

	private String userPreference = "loginCredential";
	private User user = new User();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Restore preferences
		SharedPreferences settings = getSharedPreferences(userPreference, 0);

		String name = settings.getString("name", user.getName());
		String password = settings.getString("password", user.getPassword());
		String mobile = settings.getString("mobile", user.getPhone());
		String gender = settings.getString("gender", user.getGender());
		String deviceId = settings.getString("deviceId", user.getDeviceId());

		if (mobile != null) {
			user.setDeviceId(deviceId);
			user.setGender(gender);
			user.setName(name);
			user.setPassword(password);
			user.setPhone(mobile);
			new SignInAsyncTask().execute(user.getPhone(), user.getPassword());
		}
		else
		{
			setContentView(R.layout.activity_main);
		}
		

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
		String password = passwordField.getText().toString();

		if (gender.equals("male")) {
			user.setGender(Gender.M.toString());
		} else {
			user.setGender(Gender.F.toString());
		}

		user.setName(name);
		user.setPhone(phone);
		user.setPassword(password);
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

		user.setDeviceId(telephonyManager.getDeviceId());

		new RegisterAsyncTask().execute(user);
	}

	private Integer registerDevice(User user) throws ClientProtocolException,
			IOException {
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(Constants.URL_REGISTER);

		// Add your data
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
		nameValuePairs.add(new BasicNameValuePair("mobile", user.getPhone()));
		nameValuePairs.add(new BasicNameValuePair("dev", user.getDeviceId()));
		nameValuePairs.add(new BasicNameValuePair("name", user.getName()));
		nameValuePairs.add(new BasicNameValuePair("gender", user.getGender()));
		nameValuePairs.add(new BasicNameValuePair("password", user
				.getPassword()));
		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		// Execute HTTP Post Request
		HttpResponse response = httpclient.execute(httppost);
		int status = new Integer(response.getFirstHeader("status").getValue());

		logger.info("staus : " + status);
		return status;

	}

	private void showToastMessage(String msg) {
		Context context = getApplicationContext();
		String text = getResources().getString(R.string.error_msg_register);
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}

	public void showSignIn(View view) {
		Intent i = new Intent(this, LoginActivity.class);
		startActivity(i);
		i.putExtra("Value2", "This value two ActivityTwo");
	}

	private class RegisterAsyncTask extends AsyncTask<User, Integer, Integer> {

		@Override
		protected Integer doInBackground(User... user) {
			// TODO Auto-generated method stub
			try {
				return postData(user[0]);
			} catch (ClientProtocolException e) {

				String text = getResources().getString(
						R.string.error_msg_register);
				showToastMessage(text);
				e.printStackTrace();

			} catch (IOException e) {
				String text = getResources().getString(
						R.string.error_msg_register);
				showToastMessage(text);
				e.printStackTrace();
			}
			return 0;
		}

		protected void onPostExecute(Integer status) {

			if (status == Constants.USER_SUCCESS) {
				// registration success full

				// We need an Editor object to make preference changes.
				// All objects are from android.context.Context
				SharedPreferences settings = getSharedPreferences(
						userPreference, 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("name", user.getName());
				editor.putString("password", user.getPassword());
				editor.putString("mobile", user.getPhone());
				editor.putString("gender", user.getGender());
				editor.putString("deviceId", user.getDeviceId());
				// Commit the edits!
				editor.commit();

				showFindPartnerActivity(user);

			} else if (status == Constants.USER_ALREADY_EXIST) {
				String text = getResources().getString(
						R.string.error_msg_alreadyRegistered);
				showToastMessage(text);

			} else {
				String text = getResources().getString(
						R.string.error_msg_register);
				showToastMessage(text);

			}

		}

		protected void onProgressUpdate(Integer... progress) {
			// pb.setProgress(progress[0]);
		}

		public Integer postData(User user) throws ClientProtocolException,
				IOException {
			return registerDevice(user);
		}

	}

	public void showFindPartnerActivity(User user) {
		Intent i = new Intent(this, FindPartnerActivity.class);
		i.putExtra("user", user);
		startActivity(i);
		

	}

	public class SignInAsyncTask extends AsyncTask<String, User, User> {

		@Override
		protected User doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				return postData(params[0], params[1]);
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

		protected void onPostExecute(User status) {

			if (status.getStatus() == Constants.USER_SUCCESS) {
				showFindPartnerActivity(user);

			} else if (user.getStatus() == Constants.USER_ALREADY_EXIST) {
				// user name / password is wrong
				String text = getResources().getString(
						R.string.error_msg_incorrect_credential);
				showToastMessage(text);
			} else {
				/*String text = getResources()
						.getString(R.string.error_msg_sigIn);
				showToastMessage(text);*/
				

			}

		}

		protected void onProgressUpdate(Integer... progress) {
			// pb.setProgress(progress[0]);
		}

		public User postData(String mobile, String password)
				throws ClientProtocolException, IOException {
			
			
			return signIn(mobile, password);
		}

	}
	
	
	public  User signIn(String mobile, String password)
			throws ClientProtocolException, IOException {
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(Constants.URL_LOGIN);

		// Add your data
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
		nameValuePairs.add(new BasicNameValuePair("mobile", mobile));

		nameValuePairs.add(new BasicNameValuePair("password", password));
		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		// Execute HTTP Post Request
		HttpResponse response = httpclient.execute(httppost);
		int status = new Integer(response.getFirstHeader("status").getValue());
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

/*		user.setDeviceId(telephonyManager.getDeviceId());
		user.setName(response.getFirstHeader("name").getValue());
		user.setGender(response.getFirstHeader("gender").getValue());*/
		user.setStatus(status);
		/*user.setPassword(password);
		user.setPhone(mobile);*/
		// logger.info("status :"+status);
		return user;

	}

}
