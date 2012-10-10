package com.kbsbng.androidapps.sunrise_sunset_calculator;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

public class CurrentLocationSunriseSunsetCalcution extends FragmentActivity {
	private static final int POINT_ON_MAP_ACTIVITY = 2;
	private EditText dateField;
	private EditText locationField;
	private AdView adView;

	int year;
	int month;
	int day;
	private LocationManager locationManager;
	private LocationListener locationListener;
	private Location loc;

	int chosenLongitude;
	int chosenLatitude;

	enum LocationSource {
		MY_CURRENT_LOCATION, POINT_ON_MAP
	}

	private LocationSource locationSource = LocationSource.MY_CURRENT_LOCATION;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SelectedLocation.activity = this;

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		setContentView(R.layout.activity_current_location_sunrise_sunset_calculation);
		dateField = (EditText) findViewById(R.id.dateField);
		locationField = (EditText) findViewById(R.id.locationText);
		
		final Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		updateDate();

		dateField.setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				if (event.getActionMasked() == MotionEvent.ACTION_UP) {
					showDatePicker();
				}
				return true;
			}
		});
		
		locationField.setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				if (event.getActionMasked() == MotionEvent.ACTION_UP) {
					showLocationPicker();
				}
				return true;
			}
		});

		locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				loc = location;
				locationManager.removeUpdates(locationListener);
				SelectedLocation.updateDetails(loc.getLongitude(),
						loc.getLatitude());
			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onProviderDisabled(String provider) {
			}
		};

		requestForLocation();

		adView = (AdView) this.findViewById(R.id.mainAdView);
		adView.loadAd(new AdRequest());
	}

	private void requestForLocation() {
		String provider;
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			provider = LocationManager.GPS_PROVIDER;
		} else if (locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			provider = LocationManager.NETWORK_PROVIDER;
		} else {
			provider = LocationManager.PASSIVE_PROVIDER;
		}
		locationManager
				.requestLocationUpdates(provider, 0, 0, locationListener);
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(R.menu.common, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case POINT_ON_MAP_ACTIVITY:
			if (resultCode != RESULT_OK) {
				break;
			}
			chosenLatitude = data.getExtras().getInt("latitude");
			chosenLongitude = data.getExtras().getInt("longitude");

			SelectedLocation.updateDetails(chosenLongitude / 1E6,
					chosenLatitude / 1E6);
			break;
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		default:
			return Sharer.share(item);
		}
	}

	public void showDatePicker() {
		final DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getSupportFragmentManager(), "datePicker");
	}

	public void showLocationPicker() {
		final DialogFragment newFragment = LocationTypeDialog.newInstance();
		newFragment.show(getSupportFragmentManager(), "locationPicker");
	}

	private boolean findCurrentLocation() {
		if (loc != null) {
			return true;
		}
		if (loc == null) {
			loc = locationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		}
		if (loc == null) {
			loc = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		if (loc == null) {
			loc = locationManager
					.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
		}
		if (loc == null) {
			Log.e("findCurrentLocation", "loc is null");
			new GpsOrNetworkPrompt(this);
			requestForLocation();
		}
		if (loc != null) {
			SelectedLocation.updateDetails(loc.getLongitude(),
					loc.getLatitude());
			return true;
		}
		return false;
	}

	public void calculateSunriseSunset(View v) {
		Intent intent = new Intent(getApplicationContext(),
				DisplaySunCalculationResults.class);

		if (locationSource == LocationSource.MY_CURRENT_LOCATION
				&& !findCurrentLocation()) {
			return;
		}

		intent.putExtra("day", day);
		intent.putExtra("month", month);
		intent.putExtra("year", year);
		startActivity(intent);
	}

	void handleCurrectLocationChoice() {
		locationSource = LocationSource.MY_CURRENT_LOCATION;
		locationField.setText("My Current Location");
		loc = null;
		requestForLocation();
		View focusSearch = locationField.focusSearch(View.FOCUS_DOWN);
		if (focusSearch != null) {
			focusSearch.requestFocus();
		}
	}

	void handlePointOnMapChoice() {
		locationManager.removeUpdates(locationListener);

		locationSource = LocationSource.POINT_ON_MAP;
		locationField.setText("Point on map");
		Intent intent = new Intent(getApplicationContext(),
				ChooseLocationFromMap.class);
		startActivityForResult(intent, POINT_ON_MAP_ACTIVITY);
	}

	void handleDateSelection(int y, int m, int d) {
		year = y;
		month = m;
		day = d;
		updateDate();
		dateField.clearFocus();
		View focusSearch = dateField.focusSearch(View.FOCUS_DOWN);
		if (focusSearch != null) {
			focusSearch.requestFocus();
		}
	}

	private void updateDate() {
		SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy");
		final Calendar c = Calendar.getInstance();
		c.set(year, month, day);
		dateField.setText(format.format(c.getTime()));
	}
	
}
