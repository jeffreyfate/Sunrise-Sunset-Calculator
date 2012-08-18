package com.kbsbng.androidapps.sunrise_sunset_calculator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;

class GpsOrNetworkPrompt {
	public GpsOrNetworkPrompt(final Activity activity) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		final String action;
		final String message;
		if (isAirplaneModeOn(activity.getApplicationContext())) {
			message = "Your current location cannot be found if flight mode is enabled.  Click OK to go to flight mode settings to disable it.";
			action = Settings.ACTION_AIRPLANE_MODE_SETTINGS;
		} else {
			message = "Enable either GPS or any other location service to find current location.  Click OK to go to location services settings to let you do so.";
			action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
		}

		builder.setMessage(message)
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						activity.startActivity(new Intent(action));
						dialog.dismiss();
					}
				})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		builder.create().show();
	}

	private static boolean isAirplaneModeOn(Context context) {

		return Settings.System.getInt(context.getContentResolver(),
				Settings.System.AIRPLANE_MODE_ON, 0) != 0;

	}
}
