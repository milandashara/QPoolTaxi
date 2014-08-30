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
import android.widget.Toast;

import com.example.common.Constants;
import com.example.model.User;

public class LoginActivity extends Activity {

	Logger logger = Logger.getLogger("LoginActivity");
	private static LoginActivity loginActivity;
	private String userPreference = "loginCredential";
	private static User user = new User();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		loginActivity = this;

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
		} else {
			setContentView(R.layout.activity_login);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
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

	public void signIn(View button) {
		final EditText phoneField = (EditText) findViewById(R.id.EditLoginPhone);
		final String phone = phoneField.getText().toString();

		final EditText passwordField = (EditText) findViewById(R.id.EditLoginPassword);
		final String password = passwordField.getText().toString();
		/*
		 * Thread thread = new Thread(new Runnable() {
		 * 
		 * @Override public void run() { try { signIn(phone, password); } catch
		 * (Exception e) { e.printStackTrace(); } } });
		 * 
		 * thread.start();
		 */

		new SignInAsyncTask().execute(phone, password);

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

		user.setDeviceId(telephonyManager.getDeviceId());
		user.setName(response.getFirstHeader("name").getValue());
		user.setGender(response.getFirstHeader("gender").getValue());
		user.setStatus(status);
		user.setPassword(password);
		user.setPhone(mobile);
		// logger.info("status :"+status);
		return user;

	}

	private void showToastMessage(String msg) {
		Context context = getApplicationContext();
		// String text = getResources().getString(R.string.error_msg_sigIn);
		int duration = Toast.LENGTH_LONG;

		Toast toast = Toast.makeText(context, msg, duration);
		toast.show();
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

		protected void onPostExecute(User user) {

			if (user.getStatus() == Constants.USER_SUCCESS) {
				
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

			} else if (user.getStatus() == Constants.USER_ALREADY_EXIST) {
				// user name / password is wrong
				String text = getResources().getString(
						R.string.error_msg_incorrect_credential);
				showToastMessage(text);
			} else {
				String text = getResources()
						.getString(R.string.error_msg_sigIn);
				showToastMessage(text);

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

	public void showFindPartnerActivity(User user) {
		Intent i = new Intent(this, FindPartnerActivity.class);
		i.putExtra("user", user);
		startActivity(i);
		
	}

}
