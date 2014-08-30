/*package com.example.qpooltaxi;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import android.os.AsyncTask;

import com.example.common.Constants;
import com.example.model.User;

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
			showFindPartnerActivity(null);

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
		LoginActivity la=new LoginActivity();
		
		return la.signIn(mobile, password);
	}

}*/