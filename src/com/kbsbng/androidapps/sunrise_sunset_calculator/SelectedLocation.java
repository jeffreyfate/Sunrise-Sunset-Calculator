package com.kbsbng.androidapps.sunrise_sunset_calculator;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.util.Log;

/**
 * All methods and variables in this class are static since we want to use this
 * class across the activities of the application without having to do any
 * serialization or parceling.
 */
public class SelectedLocation {

	static double longitude;
	static double latitude;
	static Activity activity;

	static volatile TimeZone timezone;
	static volatile Address address;
	
	static final String ADDRESS_DELIMITER = ", ";

	static void updateDetails(final double lon, final double lat) {
		longitude = lon;
		latitude = lat;
		
		timezone = null;
		address = null;

		if (isNetworkAvailable()) {
			new Thread(new Runnable() {
				public void run() {
					findLocation();
				}
			}).start();
		}
		
		new Thread(new Runnable() {
			public void run() {
				timezone = getTimeZone();
			}
		}).start();
	}
	
	private static TimeZone computeTimeZoneFromLatLong() {
		double longitudeHours = (longitude / 15);
		String sym;
		if (longitudeHours >= 0) {
			sym = "+";
		} else {
			sym = "-";
			longitudeHours *= (-1);
		}
		double gmtOffsetHours = Math.round(longitudeHours * 2) / 2.0;
		String gmtOffsetMinStr;
		if (gmtOffsetHours % 1 == 0) {
			gmtOffsetMinStr = "00";
		} else {
			gmtOffsetMinStr = "30";
		}
		String gmtOffsetHoursStr;
		gmtOffsetHours = Math.floor(gmtOffsetHours);
		if (gmtOffsetHours < 10) {
			gmtOffsetHoursStr = "0" + (int) gmtOffsetHours;
		} else {
			gmtOffsetHoursStr = String.valueOf((int) gmtOffsetHours);
		}
		Log.e("test", "GMT" + sym + gmtOffsetHoursStr + ":" + gmtOffsetMinStr);
		return TimeZone.getTimeZone("GMT" + sym + gmtOffsetHoursStr + ":"
				+ gmtOffsetMinStr);
	}
	
	private static String readStream(InputStream is) {
		try {
			ByteArrayOutputStream bo = new ByteArrayOutputStream();

			int i = is.read();
			while (i != -1) {
				bo.write(i);
				i = is.read();
			}
			return bo.toString();
		} catch (IOException e) {
			return "{}";
		}
	}
	
	private static TimeZone getTimeZone() {
		if (!isNetworkAvailable()) {
			return computeTimeZoneFromLatLong();
		}
		try {
			URL url = new URL(activity.getString(R.string.askGeoTimeZoneApi)
					+ URLEncoder.encode(latitude + "," + longitude, "UTF-8"));
			HttpURLConnection urlConnection = null;
			try {
				urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setReadTimeout(5000);
				urlConnection.setDoInput(true);
				urlConnection.connect();
				InputStream in = new BufferedInputStream(
						urlConnection.getInputStream());
				JSONObject askGeoResponse = new JSONObject(readStream(in));
				String timezoneId = askGeoResponse.getJSONArray("data")
						.getJSONObject(0).getJSONObject("TimeZone")
						.getString("TimeZoneId");
				return TimeZone.getTimeZone(timezoneId);
			} catch (IOException e) {
				Log.e("getTimeZone", "io exception", e);
			} catch (JSONException e) {
				Log.e("getTimeZone", "json exception", e);
			} finally {
				if (urlConnection != null) {
					urlConnection.disconnect();
				}
			}
		} catch (MalformedURLException e) {
			Log.e("getTimeZone", "malformed url", e);
		} catch (UnsupportedEncodingException e) {
			Log.e("getTimeZone", "unsupported encoding", e);
		}

		return computeTimeZoneFromLatLong();
	}

	private static void findLocation() {
		final Geocoder geocoder = new Geocoder(activity.getApplicationContext());
		final List<Address> fromLocations;
		try {
			fromLocations = geocoder.getFromLocation(latitude, longitude, 1);
		} catch (IOException e) {
			Log.e(DisplaySunCalculationResults.class.getName(), "got io error",
					e);
			return;
		}
		if (fromLocations != null && fromLocations.size() != 0) {
			address = fromLocations.get(0);
		}
	}

	private static boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) activity
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		return connectivityManager.getActiveNetworkInfo() != null;
	}

	public static String getAddress() {
		final StringBuilder addressStringBuilder = new StringBuilder(50);
		String str;
		/*
		for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
		     addressStringBuilder.append(address.getAddressLine(i)).append(ADDRESS_DELIMITER);
		}
		return addressStringBuilder.toString();
		*/

		if ((str = address.getPremises()) != null) {
			addressStringBuilder.append(str).append(ADDRESS_DELIMITER);
		}
		if ((str=address.getSubThoroughfare()) != null) {
			addressStringBuilder.append(str).append(ADDRESS_DELIMITER);
		}
		if ((str = address.getThoroughfare()) != null) {
			addressStringBuilder.append(str).append(ADDRESS_DELIMITER);
		}
		if ((str = address.getSubLocality()) != null) {
			addressStringBuilder.append(str).append(ADDRESS_DELIMITER);
		}
		if ((str = address.getLocality()) != null) {
			addressStringBuilder.append(str).append(ADDRESS_DELIMITER);
		}
		if ((str = address.getSubAdminArea()) != null) {
			addressStringBuilder.append(str).append(ADDRESS_DELIMITER);
		}
		if ((str = address.getAdminArea()) != null) {
			addressStringBuilder.append(str).append(ADDRESS_DELIMITER);
		}
		if ((str = address.getPostalCode()) != null) {
			addressStringBuilder.append(str).append(ADDRESS_DELIMITER);
		}
		if ((str = address.getCountryName()) != null) {
			addressStringBuilder.append(str);
		}
		return addressStringBuilder.toString();
	}
}
