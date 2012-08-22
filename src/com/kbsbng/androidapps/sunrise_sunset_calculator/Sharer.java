package com.kbsbng.androidapps.sunrise_sunset_calculator;

import android.content.Intent;
import android.view.MenuItem;

public class Sharer {
    public static boolean share(final MenuItem item) {
    	Intent intent = new Intent(Intent.ACTION_SEND);
    	intent.setType("text/plain");
    	intent.putExtra(Intent.EXTRA_TEXT, "Check out the android app Sunrise Sunset calculator http://www.androidzoom.com/android_applications/weather/sunrise-sunset-calculator_crjui.html");
    	SelectedLocation.activity.startActivity(Intent.createChooser(intent, "Share with"));
    	return true;
    }
}
