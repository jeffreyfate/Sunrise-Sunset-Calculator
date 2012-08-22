package com.kbsbng.androidapps.sunrise_sunset_calculator;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.kbsbng.utils.SunriseSunsetCalculation;

public class DisplaySunCalculationResults extends Activity {

	private int year;
	private int month;
	private int day;

	private SunriseSunsetCalculation sunriseSunsetCalculation;
	private TextView sunriseTime;
	private TextView sunsetTime;
	private TextView latitudeText;
	private TextView longitudeText;
	private TextView dawnText;
	private TextView duskText;
	private TextView nauticalDawnText;
	private TextView nauticalDuskText;
	private TextView astroDawnText;
	private TextView astroDuskText;
	private TextView timeZoneText;
	private TextView countryText;
	private ProgressDialog progressDialog;
	private TimeZoneHandler timeZoneHandler;
	private AdView adView;

	static class TimeZoneHandler extends Handler {

		private final WeakReference<DisplaySunCalculationResults> displaySunCalculationResults;

		TimeZoneHandler(
				DisplaySunCalculationResults displaySunCalculationResults) {
			this.displaySunCalculationResults = new WeakReference<DisplaySunCalculationResults>(
					displaySunCalculationResults);
		}

		@Override
		public void handleMessage(final Message msg) {
			final DisplaySunCalculationResults displaySunCalculationResults = this.displaySunCalculationResults
					.get();
			final DateFormat dateFormat = new SimpleDateFormat("hh:mm a zzz");
			if (SelectedLocation.timezone != null) {
				dateFormat.setTimeZone(SelectedLocation.timezone);
			}

			displaySunCalculationResults.populateTimes(dateFormat);

			displaySunCalculationResults.progressDialog.dismiss();
			displaySunCalculationResults.showLocationGeoDetails();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		year = extras.getInt("year");
		month = extras.getInt("month");
		day = extras.getInt("day");
		sunriseSunsetCalculation = new SunriseSunsetCalculation(day, month,
				year, SelectedLocation.longitude, SelectedLocation.latitude);
		sunriseSunsetCalculation.calculateOfficialSunriseSunset();
		sunriseSunsetCalculation.calculateCivilSunriseSunset();
		sunriseSunsetCalculation.calculateNauticalSunriseSunset();
		sunriseSunsetCalculation.calculateAstroSunriseSunset();

		setContentView(R.layout.activity_display_sun_calculation_results);
		progressDialog = new ProgressDialog(DisplaySunCalculationResults.this);
		progressDialog.setCancelable(false);
		progressDialog.setTitle("Loading");
		progressDialog.setMessage("Please wait...");
		progressDialog.show();

		sunriseTime = (TextView) findViewById(R.id.sunriseTime);
		sunsetTime = (TextView) findViewById(R.id.sunsetTime);
		dawnText = (TextView) findViewById(R.id.dawnTime);
		duskText = (TextView) findViewById(R.id.duskTime);
		nauticalDawnText = (TextView) findViewById(R.id.nauticalDawnTime);
		nauticalDuskText = (TextView) findViewById(R.id.nauticalDuskTime);
		astroDawnText = (TextView) findViewById(R.id.astroDawnTime);
		astroDuskText = (TextView) findViewById(R.id.astroDuskTime);
		latitudeText = (TextView) findViewById(R.id.latitude);
		longitudeText = (TextView) findViewById(R.id.longitude);
		timeZoneText = (TextView) findViewById(R.id.timeZoneText);
		countryText = (TextView) findViewById(R.id.country);

		timeZoneHandler = new TimeZoneHandler(this);
		new Thread(new Runnable() {
			public void run() {
				while (SelectedLocation.timezone == null) {
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {
						break;
					}
				}
				timeZoneHandler.sendMessage(new Message());
			}
		}).start();

		showLocationGeoDetails();

		latitudeText.setText(Double.valueOf(SelectedLocation.latitude)
				.toString());
		longitudeText.setText(Double.valueOf(SelectedLocation.longitude)
				.toString());

		adView = (AdView) this.findViewById(R.id.displayResultAdView);
		adView.loadAd(new AdRequest());

		RelativeLayout layout = (RelativeLayout) findViewById(R.id.resultDisplayLayout);
		final int layoutChildrenCount = layout.getChildCount();
		final String helpString = getString(R.string.help);

		final OnTouchListener helpIconTouchListener = new View.OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				if (event.getActionMasked() == MotionEvent.ACTION_UP) {
					handleHelpIconClick(v);
				}
				return true;
			}
		};

		for (int i = 0; i < layoutChildrenCount; i++) {
			View child = layout.getChildAt(i);
			if (child instanceof ImageView) {
				ImageView imageView = (ImageView) child;
				if (imageView.getContentDescription().equals(helpString)) {
					imageView.setOnTouchListener(helpIconTouchListener);
				}
			}
		}
	}

	private void showLocationGeoDetails() {
		if (SelectedLocation.address != null) {
			countryText.setText(SelectedLocation.getAddress());
		} else {
			countryText.setText(R.string.not_able_to_find);
		}
	}

	private void populateTimes(final DateFormat dateFormat) {
		final boolean doesSunRise = sunriseSunsetCalculation.doesSunRise();
		final boolean doesSunSet = sunriseSunsetCalculation.doesSunSet();
		if (doesSunRise && doesSunSet) {
			sunriseTime.setText(dateFormat.format(sunriseSunsetCalculation
					.getOfficialSunrise()));
			sunsetTime.setText(dateFormat.format(sunriseSunsetCalculation
					.getOfficialSunset()));
		} else if (!doesSunRise) {
			sunriseTime.setText(R.string.no_sun_rise_on_given_date);
			sunsetTime.setText(R.string.no_sun_rise_on_given_date);
		} else if (!doesSunSet) {
			sunriseTime.setText(R.string.no_sunset_on_given_date);
			sunsetTime.setText(R.string.no_sunset_on_given_date);
		}
		final boolean doesSunDawn = sunriseSunsetCalculation.doesSunDawn();
		final boolean doesSunDusk = sunriseSunsetCalculation.doesSunDusk();
		if (doesSunDawn && doesSunDusk) {
			dawnText.setText(dateFormat.format(sunriseSunsetCalculation
					.getCivilSunrise()));
			duskText.setText(dateFormat.format(sunriseSunsetCalculation
					.getCivilSunset()));
		} else if (!doesSunDawn) {
			dawnText.setText(R.string.no_dawn_on_given_date);
			duskText.setText(R.string.no_dawn_on_given_date);
		} else if (!doesSunDusk) {
			dawnText.setText(R.string.no_dusk_on_given_date);
			duskText.setText(R.string.no_dusk_on_given_date);
		}

		final boolean doesSunNauticalDawn = sunriseSunsetCalculation
				.doesSunNauticalDawn();
		final boolean doesSunNauticalDusk = sunriseSunsetCalculation
				.doesSunNauticalDusk();
		if (doesSunNauticalDawn && doesSunNauticalDusk) {
			nauticalDawnText.setText(dateFormat.format(sunriseSunsetCalculation
					.getNauticalSunrise()));
			nauticalDuskText.setText(dateFormat.format(sunriseSunsetCalculation
					.getNauticalSunset()));
		} else if (!doesSunNauticalDawn) {
			nauticalDawnText.setText(R.string.no_nautical_dawn_on_given_date);
			nauticalDuskText.setText(R.string.no_nautical_dawn_on_given_date);
		} else if (!doesSunNauticalDusk) {
			nauticalDawnText.setText(R.string.no_nautical_dusk_on_given_date);
			nauticalDuskText.setText(R.string.no_nautical_dusk_on_given_date);
		}

		final boolean doesSunAstroDawn = sunriseSunsetCalculation
				.doesSunAstroDawn();
		final boolean doesSunAstroDusk = sunriseSunsetCalculation
				.doesSunAstroDusk();
		if (doesSunAstroDawn && doesSunAstroDusk) {
			astroDawnText.setText(dateFormat.format(sunriseSunsetCalculation
					.getAstroSunrise()));
			astroDuskText.setText(dateFormat.format(sunriseSunsetCalculation
					.getAstroSunset()));
		} else if (!doesSunAstroDawn) {
			astroDawnText.setText(R.string.no_astro_dawn_on_given_date);
			astroDuskText.setText(R.string.no_astro_dawn_on_given_date);
		} else if (!doesSunAstroDusk) {
			astroDawnText.setText(R.string.no_astro_dusk_on_given_date);
			astroDuskText.setText(R.string.no_astro_dusk_on_given_date);
		}

		timeZoneText.setText(dateFormat.getTimeZone().getID());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.common, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		default:
			return Sharer.share(item);
		}
	}

	public void handleBackClick(final View view) {
		finish();
	}

	public void handleHelpIconClick(final View view) {
		Intent intent = new Intent(getApplicationContext(),
				DisplayHelpActivity.class);
		intent.putExtra("helpIconId", view.getId());
		startActivity(intent);
	}

}
