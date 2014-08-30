package com.example.common;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;

public class Utility {

	public static void setActivityEnabled(Context context,
			final Class<? extends Activity> activityClass, final boolean enable) {
		final PackageManager pm = context.getPackageManager();
		final int enableFlag = enable ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
				: PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
		pm.setComponentEnabledSetting(
				new ComponentName(context, activityClass), enableFlag,
				PackageManager.DONT_KILL_APP);
	}

}
