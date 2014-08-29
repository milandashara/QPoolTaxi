package com.example.qpooltaxi;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

import com.example.common.Constants;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

	Logger logger = Logger.getLogger("LoginActivity");
	private static LoginActivity loginActivity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		loginActivity=this;
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
		/*Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					signIn(phone, password);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		thread.start();*/
		
		new SignInAsyncTask().execute(phone,password);	
		
	}

	public Integer signIn(String mobile, String password) throws ClientProtocolException, IOException {
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(Constants.URL_LOGIN);


			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
			nameValuePairs
					.add(new BasicNameValuePair("mobile", mobile));
			
			nameValuePairs.add(new BasicNameValuePair("password", password));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);
			int status = new Integer(response.getFirstHeader("status").getValue());
			
			logger.info("status :"+status);
			return status;

		
	}
	
	private void showToastMessage(String msg) {
		Context context = getApplicationContext();
		//String text = getResources().getString(R.string.error_msg_sigIn);
		int duration = Toast.LENGTH_LONG;

		Toast toast = Toast.makeText(context, msg, duration);
		toast.show();
	}
	
	
	private class SignInAsyncTask extends AsyncTask<String, Integer, Integer>{
		 
		@Override
		protected Integer doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				return postData(params[0],params[1]);
			} catch (ClientProtocolException e) {
				String text = getResources().getString(
						R.string.error_msg_sigIn);
				showToastMessage(text);
			} catch (IOException e) {
				String text = getResources().getString(
						R.string.error_msg_sigIn);
				showToastMessage(text);
			}
			return 0;
		}
 
		protected void onPostExecute(Integer status){
			
			
			if (status == Constants.USER_SUCCESS) {
				// sign in  success full

			} else if (status == Constants.USER_ALREADY_EXIST) {
				//user name / password is wrong
				String text = getResources().getString(
						R.string.error_msg_incorrect_credential);
				showToastMessage(text);
			} else {
				String text = getResources().getString(
						R.string.error_msg_sigIn);
				showToastMessage(text);

			}
			
			
		}
		protected void onProgressUpdate(Integer... progress){
	//		pb.setProgress(progress[0]);
		}
 
		public Integer  postData(String mobile, String password) throws ClientProtocolException, IOException {
			return signIn(mobile, password);
		}
		
		
 
	}
	
	
	
	
	
	
}
